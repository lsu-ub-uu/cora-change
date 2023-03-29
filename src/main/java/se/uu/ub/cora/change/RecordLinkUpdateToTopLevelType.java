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

import java.util.List;
import java.util.Map;

import se.uu.ub.cora.change.utils.RecordTypeUtil;
import se.uu.ub.cora.change.utils.RecordTypeUtilImp;
import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.javaclient.cora.CoraClientFactory;
import se.uu.ub.cora.javaclient.cora.DataClient;
import se.uu.ub.cora.javaclient.cora.DataClientFactoryImp;

public class RecordLinkUpdateToTopLevelType {

	private static final String LINKED_RECORD_TYPE = "linkedRecordType";
	private String apptokenUrl;
	private String baseUrl;

	private CoraClientFactory clientFactory;
	private DataClient dataClient;
	private Map<String, String> mapOfTopLevelTypes;
	private String user;
	private String apptoken;

	public static RecordLinkUpdateToTopLevelType usingAppTokenVerifierUrlAndBaseUrlAndRecordTypeUtil(
			String apptokenUrl, String baseUrl, String user, String apptoken) {

		return new RecordLinkUpdateToTopLevelType(apptokenUrl, baseUrl, user, apptoken);
	}

	private RecordLinkUpdateToTopLevelType(String apptokenUrl, String baseUrl, String user,
			String apptoken) {
		this.apptokenUrl = apptokenUrl;
		this.baseUrl = baseUrl;
		this.user = user;
		this.apptoken = apptoken;

		clientFactory = DataClientFactoryImp.usingAppTokenVerifierUrlAndBaseUrl(apptokenUrl,
				baseUrl);

	}

	public void updateAllRecordLinksWithTopLevelType() {
		System.out.println("START SCRIPT");
		factorDataClient();

		ClientDataList recordLinks = dataClient.readList("metadataRecordLink");
		List<ClientData> listOfClientData = recordLinks.getDataList();
		System.out.println("RecordsFound: " + listOfClientData.size());
		// SPIKE
		for (ClientData data : listOfClientData) {
			ClientDataRecord record = (ClientDataRecord) data;
			ClientDataRecordGroup recordGroup = record.getDataRecordGroup();

			System.out.println("RecordId: " + recordGroup.getId());

			ClientDataRecordLink linkedRecordType = recordGroup
					.getFirstChildOfTypeAndName(ClientDataRecordLink.class, LINKED_RECORD_TYPE);

			String linkId = linkedRecordType.getLinkedRecordId();
			String topLevelType = mapOfTopLevelTypes.get(linkId);

			System.out.println("LinkId: " + linkId + " TopLeveleType: " + topLevelType);

			if (!linkId.equals(topLevelType)) {
				System.out.println("Update link!!");
				updateLinkToPointTopLevelType(recordGroup, topLevelType);
			}
			System.out.println();
		}
		System.out.println("STOP SCRIPT");
	}

	private void updateLinkToPointTopLevelType(ClientDataRecordGroup recordGroup,
			String topLevelTypeId) {
		recordGroup.removeFirstChildWithTypeAndName(ClientDataRecordLink.class, LINKED_RECORD_TYPE);

		ClientDataRecordLink newLinkToTopLeveltype = ClientDataProvider
				.createRecordLinkUsingNameInDataAndTypeAndId(LINKED_RECORD_TYPE, "recordType",
						topLevelTypeId);
		recordGroup.addChild(newLinkToTopLeveltype);

		dataClient.update("metadataRecordLink", recordGroup.getId(), recordGroup);
	}

	private void factorDataClient() {
		dataClient = clientFactory.factorUsingUserIdAndAppToken(user, apptoken);

		RecordTypeUtil recordTypeUtil = RecordTypeUtilImp.usingDataClient(dataClient);
		mapOfTopLevelTypes = recordTypeUtil.getMapOfImplementingToParent();
	}

	public String onlyForTestGetApptokenUrl() {
		return apptokenUrl;
	}

	public String onlyForTestGetBaseUrl() {
		return baseUrl;
	}

	public CoraClientFactory onlyForTestGetCoraClientFactory() {
		return clientFactory;
	}

	public void onlyForTestSetRestClientFactory(CoraClientFactory dataClientFactorySpy) {
		clientFactory = dataClientFactorySpy;
	}

}
