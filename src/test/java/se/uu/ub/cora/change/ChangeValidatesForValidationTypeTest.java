package se.uu.ub.cora.change;

import org.testng.annotations.Test;

public class ChangeValidatesForValidationTypeTest {

	private static final String SYSTEMONE_LOCAL_APPTOKEN_URL = "http://localhost:8180/login/rest/";
	private static final String SYSTEMONE_LOCAL_BASE_URL = "http://localhost:8080/systemone/rest/";

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
	public void runChangeValidationTypes() {

		ChangeValidatesForValidationType updater = new ChangeValidatesForValidationType(
				SYSTEMONE_LOCAL_APPTOKEN_URL, SYSTEMONE_LOCAL_BASE_URL);

		updater.changeValidationTypes();
	}

	@Test(enabled = false)
	public void runChangeValidationTypesToAbstractLevelForAlvin() {

		ChangeValidatesForValidationType updater = new ChangeValidatesForValidationType(
				ALVIN_LOCAL_APPTOKEN_URL, ALVIN_LOCAL_BASE_URL);

		updater.changeValidationTypesToAbstractLevel("Alvin");
	}

	@Test(enabled = false)
	public void runChangeValidationTypesToAbstractLevelForDiVA() {

		ChangeValidatesForValidationType updater = new ChangeValidatesForValidationType(
				DIVA_LOCAL_APPTOKEN_URL, DIVA_LOCAL_BASE_URL);

		updater.changeValidationTypesToAbstractLevel("Diva");
	}

	@Test(enabled = false)
	public void runChangeValidationTypesToAbstractLevel() {

		ChangeValidatesForValidationType updater = new ChangeValidatesForValidationType(
				SYSTEMONE_LOCAL_APPTOKEN_URL, SYSTEMONE_LOCAL_BASE_URL);

		updater.changeValidationTypesToAbstractLevel("SystemOne");
	}

	@Test(enabled = false)
	public void runChangeAbstractRecordTypesToImplementing() {

		ChangeValidatesForValidationType updater = new ChangeValidatesForValidationType(
				SYSTEMONE_LOCAL_APPTOKEN_URL, SYSTEMONE_LOCAL_BASE_URL);

		updater.changeAbstractRecordTypesToImplementing();
	}

	@Test(enabled = false)
	public void runChangeValidationTypesSystemOneDev() {

		ChangeValidatesForValidationType updater = new ChangeValidatesForValidationType(
				SYSTEMONE_DEV_APPTOKEN_URL, SYSTEMONE_DEV_BASE_URL);

		updater.changeValidationTypes();
	}

	@Test(enabled = false)
	public void runChangeValidationTypesAlvinDev() {

		ChangeValidatesForValidationType updater = new ChangeValidatesForValidationType(
				ALVIN_DEV_APPTOKEN_URL, ALVIN_DEV_BASE_URL);

		updater.changeValidationTypes();
	}

	@Test(enabled = false)
	public void runChangeValidationTypesDivaDev() {

		ChangeValidatesForValidationType updater = new ChangeValidatesForValidationType(
				DIVA_DEV_APPTOKEN_URL, DIVA_DEV_BASE_URL);

		updater.changeValidationTypes();
	}
}
