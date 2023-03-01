package se.uu.ub.cora.change;

import org.testng.annotations.Test;

public class CreateValidationTypeForEachRecordTypeTest {

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
	public void testUtvSystemOne() throws Exception {

		CreateValidationTypeForEachRecordType updater = new CreateValidationTypeForEachRecordType(
				SYSTEMONE_UTV_APPTOKEN_URL, SYSTEMONE_UTV_BASE_URL);

		updater.createValidationTypes();
	}

	@Test(enabled = false)
	public void deleteDefTextsUtvSystemOne() throws Exception {

		CreateValidationTypeForEachRecordType updater = new CreateValidationTypeForEachRecordType(
				SYSTEMONE_UTV_APPTOKEN_URL, SYSTEMONE_UTV_BASE_URL);

		updater.deleteValidationTypeTexts();
	}

	@Test(enabled = false)
	public void deleteValidationTypesUtvSystemOne() throws Exception {

		CreateValidationTypeForEachRecordType updater = new CreateValidationTypeForEachRecordType(
				SYSTEMONE_UTV_APPTOKEN_URL, SYSTEMONE_UTV_BASE_URL);

		updater.deleteValidationTypes();
	}

	@Test(enabled = false)
	public void testUtvDiva() throws Exception {

		CreateValidationTypeForEachRecordType updater = new CreateValidationTypeForEachRecordType(
				DIVA_UTV_APPTOKEN_URL, DIVA_UTV_BASE_URL);

		updater.createValidationTypes();
	}

	@Test(enabled = false)
	public void testUtvAlvin() throws Exception {

		CreateValidationTypeForEachRecordType updater = new CreateValidationTypeForEachRecordType(
				ALVIN_UTV_APPTOKEN_URL, ALVIN_UTV_BASE_URL);

		updater.createValidationTypes();
	}

	@Test(enabled = false)
	public void testDevSystemOne() throws Exception {

		CreateValidationTypeForEachRecordType updater = new CreateValidationTypeForEachRecordType(
				SYSTEMONE_DEV_APPTOKEN_URL, SYSTEMONE_DEV_BASE_URL);

		updater.createValidationTypes();
	}

	@Test(enabled = false)
	public void testDevDiva() throws Exception {

		CreateValidationTypeForEachRecordType updater = new CreateValidationTypeForEachRecordType(
				DIVA_DEV_APPTOKEN_URL, DIVA_DEV_BASE_URL);

		updater.createValidationTypes();
	}

}
