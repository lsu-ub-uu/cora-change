package se.uu.ub.cora.change;

import org.testng.annotations.Test;

public class ChangeValidatesForValidationTypeTest {

	private static final String SYSTEMONE_LOCAL_APPTOKEN_URL = "http://localhost:8180/apptokenverifier/rest/";
	private static final String SYSTEMONE_LOCAL_BASE_URL = "http://localhost:8080/systemone/rest/";

	private static final String ALVIN_LOCAL_APPTOKEN_URL = "http://localhost:8181/apptokenverifier/rest/";
	private static final String ALVIN_LOCAL_BASE_URL = "http://localhost:8081/alvin/rest/";

	private static final String DIVA_LOCAL_APPTOKEN_URL = "http://localhost:8182/apptokenverifier/rest/";
	private static final String DIVA_LOCAL_BASE_URL = "http://localhost:8082/diva/rest/";

	private static final String SYSTEMONE_DEV_APPTOKEN_URL = "http://130.238.171.238:38180/apptokenverifier/rest/";
	private static final String SYSTEMONE_DEV_BASE_URL = "http://130.238.171.238:38080/systemone/rest/";

	private static final String ALVIN_DEV_APPTOKEN_URL = "http://130.238.171.238:38181/apptokenverifier/rest/";
	private static final String ALVIN_DEV_BASE_URL = "http://130.238.171.238:38081/alvin/rest/";

	private static final String DIVA_DEV_APPTOKEN_URL = "http://130.238.171.238:38182/apptokenverifier/rest/";
	private static final String DIVA_DEV_BASE_URL = "http://130.238.171.238:38082/diva/rest/";

	@Test(enabled = false)
	public void runChangeValidationTypes() throws Exception {

		ChangeValidatesForValidationType updater = new ChangeValidatesForValidationType(
				SYSTEMONE_LOCAL_APPTOKEN_URL, SYSTEMONE_LOCAL_BASE_URL);

		updater.changeValidationTypes();
	}

	// @Test(enabled = true)
	// public void runChangeValidationTypesDiva() throws Exception {
	//
	// ChangeValidatesForValidationType updater = new ChangeValidatesForValidationType(
	// DIVA_LOCAL_APPTOKEN_URL, DIVA_LOCAL_BASE_URL);
	//
	// updater.changeValidationTypes();
	// }

	@Test(enabled = false)
	public void runChangeValidationTypesToAbstractLevelForAlvin() throws Exception {

		ChangeValidatesForValidationType updater = new ChangeValidatesForValidationType(
				ALVIN_LOCAL_APPTOKEN_URL, ALVIN_LOCAL_BASE_URL);

		updater.changeValidationTypesToAbstractLevel("Alvin");
	}

	@Test(enabled = false)
	public void runChangeValidationTypesToAbstractLevelForDiVA() throws Exception {

		ChangeValidatesForValidationType updater = new ChangeValidatesForValidationType(
				DIVA_LOCAL_APPTOKEN_URL, DIVA_LOCAL_BASE_URL);

		updater.changeValidationTypesToAbstractLevel("Diva");
	}

	@Test(enabled = false)
	public void runChangeValidationTypesToAbstractLevel() throws Exception {

		ChangeValidatesForValidationType updater = new ChangeValidatesForValidationType(
				SYSTEMONE_LOCAL_APPTOKEN_URL, SYSTEMONE_LOCAL_BASE_URL);

		updater.changeValidationTypesToAbstractLevel("SystemOne");
	}

	@Test(enabled = false)
	public void runChangeAbstractRecordTypesToImplementing() throws Exception {

		ChangeValidatesForValidationType updater = new ChangeValidatesForValidationType(
				SYSTEMONE_LOCAL_APPTOKEN_URL, SYSTEMONE_LOCAL_BASE_URL);

		updater.changeAbstractRecordTypesToImplementing();
	}

	@Test(enabled = false)
	public void runChangeValidationTypesSystemOneDev() throws Exception {

		ChangeValidatesForValidationType updater = new ChangeValidatesForValidationType(
				SYSTEMONE_DEV_APPTOKEN_URL, SYSTEMONE_DEV_BASE_URL);

		updater.changeValidationTypes();
	}

