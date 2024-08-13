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
package se.uu.ub.cora.change.utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.change.spy.DataClientSpy;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.clientdata.spies.ClientDataListSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordGroupSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordLinkSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordSpy;
import se.uu.ub.cora.javaclient.cora.DataClient;
import se.uu.ub.cora.javaclient.cora.DataClientFactoryImp;

public class RecordTypeUtilTest {
	private static final String PARENT_ID = "parentId";
	private RecordTypeUtilImp recordTypeUtil;
	private DataClientSpy dataClient;
	private List<ClientDataRecord> listOfTypes;

	@BeforeMethod

	private void beforeMethod() {
		listOfTypes = new ArrayList<>();
		dataClient = new DataClientSpy();
		setDataClientToReturnListOfTypes();

		recordTypeUtil = RecordTypeUtilImp.usingDataClient(dataClient);
	}

	private void setDataClientToReturnListOfTypes() {
		ClientDataListSpy clientDataList = new ClientDataListSpy();
		clientDataList.MRV.setDefaultReturnValuesSupplier("getDataList", () -> listOfTypes);
		dataClient.MRV.setSpecificReturnValuesSupplier("readList", () -> clientDataList,
				"recordType");
	}

	@Test
	public void testOnlyForTestGetDataClient() throws Exception {
		DataClient dataClient = recordTypeUtil.onlyForTestGetDataClient();
		assertSame(dataClient, this.dataClient);
	}

	@Test
	public void testInit() throws Exception {
		Map<String, String> mapOfImp = recordTypeUtil.getMapOfImplementingToParent();

		assertNotNull(mapOfImp);
	}

	@Test
	public void testOneRecordTypeWithoutParentReturnsMapWithIdForBothKeyAndValue()
			throws Exception {
		setUpDataClientToReturnOneRecordTypeWithoutParent("someId");

		Map<String, String> mapOfImp = recordTypeUtil.getMapOfImplementingToParent();

		assertEquals(mapOfImp.size(), 1);
		assertEquals(mapOfImp.get("someId"), "someId");
	}

	private void setUpDataClientToReturnOneRecordTypeWithoutParent(String recordId) {
		ClientDataRecordSpy clientDataRecord = createClientDataRecordSpyContainsParentId(recordId,
				false);
		listOfTypes.add(clientDataRecord);
	}

	private ClientDataRecordSpy createClientDataRecordSpyContainsParentId(String recordId,
			boolean containsParentId) {
		ClientDataRecordSpy clientDataRecord = new ClientDataRecordSpy();
		ClientDataRecordGroupSpy recordGroup = new ClientDataRecordGroupSpy();
		clientDataRecord.MRV.setDefaultReturnValuesSupplier("getId", () -> recordId);
		clientDataRecord.MRV.setDefaultReturnValuesSupplier("getDataRecordGroup",
				() -> recordGroup);
		recordGroup.MRV.setSpecificReturnValuesSupplier("containsChildOfTypeAndName",
				() -> containsParentId, ClientDataRecordLink.class, PARENT_ID);
		return clientDataRecord;
	}

	@Test
	public void testTwoRecordTypeThatAreAnImplementingTypes() throws Exception {
		setUpDataClientToReturnOneRecordTypeWithoutParent("someId");
		setUpDataClientToReturnOneRecordTypeWithoutParent("someId2");

		Map<String, String> mapOfImp = recordTypeUtil.getMapOfImplementingToParent();

		assertEquals(mapOfImp.size(), 2);
		assertEquals(mapOfImp.get("someId"), "someId");
		assertEquals(mapOfImp.get("someId2"), "someId2");
	}

	@Test
	public void testOneRecordTypeWithOneParent() throws Exception {
		setUpDataClientToReturnOneRecordTypeWithParent("someId", "someParentId");
		setUpDataClientToReturnOneRecordTypeWithoutParent("someParentId");

		Map<String, String> mapOfImp = recordTypeUtil.getMapOfImplementingToParent();

		assertEquals(mapOfImp.size(), 2);
		assertEquals(mapOfImp.get("someId"), "someParentId");
		assertEquals(mapOfImp.get("someParentId"), "someParentId");
	}

	private void setUpDataClientToReturnOneRecordTypeWithParent(String recordId, String parentId) {
		ClientDataRecordSpy clientDataRecord = createClientDataRecordSpyWithRecordAndParentId(
				recordId, parentId);

		listOfTypes.add(clientDataRecord);
	}

	private ClientDataRecordSpy createClientDataRecordSpyWithRecordAndParentId(String recordId,
			String parentId) {
		ClientDataRecordSpy clientDataRecord = new ClientDataRecordSpy();
		ClientDataRecordGroupSpy recordGroup = new ClientDataRecordGroupSpy();
		clientDataRecord.MRV.setDefaultReturnValuesSupplier("getId", () -> recordId);
		clientDataRecord.MRV.setDefaultReturnValuesSupplier("getDataRecordGroup",
				() -> recordGroup);
		recordGroup.MRV.setSpecificReturnValuesSupplier("containsChildOfTypeAndName", () -> true,
				ClientDataRecordLink.class, PARENT_ID);

		ClientDataRecordLinkSpy parentLink = new ClientDataRecordLinkSpy();
		parentLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId", () -> parentId);
		recordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> parentLink, ClientDataRecordLink.class, PARENT_ID);
		return clientDataRecord;
	}

	@Test
	public void testOneRecordTypeWithOneGrandParent() throws Exception {
		setUpDataClientToReturnOneRecordTypeWithParent("someId", "someParentId");
		setUpDataClientToReturnOneRecordTypeWithParent("someParentId", "someGrandParentId");
		setUpDataClientToReturnOneRecordTypeWithoutParent("someGrandParentId");

		Map<String, String> mapOfImp = recordTypeUtil.getMapOfImplementingToParent();

		assertEquals(mapOfImp.size(), 3);
		assertEquals(mapOfImp.get("someId"), "someGrandParentId");
		assertEquals(mapOfImp.get("someParentId"), "someGrandParentId");
		assertEquals(mapOfImp.get("someGrandParentId"), "someGrandParentId");
	}

	@Test(enabled = false)
	public void realTest() throws Exception {
		String apptokenUrl = "http://130.238.171.238:38180/login/rest/";
		String baseUrl = "http://130.238.171.238:38080/systemone/rest/";
		DataClientFactoryImp dataClientFactory = DataClientFactoryImp
				.usingAppTokenVerifierUrlAndBaseUrl(apptokenUrl, baseUrl);
		DataClient dataClient = dataClientFactory.factorUsingUserIdAndAppToken(
				"jsClientUser@system.cora.uu.se", "5d3f3ed4-4931-4924-9faa-8eaf5ac6457e");
		RecordTypeUtilImp recordTypeUtil = RecordTypeUtilImp.usingDataClient(dataClient);

		Map<String, String> mapOfImp = recordTypeUtil.getMapOfImplementingToParent();
		for (Entry<String, String> entry : mapOfImp.entrySet()) {

			System.out.println(entry.getKey() + " --> " + entry.getValue());
		}
	}
}
