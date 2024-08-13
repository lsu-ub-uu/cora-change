/*
 * Copyright 2023 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.change;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.change.spy.CoraClientFactorySpy;
import se.uu.ub.cora.change.spy.DataClientSpy;
import se.uu.ub.cora.change.spy.HashMapSpy;
import se.uu.ub.cora.change.spy.RecordTypeUtilSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataFactorySpy;
import se.uu.ub.cora.clientdata.spies.ClientDataListSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordGroupSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordLinkSpy;

public class RecordLinkUpdateToTopLevelTypeTest {

	RecordLinkUpdateToTopLevelType recordLinkUpdater;
	RecordTypeUtilSpy recordTypeUtil;

	private static final String SYSTEMONE_LOCAL_APPTOKEN_URL = "http://localhost:8180/login/rest/";
	private static final String SYSTEMONE_LOCAL_BASE_URL = "http://localhost:8080/systemone/rest/";

	private static final String ALVIN_LOCAL_APPTOKEN_URL = "http://localhost:8181/login/rest/";
	private static final String ALVIN_LOCAL_BASE_URL = "http://localhost:8081/alvin/rest/";

	private static final String DIVA_LOCAL_APPTOKEN_URL = "http://localhost:8182/login/rest/";

	private static final String SYSTEMONE_DEV_APPTOKEN_URL = "http://130.238.171.238:38180/login/rest/";
	private static final String SYSTEMONE_DEV_BASE_URL = "http://130.238.171.238:38080/systemone/rest/";

	private static final String ALVIN_DEV_APPTOKEN_URL = "http://130.238.171.238:38181/login/rest/";
	private static final String ALVIN_DEV_BASE_URL = "http://130.238.171.238:38081/alvin/rest/";

	private static final String DIVA_DEV_APPTOKEN_URL = "http://130.238.171.238:38182/login/rest/";
	private static final String DIVA_DEV_BASE_URL = "http://130.238.171.238:38082/diva/rest/";

	private static final String SOME_APPTOKEN_URL = "http://localhost:8180/somelogin/rest/";
	private static final String SOME_BASE_URL = "http://localhost:8080/someBase/rest/";
	private static final Object LINKED_RECORD_TYPE = "linkedRecordType";

	private static final String SYSTEMONE_USER = "jsClientUser@system.cora.uu.se";
	private static final String SYSTEMONE_APPTOKEN_USER = "5d3f3ed4-4931-4924-9faa-8eaf5ac6457e";

	private static final String DIVA_USER = "divaEverything@diva.cora.uu.se";
	private static final String DIVA_APPTOKEN_USER = "77edfec1-e1f1-45d4-a452-411668eba0f0";

	private static final String ALVIN_USER = "alvinAdmin@cora.epc.ub.uu.se";
	private static final String ALVIN_APPTOKEN_USER = "a50ca087-a3f5-4393-b2bb-315436d3c3be";

	CoraClientFactorySpy dataClientFactorySpy;
	ClientDataFactorySpy datafactory;
	private ClientDataRecordGroupSpy recordGroup;
	private DataClientSpy dataClientSpy;
	private ClientDataListSpy dataListSpy;
	private ClientDataRecordGroupSpy clientDataRecordGroup1;
	private ClientDataRecordLinkSpy dataRecordLink1;
	private HashMapSpy mapOfTopLevelTypes;

	@BeforeMethod
	private void beforeMethod() {
		recordTypeUtil = new RecordTypeUtilSpy();
		dataClientFactorySpy = new CoraClientFactorySpy();
		datafactory = new ClientDataFactorySpy();
		recordGroup = new ClientDataRecordGroupSpy();

		// datafactory.MRV.setDefaultReturnValuesSupplier("factorRecordGroupFromDataGroup",
		// () -> recordGroup);
		// ClientDataProvider.onlyForTestSetDataFactory(datafactory);
	}

	@Test(enabled = false)
	public void realRunOnSystemoneLocal() throws Exception {
		recordLinkUpdater = RecordLinkUpdateToTopLevelType
				.usingAppTokenVerifierUrlAndBaseUrlAndRecordTypeUtil(SYSTEMONE_LOCAL_APPTOKEN_URL,
						SYSTEMONE_LOCAL_BASE_URL, SYSTEMONE_USER, SYSTEMONE_APPTOKEN_USER);

		recordLinkUpdater.updateAllRecordLinksWithTopLevelType();
	}

	@Test(enabled = false)
	public void realRunOnSystemoneDEV() throws Exception {
		recordLinkUpdater = RecordLinkUpdateToTopLevelType
				.usingAppTokenVerifierUrlAndBaseUrlAndRecordTypeUtil(SYSTEMONE_DEV_APPTOKEN_URL,
						SYSTEMONE_DEV_BASE_URL, SYSTEMONE_USER, SYSTEMONE_APPTOKEN_USER);

		recordLinkUpdater.updateAllRecordLinksWithTopLevelType();
	}

	@Test(enabled = false)
	public void realRunOnAlvinLocal() throws Exception {
		recordLinkUpdater = RecordLinkUpdateToTopLevelType
				.usingAppTokenVerifierUrlAndBaseUrlAndRecordTypeUtil(ALVIN_LOCAL_APPTOKEN_URL,
						ALVIN_LOCAL_BASE_URL, SYSTEMONE_USER, SYSTEMONE_APPTOKEN_USER);

		recordLinkUpdater.updateAllRecordLinksWithTopLevelType();
	}

	@Test(enabled = false)
	public void realRunOnAlvinDEV() throws Exception {
		recordLinkUpdater = RecordLinkUpdateToTopLevelType
				.usingAppTokenVerifierUrlAndBaseUrlAndRecordTypeUtil(ALVIN_DEV_APPTOKEN_URL,
						ALVIN_DEV_BASE_URL, SYSTEMONE_USER, SYSTEMONE_APPTOKEN_USER);

		recordLinkUpdater.updateAllRecordLinksWithTopLevelType();
	}

	@Test(enabled = false)
	public void realRunOnDIVADEV() throws Exception {
		recordLinkUpdater = RecordLinkUpdateToTopLevelType
				.usingAppTokenVerifierUrlAndBaseUrlAndRecordTypeUtil(DIVA_DEV_APPTOKEN_URL,
						DIVA_DEV_BASE_URL, DIVA_USER, DIVA_APPTOKEN_USER);

		recordLinkUpdater.updateAllRecordLinksWithTopLevelType();
	}

	// @Test
	// public void testCreateClientFactory() throws Exception {
	// recordLinkUpdater = RecordLinkUpdateToTopLevelType
	// .usingAppTokenVerifierUrlAndBaseUrlAndRecordTypeUtil(SOME_APPTOKEN_URL,
	// SOME_BASE_URL);
	//
	// DataClientFactoryImp createdClientFactory = (DataClientFactoryImp) recordLinkUpdater
	// .onlyForTestGetCoraClientFactory();
	//
	// assertTrue(createdClientFactory instanceof DataClientFactoryImp);
	// assertEquals(createdClientFactory.onlyForTestGetAppTokenVerifierUrl(), SOME_APPTOKEN_URL);
	// assertEquals(createdClientFactory.onlyForTestGetBaseUrl(), SOME_BASE_URL);
	// }
	//
	// @Test
	// public void testUpdateClientFactory() throws Exception {
	//
	// recordLinkUpdater = RecordLinkUpdateToTopLevelType
	// .usingAppTokenVerifierUrlAndBaseUrlAndRecordTypeUtil(SOME_APPTOKEN_URL,
	// SOME_BASE_URL);
	//
	// recordLinkUpdater.onlyForTestSetRestClientFactory(dataClientFactorySpy);
	//
	// recordLinkUpdater.onlyForTestGetCoraClientFactory();
	// assertTrue(recordLinkUpdater
	// .onlyForTestGetCoraClientFactory() instanceof CoraClientFactorySpy);
	// recordTypeUtil.MCR.assertParameters("getMapOfImplementingToParent", 0);
	//
	// }
	//
	// @Test
	// public void testFactorClientFactory() throws Exception {
	// recordLinkUpdater = RecordLinkUpdateToTopLevelType
	// .usingAppTokenVerifierUrlAndBaseUrlAndRecordTypeUtil(SOME_APPTOKEN_URL,
	// SOME_BASE_URL);
	//
	// recordLinkUpdater.onlyForTestSetRestClientFactory(dataClientFactorySpy);
	//
	// recordLinkUpdater.updateAllRecordLinksWithTopLevelType();
	//
	// dataClientFactorySpy.MCR.assertParameters("factorUsingUserIdAndAppToken", 0,
	// "jsClientUser@system.cora.uu.se",
	// "5d3f3ed4-4931-4924-9faa-8eaf5ac6457e");
	//
	// }
	//
	// @Test
	// public void testListRecorLinksNotEqual() throws Exception {
	// prepareTest();
	// mapOfTopLevelTypes.MRV.setDefaultReturnValuesSupplier("get", () -> "someTopLevelType");
	//
	// // CREATES recordLinkUpdater
	// recordLinkUpdater = RecordLinkUpdateToTopLevelType
	// .usingAppTokenVerifierUrlAndBaseUrlAndRecordTypeUtil(SOME_APPTOKEN_URL,
	// SOME_BASE_URL);
	// recordLinkUpdater.onlyForTestSetRestClientFactory(dataClientFactorySpy);
	//
	// // RUN TEST
	// recordLinkUpdater.updateAllRecordLinksWithTopLevelType();
	//
	// // ASSERT BEGINS
	// DataClientSpy dataClient = (DataClientSpy) dataClientFactorySpy.MCR
	// .getReturnValue("factorUsingUserIdAndAppToken", 0);
	//
	// assertReadList(dataClient);
	//
	// // ASSERT DATA RECORD clientDataGroup1
	// clientDataRecordGroup1.MCR.assertParameters("getFirstChildOfTypeAndName", 0,
	// ClientDataRecordLink.class, LINKED_RECORD_TYPE);
	// dataRecordLink1.MCR.assertParameters("getLinkedRecordId", 0);
	// }
	//
	// private void prepareTest() {
	// dataRecordLink1 = new ClientDataRecordLinkSpy();
	//
	// clientDataRecordGroup1 = new ClientDataRecordGroupSpy();
	// clientDataRecordGroup1.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
	// () -> dataRecordLink1, ClientDataRecordLink.class, LINKED_RECORD_TYPE);
	//
	// dataListSpy = new ClientDataListSpy();
	// dataListSpy.MRV.setDefaultReturnValuesSupplier("getDataList",
	// () -> List.of(clientDataRecordGroup1));
	//
	// dataClientSpy = new DataClientSpy();
	// dataClientSpy.MRV.setSpecificReturnValuesSupplier("readList", () -> dataListSpy,
	// "metadataRecordLink");
	//
	// dataClientFactorySpy.MRV.setSpecificReturnValuesSupplier("factorUsingUserIdAndAppToken",
	// () -> dataClientSpy, "jsClientUser@system.cora.uu.se",
	// "5d3f3ed4-4931-4924-9faa-8eaf5ac6457e");
	//
	// mapOfTopLevelTypes = new HashMapSpy();
	//
	// }
	//
	// @Test
	// public void testListRecorLinksEqual() throws Exception {
	// prepareTest();
	// mapOfTopLevelTypes.MRV.setDefaultReturnValuesSupplier("get", () -> "someTopLevelType");
	//
	// // CREATES recordLinkUpdater
	// recordLinkUpdater = RecordLinkUpdateToTopLevelType
	// .usingAppTokenVerifierUrlAndBaseUrlAndRecordTypeUtil(SOME_APPTOKEN_URL,
	// SOME_BASE_URL);
	// recordLinkUpdater.onlyForTestSetRestClientFactory(dataClientFactorySpy);
	//
	// // RUN TEST
	// recordLinkUpdater.updateAllRecordLinksWithTopLevelType();
	//
	// // ASSERT BEGINS
	// DataClientSpy dataClient = (DataClientSpy) dataClientFactorySpy.MCR
	// .getReturnValue("factorUsingUserIdAndAppToken", 0);
	//
	// assertReadList(dataClient);
	//
	// // ASSERT DATA RECORD clientDataGroup1
	// clientDataRecordGroup1.MCR.assertParameters("getFirstChildOfTypeAndName", 0,
	// ClientDataRecordLink.class, LINKED_RECORD_TYPE);
	// dataRecordLink1.MCR.assertParameters("getLinkedRecordId", 0);
	//
	// dataClient.MCR.assertMethodNotCalled("update");
	// }
	//
	// private void assertReadList(DataClientSpy dataClient) {
	// dataClient.MCR.assertParameters("readList", 0, "metadataRecordLink");
	// ClientDataListSpy recordLinks = (ClientDataListSpy) dataClient.MCR
	// .getReturnValue("readList", 0);
	// recordLinks.MCR.assertParameters("getDataList", 0);
	// }

}
