package se.uu.ub.cora.change.ocfl;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import se.uu.ub.cora.fedoraarchive.path.ArchivePathBuilderImp;
import se.uu.ub.cora.storage.archive.ArchivePathBuilder;

public class OCFLProcessorTest {

	private static final String PATH_TO_OCFL_REPO = "/tmp/sharedArchive/systemOne";
	private static final String OCFL_OCFL_WORK = "ocfl/ocfl-work";
	private OcflProcessor ocflProcessor;
	private ArchivePathBuilder archivePathBuilderImp;

	@BeforeTest
	public void beforeTest() {
		createDir(OCFL_OCFL_WORK);

		archivePathBuilderImp = new ArchivePathBuilderImp(PATH_TO_OCFL_REPO);
		ocflProcessor = new OcflProcessor(PATH_TO_OCFL_REPO, OCFL_OCFL_WORK, archivePathBuilderImp);
		// createDir("ocfl/ocfl-repo");
	}

	@Test(enabled = false)
	public void testPutOneBinary() {

		String dataDividerd = "systemOne";
		String recordId = "binary:001";
		String pathOnDiskToFileToMoveToFedora = "ocfl/systemOne:binary:binary:001-master";
		createFileForTest(pathOnDiskToFileToMoveToFedora);

		ocflProcessor.addBinaryToFedoraRepo(pathOnDiskToFileToMoveToFedora, dataDividerd, recordId);

		assertTrue(true);

		// assertEquals(
		// removeFromV1Content(archivePathBuilderImp
		// .buildPathToAResourceInArchive(dataDividerd, "binary", recordId)),
		// "/tmp/sharedArchive/systemOne/1a0/c80/f4b/1a0c80f4b87f8b9bce0fb9672fb2b2912b5e5613c115626d7c1f391dc157fcdb/");
	}

	private void createFileForTest(String fileName) {
		File file = new File(fileName);
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createDir(String dirOcflWork) {
		File newDirectory = new File(dirOcflWork);
		if (!newDirectory.exists()) {
			newDirectory.mkdirs(); // Ensure all directories in the path are created
		}
	}
}
