package se.uu.ub.cora.change;

import org.testng.annotations.Test;

public class CollectAllFieldsForAbstractRecordTypesTest {

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
	public void runCollectAllFieldsForAbstractRecordTypes() throws Exception {

		CollectAllFieldsForAbstractRecordTypes updater = new CollectAllFieldsForAbstractRecordTypes(
				SYSTEMONE_LOCAL_APPTOKEN_URL, SYSTEMONE_LOCAL_BASE_URL);

		updater.collectAndStoreGroupForAbstract();
	}

	@Test(enabled = false)
	public void runCollectAllFieldsForAbstractRecordTypesDiVA() throws Exception {

		CollectAllFieldsForAbstractRecordTypes updater = new CollectAllFieldsForAbstractRecordTypes(
				DIVA_LOCAL_APPTOKEN_URL, DIVA_LOCAL_BASE_URL);

		updater.collectAndStoreGroupForAbstract();
	}

	@Test(enabled = false)
	public void runCollectAllFieldsForAbstractRecordTypesAlvin() throws Exception {

		CollectAllFieldsForAbstractRecordTypes updater = new CollectAllFieldsForAbstractRecordTypes(
				ALVIN_LOCAL_APPTOKEN_URL, ALVIN_LOCAL_BASE_URL);

		updater.collectAndStoreGroupForAbstract();
	}

}
