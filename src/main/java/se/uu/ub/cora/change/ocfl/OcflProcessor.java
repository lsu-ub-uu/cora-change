package se.uu.ub.cora.change.ocfl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import io.ocfl.api.model.ObjectVersionId;
import io.ocfl.api.model.VersionInfo;
import io.ocfl.core.OcflRepositoryBuilder;
import io.ocfl.core.extension.storage.layout.config.HashedNTupleLayoutConfig;
import se.uu.ub.cora.storage.archive.ArchivePathBuilder;

public class OcflProcessor {
	private static final String INVENTORY_JSON_SHA512 = "inventory.json.sha512";
	private static final String INVENTORY_JSON = "inventory.json";
	private static final String V1_CONTENT = "v1/content/";
	private static final String BINARY = "binary";

	private final Logger LOGGER = Logger.getLogger(OcflProcessor.class.getName());

	private String pathToOcflRepo;
	private ArchivePathBuilder archivePathBuilderImp;
	private String pathToOcflWork;

	public OcflProcessor(String pathToOcflRepo, String pathToOcflWork,
			ArchivePathBuilder archivePathBuilderImp) {
		this.pathToOcflRepo = pathToOcflRepo;
		this.pathToOcflWork = pathToOcflWork;
		this.archivePathBuilderImp = archivePathBuilderImp;
	}

	public void addBinaryToFedoraRepo(String pathToFileToMoveIntoFedora, String dataDivider,
			String recordId) {
		String pathToObjectFiles = createOcflFileStructure(pathToFileToMoveIntoFedora, dataDivider,
				recordId);
		addFedoraMetadataFilesToOCFL(pathToObjectFiles);
	}

	private String createOcflFileStructure(String pathToFileToMoveIntoFedora, String dataDivider,
			String recordId) {
		var repoDir = Paths.get(pathToOcflRepo);
		var workDir = Paths.get(pathToOcflWork);
		var repo = new OcflRepositoryBuilder().defaultLayoutConfig(new HashedNTupleLayoutConfig())
				.storage(storage -> storage.fileSystem(repoDir)).workDir(workDir).build();

		String objectId = generateObjectIdentifier(dataDivider, BINARY, recordId);
		VersionInfo versionInfo = createVersion("initial commit");

		repo.putObject(ObjectVersionId.head(objectId), Paths.get(pathToFileToMoveIntoFedora, ""),
				versionInfo);

		return getPathInFedoraToOcflObjects(dataDivider, recordId);
	}

	private void addFedoraMetadataFilesToOCFL(String pathToObjectFiles) {
		File rootFolderForObject = new File(pathToObjectFiles);
		List<JSONObject> metadataList = createFcrepoCatalog(rootFolderForObject);
		updateInventory(rootFolderForObject, metadataList, pathToObjectFiles);
		updateInventoryShaFile(rootFolderForObject);
		copyInventoryFiles(rootFolderForObject);
	}

	private String getPathInFedoraToOcflObjects(String dataDivider, String recordId) {
		String pathToAResourceInArchive = archivePathBuilderImp
				.buildPathToAResourceInArchive(dataDivider, BINARY, recordId);
		return removeFromV1Content(pathToAResourceInArchive);
	}

	private VersionInfo createVersion(String message) {
		VersionInfo versionInfo = new VersionInfo();
		versionInfo.setMessage(message);
		versionInfo.setUser("fedoraAdmin", "info:fedora/fedoraAdmin");
		return versionInfo;
	}

	public static String removeFromV1Content(String path) {
		int index = path.indexOf("v1/content");
		return (index != -1) ? path.substring(0, index) : path;
	}

	private String generateObjectIdentifier(String dataDivider, String type, String id) {
		String ocflPathLayout = "info:fedora/{0}:{1}:{2}-master";
		return MessageFormat.format(ocflPathLayout, dataDivider, type, id);
	}

	private void copyInventoryFiles(File ocflObjectPath) {
		File inventoryFile = new File(ocflObjectPath, INVENTORY_JSON);
		File inventoryShaFile = new File(ocflObjectPath, INVENTORY_JSON_SHA512);
		File v1Path = new File(ocflObjectPath, "v1");

		try {
			Files.copy(inventoryFile.toPath(), new File(v1Path, INVENTORY_JSON).toPath(),
					StandardCopyOption.REPLACE_EXISTING);
			Files.copy(inventoryShaFile.toPath(), new File(v1Path, INVENTORY_JSON_SHA512).toPath(),
					StandardCopyOption.REPLACE_EXISTING);
			LOGGER.info("Copied inventory files to " + v1Path.getAbsolutePath());
		} catch (IOException e) {
			LOGGER.severe("Error copying inventory files: " + e.getMessage());
		}
	}

