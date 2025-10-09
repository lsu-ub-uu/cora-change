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

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.change.spy.DataClientSpy;
import se.uu.ub.cora.change.spy.JavaClientFactorySpy;
import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.clientdata.spies.ClientDataFactorySpy;
import se.uu.ub.cora.clientdata.spies.ClientDataListSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordGroupSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordSpy;
import se.uu.ub.cora.javaclient.JavaClientAppTokenCredentials;
import se.uu.ub.cora.javaclient.JavaClientProvider;

public class RecordTypeSetIdSourceTest {

	private static final String LOGIN_URL = "someLoginUrl";
	private static final String REST_URL = "someRestUrl";
	private static final String LOGIN_ID = "someLoginId";
	private static final String APPTOKEN = "someAppToken";
	private JavaClientAppTokenCredentials appTokenCred;

	private RecordTypeSetIdSource updater;

	private JavaClientFactorySpy javaClientFactory;

	private static final String SYSTEMONE_USER = "systemoneAdmin@system.cora.uu.se";
	private static final String SYSTEMONE_APPTOKEN = "5d3f3ed4-4931-4924-9faa-8eaf5ac6457e";

	private static final String ALVIN_USER = "alvinAdmin@cora.epc.ub.uu.se";
	private static final String ALVIN_APPTOKEN = "a50ca087-a3f5-4393-b2bb-315436d3c3be";

	private static final String DIVA_USER = "divaAdmin@cora.epc.ub.uu.se";
	private static final String DIVA_APPTOKEN = "49ce00fb-68b5-4089-a5f7-1c225d3cf156";

	private static final String SYSTEMONE_UTV_LOGIN_URL = "http://localhost:8180/login/rest/";
	private static final String SYSTEMONE_UTV_REST_URL = "http://localhost:8080/systemone/rest/";

	private static final String ALVIN_UTV_LOGIN_URL = "http://localhost:8181/login/rest/";
	private static final String ALVIN_UTV_REST_URL = "http://localhost:8081/alvin/rest/";

	private static final String DIVA_UTV_LOGIN_URL = "http://localhost:8182/login/rest/";
	private static final String DIVA_UTV_REST_URL = "http://localhost:8082/diva/rest/";

	private static final String SYSTEMONE_DEV_LOGIN_URL = "http://130.238.171.238:38180/login/rest/";
	private static final String SYSTEMONE_DEV_REST_URL = "http://130.238.171.238:38080/systemone/rest/";

	private static final String ALVIN_DEV_LOGIN_URL = "http://130.238.171.238:38181/login/rest/";
	private static final String ALVIN_DEV_REST_URL = "http://130.238.171.238:38081/alvin/rest/";

	private static final String DIVA_DEV_LOGIN_URL = "http://130.238.171.238:38182/login/rest/";
	private static final String DIVA_DEV_REST_URL = "http://130.238.171.238:38082/diva/rest/";
	private DataClientSpy dataClient;
	private ClientDataListSpy clientDataListSpy;
	private ClientDataRecordGroupSpy dataRecordGroup001;
	private ClientDataFactorySpy clientDataFactory;

	@BeforeMethod
	public void beforeMethod() {
		appTokenCred = new JavaClientAppTokenCredentials(REST_URL, LOGIN_URL, LOGIN_ID, APPTOKEN);
		setUpJavaClient();

		clientDataFactory = new ClientDataFactorySpy();
		ClientDataProvider.onlyForTestSetDataFactory(clientDataFactory);

		updater = new RecordTypeSetIdSource(appTokenCred);
	}

	private void setUpJavaClient() {
		javaClientFactory = new JavaClientFactorySpy();
		JavaClientProvider.onlyForTestSetJavaClientFactory(javaClientFactory);
		dataClient = new DataClientSpy();
		javaClientFactory.MRV.setDefaultReturnValuesSupplier(
				"factorDataClientUsingJavaClientAppTokenCredentials", () -> dataClient);

		clientDataListSpy = new ClientDataListSpy();
		dataClient.MRV.setDefaultReturnValuesSupplier("readList", () -> clientDataListSpy);

		List<ClientData> recordTypeList = new ArrayList<>();
		ClientDataRecordSpy recordType001 = new ClientDataRecordSpy();
		recordTypeList.add(recordType001);
		clientDataListSpy.MRV.setDefaultReturnValuesSupplier("getDataList", () -> recordTypeList);

		dataRecordGroup001 = new ClientDataRecordGroupSpy();
		recordType001.MRV.setDefaultReturnValuesSupplier("getDataRecordGroup",
				() -> dataRecordGroup001);

		dataRecordGroup001.MRV.setDefaultReturnValuesSupplier("getType", () -> "sometype");
		dataRecordGroup001.MRV.setDefaultReturnValuesSupplier("getId", () -> "someId");
	}

