package se.uu.ub.cora.change;

import org.testng.annotations.Test;

public class PresentationAddValidationTypeLinkTest {

	PresentationAddValidationTypeLink updater;

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
	public void updatePresentations_SystemOne_Utv() throws Exception {
		updater = new PresentationAddValidationTypeLink(SYSTEMONE_UTV_APPTOKEN_URL,
				SYSTEMONE_UTV_BASE_URL);
		updater.updateAllPresentationsAndAddAValidationType();
	}

	@Test(enabled = false)
	public void testListRecordTypes_Alvin_Utv() throws Exception {
		updater = new PresentationAddValidationTypeLink(ALVIN_UTV_APPTOKEN_URL, ALVIN_UTV_BASE_URL);
		updater.updateAllPresentationsAndAddAValidationType();
	}

	@Test(enabled = false)
	public void testListRecordTypes_Diva_Utv() throws Exception {
		updater = new PresentationAddValidationTypeLink(DIVA_UTV_APPTOKEN_URL, DIVA_UTV_BASE_URL);
		updater.updateAllPresentationsAndAddAValidationType();
	}

	@Test(enabled = false)
	public void testListRecordTypes_SystemOne_Dev() throws Exception {
		updater = new PresentationAddValidationTypeLink(SYSTEMONE_DEV_APPTOKEN_URL,
				SYSTEMONE_DEV_BASE_URL);
		updater.updateAllPresentationsAndAddAValidationType();
	}

	@Test(enabled = false)
	public void testListRecordTypes_Alvin_Dev() throws Exception {
		updater = new PresentationAddValidationTypeLink(ALVIN_DEV_APPTOKEN_URL, ALVIN_DEV_BASE_URL);
		updater.updateAllPresentationsAndAddAValidationType();
	}

	@Test(enabled = false)
	public void testListRecordTypes_Diva_Dev() throws Exception {
		updater = new PresentationAddValidationTypeLink(DIVA_DEV_APPTOKEN_URL, DIVA_DEV_BASE_URL);
		updater.updateAllPresentationsAndAddAValidationType();
	}

}
