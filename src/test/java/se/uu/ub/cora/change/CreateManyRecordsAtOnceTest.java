package se.uu.ub.cora.change;

import org.testng.annotations.Test;

public class CreateManyRecordsAtOnceTest {

	private static final String SYSTEMONE_LOCAL_BASE_URL = "http://localhost:8080/systemone/rest/";
	private static final String SYSTEMONE_LOCAL_APPTOKEN_URL = "http://localhost:8180/login/rest/";

	@Test(enabled = false)
	public void runCreateManyDemosAtOnce() {
		CreateManyRecordsAtOnce creator = new CreateManyRecordsAtOnce(SYSTEMONE_LOCAL_APPTOKEN_URL,
				SYSTEMONE_LOCAL_BASE_URL);

		creator.createExamples();
	}

}
