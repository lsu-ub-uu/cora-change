package se.uu.ub.cora.change;

import org.testng.annotations.Test;

public class ChangeTextsRemoveEmptyTextsTest {

	private static final String DRY_RUN = "dryRun";
	private static final String SYSTEMONE_LOCAL_APPTOKEN_URL = "http://localhost:8180/login/rest/";
	private static final String SYSTEMONE_LOCAL_BASE_URL = "http://localhost:8080/systemone/rest/";

	private static final String SYSTEMONE_PREVIEW_APPTOKEN_URL = "https://preview.systemone.cora.epc.ub.uu.se/login/rest/";
	private static final String SYSTEMONE_PREVIEW_BASE_URL = "https://preview.systemone.cora.epc.ub.uu.se/rest/";

	private static final String ALVIN_LOCAL_APPTOKEN_URL = "http://localhost:8181/login/rest/";
	private static final String ALVIN_LOCAL_BASE_URL = "http://localhost:8081/alvin/rest/";

	private static final String DIVA_LOCAL_APPTOKEN_URL = "http://localhost:8182/login/rest/";
	private static final String DIVA_LOCAL_BASE_URL = "http://localhost:8082/diva/rest/";

	private static final String SYSTEMONE_DEV_APPTOKEN_URL = "http://130.238.171.238:38180/login/rest/";
	private static final String SYSTEMONE_DEV_BASE_URL = "http://130.238.171.238:38080/systemone/rest/";

	private static final String ALVIN_DEV_APPTOKEN_URL = "http://130.238.171.238:38181/login/rest/";
	private static final String ALVIN_DEV_BASE_URL = "http://130.238.171.238:38081/alvin/rest/";

	private static final String DIVA_DEV_APPTOKEN_URL = "http://130.238.171.238:38182/login/rest/";
	private static final String DIVA_DEV_BASE_URL = "http://130.238.171.238:38082/diva/rest/";

	private static final String ALVIN_PRE_LOGIN_URL = "https://cora.alvin-portal.org/login/rest/";
	private static final String ALVIN_PRE_BASE_URL = "https://cora.alvin-portal.org/rest/";

	@Test(enabled = false)
	public void runChangeValidationTypeForSystemOneTexts() {

		ChangeTextsRemoveEmptyTexts updater = new ChangeTextsRemoveEmptyTexts(
				SYSTEMONE_DEV_APPTOKEN_URL, SYSTEMONE_DEV_BASE_URL,
				"systemoneAdmin@system.cora.uu.se", "f464dbf7-fad3-43cf-8871-543534");

		updater.removeEmptyTexts(DRY_RUN);
	}

	@Test(enabled = false)
	public void runChangeValidationTypeForAlvinTexts() {

		ChangeTextsRemoveEmptyTexts updater = new ChangeTextsRemoveEmptyTexts(
				ALVIN_DEV_APPTOKEN_URL, ALVIN_DEV_BASE_URL, "alvinAdmin@cora.epc.ub.uu.se",
				"b1752dd4-eeb4-4b7d-98e2-435345");

		updater.removeEmptyTexts(DRY_RUN);
	}

	@Test(enabled = false)
	public void runChangeValidationTypeForAlvinTexts_PRE() {

		ChangeTextsRemoveEmptyTexts updater = new ChangeTextsRemoveEmptyTexts(ALVIN_PRE_LOGIN_URL,
				ALVIN_PRE_BASE_URL, "alvinAdmin@cora.epc.ub.uu.se",
				"f2b2a0b8-3ae4-4223-acd6-543534532");

		updater.removeEmptyTexts(DRY_RUN);
	}

	@Test(enabled = false)
	public void runChangeValidationTypeForDivaTexts() {

		ChangeTextsRemoveEmptyTexts updater = new ChangeTextsRemoveEmptyTexts(DIVA_DEV_APPTOKEN_URL,
				DIVA_DEV_BASE_URL, "divaAdmin@cora.epc.ub.uu.se",
				"cb1256d6-29a3-4162-abb0-5435235");
		// ChangeTextsRemoveEmptyTexts updater = new
		// ChangeTextsRemoveEmptyTexts(DIVA_DEV_APPTOKEN_URL,
		// DIVA_DEV_BASE_URL, "systemoneAdmin@system.cora.uu.se",
		// "51348191-b8e4-4a76-b5c1-b6f069c78f39");

		updater.removeEmptyTexts(DRY_RUN);
	}

}
