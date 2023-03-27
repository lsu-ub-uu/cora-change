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
import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataProvider;
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

	public static RecordLinkUpdateToTopLevelType usingAppTokenVerifierUrlAndBaseUrlAndRecordTypeUtil(
			String apptokenUrl, String baseUrl, RecordTypeUtil recordTypeUtil) {

		return new RecordLinkUpdateToTopLevelType(apptokenUrl, baseUrl, recordTypeUtil);
	}

	private RecordLinkUpdateToTopLevelType(String apptokenUrl, String baseUrl,
			RecordTypeUtil recordTypeUtil) {
		this.apptokenUrl = apptokenUrl;
		this.baseUrl = baseUrl;

		clientFactory = DataClientFactoryImp.usingAppTokenVerifierUrlAndBaseUrl(apptokenUrl,
				baseUrl);

		mapOfTopLevelTypes = recordTypeUtil.getMapOfImplementingToParent();
	}

	public void updateAllRecordLinksWithTopLevelType() {
		factorDataClient();

		ClientDataList recordLinks = dataClient.readList("metadataRecordLink");
		List<ClientData> listOfClientData = recordLinks.getDataList();
		// SPIKE
		for (ClientData data : listOfClientData) {
			ClientDataGroup group = (ClientDataGroup) data;
			ClientDataRecordLink linkedRecordType = group
					.getFirstChildOfTypeAndName(ClientDataRecordLink.class, LINKED_RECORD_TYPE);
			String linkType = linkedRecordType.getLinkedRecordType();
			String linkId = linkedRecordType.getLinkedRecordId();
			String topLevelType = mapOfTopLevelTypes.get(linkType);

			if (!linkType.equals(topLevelType)) {
				updateLinkToPointTopLevelType(group, linkId, topLevelType);
			}
		}
	}

	private void updateLinkToPointTopLevelType(ClientDataGroup group, String linkId,
			String topLevelType) {
		group.removeFirstChildWithTypeAndName(ClientDataRecordLink.class,
				LINKED_RECORD_TYPE);
		ClientDataRecordLink newLinkToTopLeveltype = ClientDataProvider
				.createRecordLinkUsingNameInDataAndTypeAndId(LINKED_RECORD_TYPE,
						topLevelType, linkId);
		group.addChild(newLinkToTopLeveltype);
		ClientDataRecordGroup recordGroup = ClientDataProvider
				.createRecordGroupFromDataGroup(group);
		dataClient.update("metadataRecordLink", recordGroup.getId(), recordGroup);
	}

	private void factorDataClient() {
		dataClient = clientFactory.factorUsingUserIdAndAppToken("141414",
				"63e6bd34-02a1-4c82-8001-158c104cae0e");
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
