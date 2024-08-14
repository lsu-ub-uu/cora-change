package se.uu.ub.cora.change;

import java.util.Collections;
import java.util.List;

import org.testng.annotations.Test;

import se.uu.ub.cora.javaclient.cora.DataClient;
import se.uu.ub.cora.javaclient.cora.DataClientFactoryImp;

public class UpdateAllRecordsAndLinkValidationTypeTest {

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

	private static final String SYSTEMONE_USER = "systemoneAdmin@system.cora.uu.se";
	private static final String SYSTEMONE_APPTOKEN_USER = "5d3f3ed4-4931-4924-9faa-8eaf5ac6457e";

	private static final String DIVA_USER = "divaEverything@diva.cora.uu.se";
	private static final String DIVA_APPTOKEN_USER = "77edfec1-e1f1-45d4-a452-411668eba0f0";

	private static final String ALVIN_USER = "alvinAdmin@cora.epc.ub.uu.se";
	private static final String ALVIN_APPTOKEN_USER = "a50ca087-a3f5-4393-b2bb-315436d3c3be";

	@Test(enabled = false)
	public void runLocalSystemOne() throws Exception {

		UpdateAllRecordsAndLinkValidationType updater = new UpdateAllRecordsAndLinkValidationType(
				SYSTEMONE_LOCAL_APPTOKEN_URL, SYSTEMONE_LOCAL_BASE_URL, SYSTEMONE_USER,
				SYSTEMONE_APPTOKEN_USER);

		updater.updateAllRecords(Collections.emptyList());
	}

	private UpdateAllRecordsAndLinkValidationType createLocalAlvinUpdater() {
		// return new UpdateAllRecordsAndLinkValidationType(ALVIN_LOCAL_APPTOKEN_URL,
		// ALVIN_LOCAL_BASE_URL, SYSTEMONE_USER, SYSTEMONE_APPTOKEN_USER);
		return new UpdateAllRecordsAndLinkValidationType(ALVIN_LOCAL_APPTOKEN_URL,
				ALVIN_LOCAL_BASE_URL, ALVIN_USER, ALVIN_APPTOKEN_USER);
	}

	@Test(enabled = false)
	public void runLocalDiva() throws Exception {
		UpdateAllRecordsAndLinkValidationType updater = new UpdateAllRecordsAndLinkValidationType(
				DIVA_LOCAL_APPTOKEN_URL, DIVA_LOCAL_BASE_URL, DIVA_USER, DIVA_APPTOKEN_USER);

		updater.updateOnlyRecordsForTheseTypes(List.of("collectTerm", "metadata", "presentation",
				"system", "text", "user", "permissionRole", "permissionRule", "validationType"),
				"diva");
	}

	@Test(enabled = false)
	public void runLocalAlvinPSurroundingContainer() throws Exception {
		UpdateAllRecordsAndLinkValidationType updater = createLocalAlvinUpdater();
		updater.updateOnlyRecordsForTheseTypes(List.of("presentationSurroundingContainer"), "cora");
	}

	@Test(enabled = false)
	public void runLocalText() throws Exception {
		UpdateAllRecordsAndLinkValidationType updater = createLocalAlvinUpdater();
		updater.updateOnlyRecordsForTheseTypes(List.of("text", "coraText", "systemOneText"),
				"cora");
	}

	@Test(enabled = false)
	public void runLocalItems() throws Exception {
		UpdateAllRecordsAndLinkValidationType updater = createLocalAlvinUpdater();
		updater.updateOnlyRecordsForTheseTypes(List.of("metadataCollectionItem",
				"languageCollectionItem", "countryCollectionItem"), "cora");
	}

	@Test(enabled = false)
	public void runLocalItems2() throws Exception {
		UpdateAllRecordsAndLinkValidationType updater = createLocalAlvinUpdater();
		updater.updateOnlyRecordsForTheseTypes(List.of("genericCollectionItem"), "cora");
	}

	@Test(enabled = false)
	public void addMissingTextAndDefTextLocalAlvin() throws Exception {

		UpdateAllRecordsAndLinkValidationType updater = new UpdateAllRecordsAndLinkValidationType(
				ALVIN_LOCAL_APPTOKEN_URL, ALVIN_LOCAL_BASE_URL, SYSTEMONE_USER,
				SYSTEMONE_APPTOKEN_USER);

		updater.addMissingTextsAndDefTexts();
	}

	@Test(enabled = false)
	public void runAllAlvin() throws Exception {
		UpdateAllRecordsAndLinkValidationType updater = createLocalAlvinUpdater();
		updater.updateAllRecordsExceptTheseTypes(List.of());
		// updater.updateAllRecordsExceptTheseTypes(List.of("presentationSurroundingContainer",
		// "text",
		// "coraText", "systemOneText", "metadataCollectionItem", "languageCollectionItem",
		// "countryCollectionItem", "genericCollectionItem", "languageMaterial",
		// "locationUnit", "soundRecording", "musicalWork", "location"));
	}

	private DataClient createDataClient(String apptokenUrl, String baseUrl, String user,
			String appToken) {
		DataClientFactoryImp dataClientFactory = DataClientFactoryImp
				.usingAppTokenVerifierUrlAndBaseUrl(apptokenUrl, baseUrl);
		return dataClientFactory.factorUsingUserIdAndAppToken(user, appToken);
	}

	@Test(enabled = false)
	public void addMissingTextAndDefTextLocalDiva() throws Exception {

		UpdateAllRecordsAndLinkValidationType updater = new UpdateAllRecordsAndLinkValidationType(
				DIVA_LOCAL_APPTOKEN_URL, DIVA_LOCAL_BASE_URL, DIVA_USER, DIVA_APPTOKEN_USER);

		updater.addMissingTextsAndDefTexts();
	}

	@Test(enabled = false)
	public void runDevSystemOne() throws Exception {

		UpdateAllRecordsAndLinkValidationType updater = new UpdateAllRecordsAndLinkValidationType(
				SYSTEMONE_DEV_APPTOKEN_URL, SYSTEMONE_DEV_BASE_URL, SYSTEMONE_USER,
				SYSTEMONE_APPTOKEN_USER);

		updater.updateAllRecords(Collections.emptyList());
	}

	@Test(enabled = false)
	public void addMissingTextAndDefTextLocalSystemOne() throws Exception {

		UpdateAllRecordsAndLinkValidationType updater = new UpdateAllRecordsAndLinkValidationType(
				SYSTEMONE_LOCAL_APPTOKEN_URL, SYSTEMONE_LOCAL_BASE_URL, SYSTEMONE_USER,
				SYSTEMONE_APPTOKEN_USER);

		updater.addMissingTextsAndDefTexts();
	}

	@Test(enabled = false)
	public void addMissingTextAndDefTextDEVSystemOne() throws Exception {

		UpdateAllRecordsAndLinkValidationType updater = new UpdateAllRecordsAndLinkValidationType(
				SYSTEMONE_DEV_APPTOKEN_URL, SYSTEMONE_DEV_BASE_URL, SYSTEMONE_USER,
				SYSTEMONE_APPTOKEN_USER);

		updater.addMissingTextsAndDefTexts();
	}

}