	@Test(enabled = false)
	public void runChangeValidationTypesAlvinDev() throws Exception {

		ChangeValidatesForValidationType updater = new ChangeValidatesForValidationType(
				ALVIN_DEV_APPTOKEN_URL, ALVIN_DEV_BASE_URL);

		updater.changeValidationTypes();
	}

	@Test(enabled = false)
	public void runChangeValidationTypesDivaDev() throws Exception {

		ChangeValidatesForValidationType updater = new ChangeValidatesForValidationType(
				DIVA_DEV_APPTOKEN_URL, DIVA_DEV_BASE_URL);

		updater.changeValidationTypes();
	}
}

// @Test(enabled = false)
// public void deleteDefTextsLocalSystemOne() throws Exception {
//
// CreateValidationTypeForEachRecordType updater = new CreateValidationTypeForEachRecordType(
// SYSTEMONE_LOCAL_APPTOKEN_URL, SYSTEMONE_LOCAL_BASE_URL);
//
// updater.deleteValidationTypeTexts();
// }
//
// @Test(enabled = false)
// public void createValidationTypesLocalSystemOne() throws Exception {
//
// CreateValidationTypeForEachRecordType updater = new CreateValidationTypeForEachRecordType(
// SYSTEMONE_LOCAL_APPTOKEN_URL, SYSTEMONE_LOCAL_BASE_URL);
//
// updater.createValidationTypes();
// }
//
// @Test(enabled = false)
// public void testLocalDiva() throws Exception {
//
// CreateValidationTypeForEachRecordType updater = new CreateValidationTypeForEachRecordType(
// DIVA_LOCAL_APPTOKEN_URL, DIVA_LOCAL_BASE_URL);
//
// updater.createValidationTypes();
// }
//
// @Test(enabled = false)
// public void testLocalAlvin() throws Exception {
//
// CreateValidationTypeForEachRecordType updater = new CreateValidationTypeForEachRecordType(
// ALVIN_LOCAL_APPTOKEN_URL, ALVIN_LOCAL_BASE_URL);
//
// updater.createValidationTypes();
// }
//
// // system one dev
// @Test(enabled = false)
// public void deleteValidationTypesDevSystemOne() throws Exception {
//
// CreateValidationTypeForEachRecordType updater = new CreateValidationTypeForEachRecordType(
// SYSTEMONE_DEV_APPTOKEN_URL, SYSTEMONE_DEV_BASE_URL);
//
// updater.deleteValidationTypes();
// }
//
// @Test(enabled = false)
// public void deleteDefTextsDevSystemOne() throws Exception {
//
// CreateValidationTypeForEachRecordType updater = new CreateValidationTypeForEachRecordType(
// SYSTEMONE_DEV_APPTOKEN_URL, SYSTEMONE_DEV_BASE_URL);
//
// updater.deleteValidationTypeTexts();
// }
//
// @Test(enabled = false)
// public void testDevSystemOne() throws Exception {
//
// CreateValidationTypeForEachRecordType updater = new CreateValidationTypeForEachRecordType(
// SYSTEMONE_DEV_APPTOKEN_URL, SYSTEMONE_DEV_BASE_URL);
//
// updater.createValidationTypes();
// }
//
// @Test(enabled = false)
// public void testDevAlvin() throws Exception {
//
// CreateValidationTypeForEachRecordType updater = new CreateValidationTypeForEachRecordType(
// ALVIN_DEV_APPTOKEN_URL, ALVIN_DEV_BASE_URL);
//
// updater.createValidationTypes();
// }
//
// @Test(enabled = false)
// public void testDevDiva() throws Exception {
//
// CreateValidationTypeForEachRecordType updater = new CreateValidationTypeForEachRecordType(
// DIVA_DEV_APPTOKEN_URL, DIVA_DEV_BASE_URL);
//
// updater.createValidationTypes();
// }
// }
