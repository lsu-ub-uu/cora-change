package se.uu.ub.cora.change;

import org.testng.annotations.Test;

public class ChangeFindEmptyAtomicsTest {

	private static final String DRY_RUN = "dryRun";
	private static final String SYSTEMONE_LOCAL_APPTOKEN_URL = "http://localhost:8180/login/rest/";
	private static final String SYSTEMONE_LOCAL_BASE_URL = "http://localhost:8080/systemone/rest/";

	private static final String SYSTEMONE_PREVIEW_APPTOKEN_URL = "https://preview.systemone.cora.epc.ub.uu.se/login/rest/";
	private static final String SYSTEMONE_PREVIEW_BASE_URL = "https://preview.systemone.cora.epc.ub.uu.se/rest/";

	private static final String ALVIN_PREVIEW_APPTOKEN_URL = "https://preview.alvin.cora.epc.ub.uu.se/login/rest/";
	private static final String ALVIN_PREVIEW_BASE_URL = "https://preview.alvin.cora.epc.ub.uu.se/rest/";

	private static final String DIVA_PREVIEW_APPTOKEN_URL = "https://preview.diva.cora.epc.ub.uu.se/login/rest/";
	private static final String DIVA_PREVIEW_BASE_URL = "https://preview.diva.cora.epc.ub.uu.se/rest/";

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
	public void runFindEmptyForSystemOneTexts() {

		ChangeFindEmptyAtomics updater = new ChangeFindEmptyAtomics(SYSTEMONE_PREVIEW_APPTOKEN_URL,
				SYSTEMONE_PREVIEW_BASE_URL, "systemoneAdmin@system.cora.uu.se",
				"47e50f91-9d62-44ae-96f7-6102139c838e");

		updater.findEmptyAtomics(DRY_RUN);
	}

	@Test(enabled = false)
	public void runFindEmptyForAlvinOneTexts() {

		ChangeFindEmptyAtomics updater = new ChangeFindEmptyAtomics(ALVIN_PREVIEW_APPTOKEN_URL,
				ALVIN_PREVIEW_BASE_URL, "alvinAdmin@cora.epc.ub.uu.se",
				"41c37615-c33d-4e01-9748-554d1b2cfec1");

		updater.findEmptyAtomics(DRY_RUN);
	}

	@Test(enabled = false)
	public void runFindEmptyForDivaOneTexts() {

		ChangeFindEmptyAtomics updater = new ChangeFindEmptyAtomics(DIVA_PREVIEW_APPTOKEN_URL,
				DIVA_PREVIEW_BASE_URL, "divaAdmin@cora.epc.ub.uu.se",
				"b5ffafe1-15a5-492b-baa9-511cb2a47b33");

		updater.findEmptyAtomics(DRY_RUN);
	}

	@Test(enabled = false)
	public void runChangeValidationTypeForSystemOneTexts() {

		ChangeFindEmptyAtomics updater = new ChangeFindEmptyAtomics(SYSTEMONE_DEV_APPTOKEN_URL,
				SYSTEMONE_DEV_BASE_URL, "systemoneAdmin@system.cora.uu.se",
				"47e50f91-9d62-44ae-96f7-6102139c838e");

		updater.findEmptyAtomics(DRY_RUN);
	}

	@Test(enabled = false)
	public void runChangeValidationTypeForAlvinTexts() {

		ChangeFindEmptyAtomics updater = new ChangeFindEmptyAtomics(ALVIN_DEV_APPTOKEN_URL,
				ALVIN_DEV_BASE_URL, "alvinAdmin@cora.epc.ub.uu.se",
				"b1752dd4-eeb4-4b7d-98e2-9d6167cce2e4");

		updater.findEmptyAtomics(DRY_RUN);
	}

	@Test(enabled = false)
	public void runChangeValidationTypeForDivaTexts() {

		ChangeFindEmptyAtomics updater = new ChangeFindEmptyAtomics(DIVA_DEV_APPTOKEN_URL,
				DIVA_DEV_BASE_URL, "divaAdmin@cora.epc.ub.uu.se",
				"c0ff99a1-2aec-477d-99d6-224eecd80825");

		updater.findEmptyAtomics(DRY_RUN);
	}

}
