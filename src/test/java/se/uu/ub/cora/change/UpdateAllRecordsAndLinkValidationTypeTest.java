package se.uu.ub.cora.change;

import org.testng.annotations.Test;

public class UpdateAllRecordsAndLinkValidationTypeTest {

	private static final String SYSTEMONE_UTV_APPTOKEN_URL = "http://localhost:8180/apptokenverifier/rest/";
	private static final String SYSTEMONE_UTV_BASE_URL = "http://localhost:8080/systemone/rest/";

	private static final String ALVIN_UTV_APPTOKEN_URL = "http://localhost:8181/apptokenverifier/rest/";
	private static final String ALVIN_UTV_BASE_URL = "http://localhost:8081/alvin/rest/";

	private static final String DIVA_UTV_APPTOKEN_URL = "http://localhost:8182/apptokenverifier/rest/";
	private static final String DIVA_UTV_BASE_URL = "http://localhost:8082/diva/rest/";

	private static final String SYSTEMONE_DEV_APPTOKEN_URL = "http://130.238.171.238:38180/apptokenverifier/rest/";
	private static final String SYSTEMONE_DEV_BASE_URL = "http://130.238.171.238:38080/systemone/rest/";

	private static final String ALVIN_DEV_APPTOKEN_URL = "http://130.238.171.238:38181/apptokenverifier/rest/";
	private static final String ALVIN_DEV_BASE_URL = "http://130.238.171.238:38081/alvin/rest/";

	private static final String DIVA_DEV_APPTOKEN_URL = "http://130.238.171.238:38182/apptokenverifier/rest/";
	private static final String DIVA_DEV_BASE_URL = "http://130.238.171.238:38082/diva/rest/";

	@Test(enabled = false)
	public void testName() throws Exception {

		UpdateAllRecordsAndLinkValidationType updater = new UpdateAllRecordsAndLinkValidationType(
				SYSTEMONE_UTV_APPTOKEN_URL, SYSTEMONE_UTV_BASE_URL);

		updater.updateAllRecords();
	}

}