	@Test
	public void testConstructorFactorsDataClient() {
		javaClientFactory.MCR.assertParameters("factorDataClientUsingJavaClientAppTokenCredentials",
				0, appTokenCred);
	}

	@Test
	public void testAddSourceId_timestamp() {
		dataRecordGroup001.MRV.setDefaultReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> "false");

		updater.addSourceIdToAllRecordTypes();

		assertReadUserSuppliedId();
		clientDataFactory.MCR.assertParameters("factorAtomicUsingNameInDataAndValue", 0, "idSource",
				"timestamp");
	}

	@Test
	public void testAddSourceId_userSupplied() {
		dataRecordGroup001.MRV.setDefaultReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> "true");

		updater.addSourceIdToAllRecordTypes();

		assertReadUserSuppliedId();
		clientDataFactory.MCR.assertParameters("factorAtomicUsingNameInDataAndValue", 0, "idSource",
				"userSupplied");
	}

	@Test
	public void testAddSourceId_idSourceExists() {
		dataRecordGroup001.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData",
				() -> true, "idSource");

		updater.addSourceIdToAllRecordTypes();

		assertReadIdSource();
	}

	private void assertReadUserSuppliedId() {
		dataClient.MCR.assertParameters("readList", 0);
		clientDataListSpy.MCR.assertParameters("getDataList", 0);
		dataRecordGroup001.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0,
				"userSuppliedId");
	}

	private void assertReadIdSource() {
		dataClient.MCR.assertParameters("readList", 0);
		clientDataListSpy.MCR.assertParameters("getDataList", 0);
		dataRecordGroup001.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "idSource");
	}

	@Test(enabled = false)
	public void testListRecordTypes_SystemOne_Utv() {
		var credentials = new JavaClientAppTokenCredentials(SYSTEMONE_UTV_REST_URL,
				SYSTEMONE_UTV_LOGIN_URL, SYSTEMONE_USER, SYSTEMONE_APPTOKEN);
		updater = new RecordTypeSetIdSource(credentials);
		updater.addSourceIdToAllRecordTypes();
	}

	@Test(enabled = false)
	public void testListRecordTypes_Alvin_Utv() {
		var credentials = new JavaClientAppTokenCredentials(ALVIN_UTV_REST_URL, ALVIN_UTV_LOGIN_URL,
				ALVIN_USER, ALVIN_APPTOKEN);
		updater = new RecordTypeSetIdSource(credentials);
		updater.addSourceIdToAllRecordTypes();
	}

	@Test(enabled = false)
	public void testListRecordTypes_Diva_Utv() {
		var credentials = new JavaClientAppTokenCredentials(DIVA_UTV_REST_URL, DIVA_UTV_LOGIN_URL,
				DIVA_USER, DIVA_APPTOKEN);
		updater = new RecordTypeSetIdSource(credentials);
		updater.addSourceIdToAllRecordTypes();
	}

	@Test(enabled = false)
	public void testListRecordTypes_SystemOne_Dev() {
		var credentials = new JavaClientAppTokenCredentials(SYSTEMONE_DEV_REST_URL,
				SYSTEMONE_DEV_LOGIN_URL, SYSTEMONE_USER, SYSTEMONE_APPTOKEN);
		RecordTypeSetIdSource realUpdater = new RecordTypeSetIdSource(credentials);
		realUpdater.addSourceIdToAllRecordTypes();
	}

	@Test(enabled = false)
	public void testListRecordTypes_Alvin_Dev() {
		var credentials = new JavaClientAppTokenCredentials(ALVIN_DEV_REST_URL, ALVIN_DEV_LOGIN_URL,
				ALVIN_USER, ALVIN_APPTOKEN);
		RecordTypeSetIdSource realUpdater = new RecordTypeSetIdSource(credentials);
		realUpdater.addSourceIdToAllRecordTypes();
	}

	@Test(enabled = false)
	public void testListRecordTypes_Diva_Dev() {
		var credentials = new JavaClientAppTokenCredentials(DIVA_DEV_REST_URL, DIVA_DEV_LOGIN_URL,
				DIVA_USER, DIVA_APPTOKEN);
		updater = new RecordTypeSetIdSource(credentials);
		updater.addSourceIdToAllRecordTypes();
	}
}
