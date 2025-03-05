package se.uu.ub.cora.change.ocfl;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.nio.file.Paths;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.ocfl.api.OcflOption;
import io.ocfl.api.model.ObjectVersionId;
import io.ocfl.api.model.OcflObjectVersion;
import io.ocfl.api.model.VersionInfo;
import io.ocfl.core.OcflRepositoryBuilder;
import io.ocfl.core.extension.storage.layout.config.HashedNTupleLayoutConfig;

public class OCFLwrappereTest {

	private static final File TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));

	@BeforeTest
	public void beforeTest() {
		createDir("ocfl/ocfl-repo");
		createDir("ocfl/ocfl-work");
	}

	@Test
	public void testDirs() throws Exception {
		createDir("ocfl/ocfl-repo2");
		createDir("ocfl/ocfl-work2");

	}

	@Test
	public void testPutOneBinary() throws Exception {
		// createDir("ocfl/ocfl-repo");
		// createDir("ocfl/ocfl-work");

		var repoDir = Paths.get("ocfl/ocfl-repo"); // This directory contains the OCFL
													// storage root.
		var workDir = Paths.get("ocfl/ocfl-work"); // This directory is used to assemble OCFL
													// versions.
		// It cannot be within the OCFL storage root.

		var repo = new OcflRepositoryBuilder().defaultLayoutConfig(new HashedNTupleLayoutConfig())
				.storage(storage -> storage.fileSystem(repoDir)).workDir(workDir).build();

		// repo.putObject(ObjectVersionId.head("o1"), Paths.get("ocfl/object-out-dir"),
		repo.putObject(ObjectVersionId.head("o1"), Paths.get("ocfl/failedHard.png"),
				new VersionInfo().setMessage("initial commit"), OcflOption.MOVE_SOURCE);

		// Contains object details and lazy-load resource handles
		OcflObjectVersion objectVersion = repo.getObject(ObjectVersionId.version("o1", "v1"));

		assertEquals(objectVersion.getVersionInfo(), "");

	}

	@Test
	public void testName() throws Exception {
		createDir("ocfl/ocfl-repo");
		createDir("ocfl/ocfl-work");

		var repoDir = Paths.get("ocfl/ocfl-repo"); // This directory contains the OCFL
		// storage root.
		var workDir = Paths.get("ocfl/ocfl-work"); // This directory is used to assemble OCFL
		// versions.
		// It cannot be within the OCFL storage root.

		var repo = new OcflRepositoryBuilder().defaultLayoutConfig(new HashedNTupleLayoutConfig())
				.storage(storage -> storage.fileSystem(repoDir)).workDir(workDir).build();

		// repo.putObject(ObjectVersionId.head("o1"), Paths.get("ocfl/object-out-dir"),
		repo.putObject(ObjectVersionId.head("o1"), Paths.get("ocfl/failed.png"),
				new VersionInfo().setMessage("initial commit"));
		// repo.getObject(ObjectVersionId.head("o1"), Paths.get("ocfl/object-in-dir"));
		//
		repo.updateObject(ObjectVersionId.head("o1"), new VersionInfo().setMessage("update"),
				updater -> {
					updater.addPath(Paths.get("path-to-file2"), "file2").removeFile("file1")
							.addPath(Paths.get("path-to-file3"), "dir1/file3");
				});

		// Contains object details and lazy-load resource handles
		OcflObjectVersion objectVersion = repo.getObject(ObjectVersionId.version("o1", "v1"));

		assertEquals(objectVersion.getVersionInfo(), "");

	}

	private void createDir(String dirOcflWork) {
		File newDirectory = new File(TEMP_DIRECTORY, dirOcflWork);
		newDirectory.mkdir();
	}

}
