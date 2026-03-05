package se.uu.ub.cora.change;

import org.testng.annotations.Test;

public class ChangeRegexpTest {

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

	@Test(enabled = false)
	public void runChangeValidationTypeForSystemOneTexts() {

		// ChangeRegexp updater = new ChangeRegexp(
		// SYSTEMONE_DEV_APPTOKEN_URL, SYSTEMONE_DEV_BASE_URL,
		// "systemoneAdmin@system.cora.uu.se", "fd0d31c1-5ed1-451c-bb46-6c4b6c9483aa");
		ChangeRegexp updater = new ChangeRegexp(SYSTEMONE_PREVIEW_APPTOKEN_URL,
				SYSTEMONE_PREVIEW_BASE_URL, "systemoneAdmin@system.cora.uu.se",
				"fd0d31c1-5ed1-451c-bb46-6c4b6c9483aa");

		updater.removeEmptyTexts(DRY_RUN);
	}

	@Test(enabled = false)
	public void runChangeValidationTypeForAlvinTexts() {

		ChangeRegexp updater = new ChangeRegexp(ALVIN_DEV_APPTOKEN_URL, ALVIN_DEV_BASE_URL,
				"alvinAdmin@cora.epc.ub.uu.se", "b1752dd4-eeb4-4b7d-98e2-9d6167cce2e4");

		updater.removeEmptyTexts(DRY_RUN);
	}

	@Test(enabled = false)
	public void runChangeValidationTypeForDivaTexts() {

		ChangeRegexp updater = new ChangeRegexp(DIVA_DEV_APPTOKEN_URL, DIVA_DEV_BASE_URL,
				"divaAdmin@cora.epc.ub.uu.se", "cb1256d6-29a3-4162-abb0-cd73732963c6");
		// ChangeRegexp updater = new
		// ChangeRegexp(DIVA_DEV_APPTOKEN_URL,
		// DIVA_DEV_BASE_URL, "systemoneAdmin@system.cora.uu.se",
		// "51348191-b8e4-4a76-b5c1-b6f069c78f39");

		updater.removeEmptyTexts(DRY_RUN);
	}

}