	private List<JSONObject> createFcrepoCatalog(File ocflObjectPath) {
		File v1ContentPath = new File(ocflObjectPath, "v1/content");
		File fcrepoPath = new File(v1ContentPath, ".fcrepo");

		fcrepoPath.mkdirs();

		File[] files = v1ContentPath.listFiles((dir, name) -> new File(dir, name).isFile());
		if (files == null || files.length == 0) {
			LOGGER.warning("No files found in " + v1ContentPath.getAbsolutePath() + ", skipping.");
			return Collections.emptyList();
		}

		File filename = files[0];
		String createdDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
				.format(new Date());
		String stateToken = UUID.randomUUID().toString().toUpperCase();

		String fileHashRoot = sha512ChecksumUsingFile(filename);
		long fileSize = filename.length();

		JSONObject fcrRootMetadata = new JSONObject();
		fcrRootMetadata.put("id", "info:fedora/" + filename.getName());
		fcrRootMetadata.put("parent", "info:fedora");
		fcrRootMetadata.put("stateToken", stateToken);
		fcrRootMetadata.put("interactionModel", "http://www.w3.org/ns/ldp#NonRDFSource");
		fcrRootMetadata.put("mimeType", "application/octet-stream");
		fcrRootMetadata.put("filename", "");
		fcrRootMetadata.put("contentSize", fileSize);
		fcrRootMetadata.put("digests", Collections.singletonList("urn:sha-512:" + fileHashRoot));
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
		fcrDescMetadata.put("interactionModel",
				"http://fedora.info/definitions/v4/repository#NonRdfSourceDescription");
		fcrDescMetadata.put("contentSize", 0);
		fcrDescMetadata.put("digests", Collections.singletonList(
				"urn:sha-512:cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e"));
		fcrDescMetadata.put("createdDate", createdDate);
		fcrDescMetadata.put("lastModifiedDate", createdDate);
		fcrDescMetadata.put("mementoCreatedDate", createdDate);
		fcrDescMetadata.put("archivalGroup", false);
		fcrDescMetadata.put("objectRoot", false);
		fcrDescMetadata.put("deleted", false);
		fcrDescMetadata.put("contentPath", filename.getName() + "~fcr-desc.nt");
		fcrDescMetadata.put("headersVersion", "1.0");

		try {
			Files.write(new File(fcrepoPath, "fcr-root.json").toPath(),
					fcrRootMetadata.toString(4).getBytes());
			Files.write(new File(fcrepoPath, "fcr-root~fcr-desc.json").toPath(),
					fcrDescMetadata.toString(4).getBytes());
			new File(v1ContentPath, filename.getName() + "~fcr-desc.nt").createNewFile();
		} catch (IOException e) {
			LOGGER.severe("Error writing fcrepo files: " + e.getMessage());
		}
		return Arrays.asList(fcrRootMetadata, fcrDescMetadata);
	}

	private void updateInventory(File ocflObjectPath, List<JSONObject> metadataList,
			String basePath) {
		if (metadataList.isEmpty())
			return;

		File inventoryPath = new File(ocflObjectPath, INVENTORY_JSON);
		try {
			String content = new String(Files.readAllBytes(inventoryPath.toPath()));
			JSONObject inventory = new JSONObject(content);

			for (JSONObject metadata : metadataList) {
				JSONArray digests = metadata.getJSONArray("digests");
				for (Object digest : digests) {
					String digestStr = digest.toString().replace("urn:sha-512:", "");
					inventory.getJSONObject("manifest").put(digestStr, Collections
							.singletonList(V1_CONTENT + metadata.getString("contentPath")));
					inventory.getJSONObject("versions").getJSONObject("v1").getJSONObject("state")
							.put(digestStr,
									Collections.singletonList(metadata.getString("contentPath")));
				}
			}

			List<String> fcrepoFiles = List.of(".fcrepo/fcr-root.json",
					".fcrepo/fcr-root~fcr-desc.json");
			for (String filePath : fcrepoFiles) {
				String digestStr = sha512ChecksumUsingString(basePath + V1_CONTENT + filePath);
				inventory.getJSONObject("manifest").put(digestStr,
						Collections.singletonList(V1_CONTENT + filePath));
				inventory.getJSONObject("versions").getJSONObject("v1").getJSONObject("state")
						.put(digestStr, Collections.singletonList(filePath));
			}

			Files.write(inventoryPath.toPath(), inventory.toString(4).getBytes());
			LOGGER.info("Updated " + inventoryPath.getAbsolutePath());
		} catch (IOException e) {
			LOGGER.severe("Error updating inventory: " + e.getMessage());
		}
	}

	private void updateInventoryShaFile(File ocflObjectPath) {
		File fileJson = new File(ocflObjectPath, INVENTORY_JSON);
		File fileJsonSha512 = new File(ocflObjectPath, INVENTORY_JSON_SHA512);

		try (FileWriter writer = new FileWriter(fileJsonSha512, false)) {
			String fileJsonChecksume = sha512ChecksumUsingFile(fileJson);
			writer.write(fileJsonChecksume + "  inventory.json");
		} catch (IOException e) {
			LOGGER.severe("Error writing to file inventory.json.sha512:  " + e.getMessage());
		}
	}

	private String sha512ChecksumUsingFile(File file) {
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

	private String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

	private String sha512ChecksumUsingString(String text) {
		MessageDigest digest = tryToGetDigestAlgorithm();

		final byte[] hashbytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
		return bytesToHex2(hashbytes);
	}

	private MessageDigest tryToGetDigestAlgorithm() {
		try {
			return MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Error while analyzing image.", e);
		}
	}

	protected String bytesToHex2(byte[] hash) {
		final int initialFactorBytesToHex = 2;
		StringBuilder hexString = new StringBuilder(initialFactorBytesToHex * hash.length);
		bytesToHexInStringBuilder(hash, hexString);
		return hexString.toString();
	}

	private void bytesToHexInStringBuilder(byte[] hash, StringBuilder hexString) {
		for (int i = 0; i < hash.length; i++) {
			byte byteOfHash = hash[i];
			String hex = byteToHex(byteOfHash);
			hexString.append(hex);
		}
	}

	private String byteToHex(byte byteOfHash) {
		String hex = Integer.toHexString(0xff & byteOfHash);
		if (hex.length() == 1) {
			hex = "0" + hex;
		}
		return hex;
	}

}
