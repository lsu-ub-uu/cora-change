package se.uu.ub.cora.change.ocfl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

public class OcflProcessor {
	private static final Logger LOGGER = Logger.getLogger(OcflProcessor.class.getName());

	// public static void main(String[] args) {
	public static void addFedoraFilesToOcflStructure(String basePath, String options) {
		// if (args.length < 1) {
		// System.out.println("Usage: java OcflProcessor <base_path> [--dry-run]");
		// return;
		// }
		// String basePath = args[0];
		boolean dryRun = options.equals("--dry-run");
		LOGGER.info("Start Adding Fedora Files in OCFL object at : " + basePath);

		// setupLogging();
		// List<File> ocflObjects = findOcflObjects(new File(basePath));
		File rootFolderForObject = new File(basePath);
		// LOGGER.info("Files num: " + ocflObjects.size());
		// for (File obj : ocflObjects) {
		LOGGER.info("Processing OCFL object: " + rootFolderForObject.getAbsolutePath());
		List<JSONObject> metadataList = createFcrepoCatalog(rootFolderForObject, dryRun);
		updateInventory(rootFolderForObject, metadataList, dryRun);
		copyInventoryFiles(rootFolderForObject, dryRun);
		// }
	}

	// private static void setupLogging() {
	// LOGGER.setLevel(Level.SEVERE);
	// }

	private static List<File> findOcflObjects(File basePath) {
		List<File> ocflObjects = new ArrayList<>();
		File[] files = basePath.listFiles();
		// LOGGER.info("Files: " + files.);
		if (files != null) {
			for (File file : files) {
				if (new File(file, "v1").exists() && new File(file, "inventory.json").exists()) {
					ocflObjects.add(file);
				}
			}
		}
		return ocflObjects;
	}

	private static void copyInventoryFiles(File ocflObjectPath, boolean dryRun) {
		File inventoryFile = new File(ocflObjectPath, "inventory.json");
		File inventoryShaFile = new File(ocflObjectPath, "inventory.json.sha512");
		File v1Path = new File(ocflObjectPath, "v1");

		if (dryRun) {
			LOGGER.info("[Dry Run] Would copy inventory files to " + v1Path.getAbsolutePath());
		} else {
			try {
				Files.copy(inventoryFile.toPath(), new File(v1Path, "inventory.json").toPath(),
						StandardCopyOption.REPLACE_EXISTING);
				Files.copy(inventoryShaFile.toPath(),
						new File(v1Path, "inventory.json.sha512").toPath(),
						StandardCopyOption.REPLACE_EXISTING);
				LOGGER.info("Copied inventory files to " + v1Path.getAbsolutePath());
			} catch (IOException e) {
				LOGGER.severe("Error copying inventory files: " + e.getMessage());
			}
		}
	}

	private static List<JSONObject> createFcrepoCatalog(File ocflObjectPath, boolean dryRun) {
		File v1ContentPath = new File(ocflObjectPath, "v1/content");
		File fcrepoPath = new File(v1ContentPath, ".fcrepo");

		if (dryRun) {
			LOGGER.info("[Dry Run] Would create directory: " + fcrepoPath.getAbsolutePath());
		} else {
			fcrepoPath.mkdirs();
		}

		File[] files = v1ContentPath.listFiles((dir, name) -> new File(dir, name).isFile());
		if (files == null || files.length == 0) {
			LOGGER.warning("No files found in " + v1ContentPath.getAbsolutePath() + ", skipping.");
			return Collections.emptyList();
		}

		File filename = files[0];
		String createdDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
				.format(new Date());
		String stateToken = UUID.randomUUID().toString().toUpperCase();

		String fileHash = sha512Checksum(filename);
		long fileSize = filename.length();

		JSONObject fcrRootMetadata = new JSONObject();
		fcrRootMetadata.put("id", "info:fedora/" + filename.getName());
		fcrRootMetadata.put("parent", "info:fedora");
		fcrRootMetadata.put("stateToken", stateToken);
		fcrRootMetadata.put("interactionModel", "http://www.w3.org/ns/ldp#NonRDFSource");
		fcrRootMetadata.put("mimeType", "application/octet-stream");
		fcrRootMetadata.put("filename", "");
		fcrRootMetadata.put("contentSize", fileSize);
		fcrRootMetadata.put("digests", Collections.singletonList("urn:sha-512:" + fileHash));
		fcrRootMetadata.put("createdDate", createdDate);
		fcrRootMetadata.put("lastModifiedDate", createdDate);
		fcrRootMetadata.put("mementoCreatedDate", createdDate);
		fcrRootMetadata.put("archivalGroup", false);
		fcrRootMetadata.put("objectRoot", true);
		fcrRootMetadata.put("deleted", false);
		fcrRootMetadata.put("contentPath", filename.getName());
		fcrRootMetadata.put("headersVersion", "1.0");

		JSONObject fcrDescMetadata = new JSONObject();
		fcrDescMetadata.put("id", "info:fedora/" + filename.getName() + "/fcr:metadata");
		fcrDescMetadata.put("parent", "info:fedora/" + filename.getName());
		fcrDescMetadata.put("stateToken", UUID.randomUUID().toString().toUpperCase());

		if (!dryRun) {
			try {
				Files.write(new File(fcrepoPath, "fcr-root.json").toPath(),
						fcrRootMetadata.toString(4).getBytes());
				Files.write(new File(fcrepoPath, "fcr-root~fcr-desc.json").toPath(),
						fcrDescMetadata.toString(4).getBytes());
				new File(v1ContentPath, filename.getName() + "~fcr-desc.nt").createNewFile();
			} catch (IOException e) {
				LOGGER.severe("Error writing fcrepo files: " + e.getMessage());
			}
		}
		return Arrays.asList(fcrRootMetadata, fcrDescMetadata);
	}

	private static void updateInventory(File ocflObjectPath, List<JSONObject> metadataList,
			boolean dryRun) {
		if (metadataList.isEmpty())
			return;

		File inventoryPath = new File(ocflObjectPath, "inventory.json");
		try {
			String content = new String(Files.readAllBytes(inventoryPath.toPath()));
			JSONObject inventory = new JSONObject(content);

			for (JSONObject metadata : metadataList) {
				JSONArray digests = metadata.getJSONArray("digests");
				for (Object digest : digests) {
					String digestStr = digest.toString().replace("urn:sha-512:", "");
					inventory.getJSONObject("manifest").put(digestStr,
							Collections.singletonList("v1/content/" + metadata.getString("id")));
				}
			}

			if (dryRun) {
				LOGGER.info("[Dry Run] Would update " + inventoryPath.getAbsolutePath());
			} else {
				Files.write(inventoryPath.toPath(), inventory.toString(4).getBytes());
				LOGGER.info("Updated " + inventoryPath.getAbsolutePath());
			}
		} catch (IOException e) {
			LOGGER.severe("Error updating inventory: " + e.getMessage());
		}
	}

	private static String sha512Checksum(File file) {
		try (InputStream fis = new FileInputStream(file)) {
			MessageDigest digest = MessageDigest.getInstance("SHA-512");
			byte[] buffer = new byte[8192];
			int bytesRead;
			while ((bytesRead = fis.read(buffer)) != -1) {
				digest.update(buffer, 0, bytesRead);
			}
			return bytesToHex(digest.digest());
		} catch (Exception e) {
			LOGGER.severe("Error computing SHA-512: " + e.getMessage());
			return "";
		}
	}

	private static String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}
}
