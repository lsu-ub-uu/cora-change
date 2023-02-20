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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataChild;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.javaclient.cora.DataClient;
import se.uu.ub.cora.javaclient.cora.DataClientFactoryImp;

public class RecordTypeUpdater {

	private static final String CHILD_REFERENCES = "childReferences";
	private static final String CHILD_REFERENCE = "childReference";
	// private String apptokenUrl = "https://cora.epc.ub.uu.se/systemone/apptokenverifier/rest/";
	// private String baseUrl = "https://cora.epc.ub.uu.se/systemone/rest/";
	private String apptokenUrl = "http://localhost:8180/apptokenverifier/rest/";
	private String baseUrl = "http://localhost:8080/systemone/rest/";
	private DataClientFactoryImp dataClientFactory;
	private DataClient dataClient;
	private ClientDataGroup validationLinkChildReference;

	public RecordTypeUpdater() {
		dataClientFactory = DataClientFactoryImp.usingAppTokenVerifierUrlAndBaseUrl(apptokenUrl,
				baseUrl);
		dataClient = dataClientFactory.factorUsingUserIdAndAppToken("141414",
				"63e6bd34-02a1-4c82-8001-158c104cae0e");
		validationLinkChildReference = createNewValidationLinkReference();
	}

	private ClientDataGroup createNewValidationLinkReference() {
		ClientDataGroup childReference = ClientDataProvider
				.createGroupUsingNameInData(CHILD_REFERENCE);
		childReference.setRepeatId("111");
		childReference
				.addChild(ClientDataProvider.createAtomicUsingNameInDataAndValue("repeatMin", "0"));
		childReference
				.addChild(ClientDataProvider.createAtomicUsingNameInDataAndValue("repeatMax", "0"));
		childReference.addChild(ClientDataProvider.createRecordLinkUsingNameInDataAndTypeAndId(
				"ref", "metadataRecordLink", "validationTypeLink"));
		return childReference;
	}

	public void updateAllRecordInfosForAllGroupForAllRecordTypes() throws Exception {
		List<ClientData> listOfRecordTypes = readAllRecordTypes();
		Set<ClientDataRecordGroup> recordInfoIds = getAllRecordInfosFromRecordTypes(
				listOfRecordTypes);
		updateRecordInfos(recordInfoIds);
	}

	private List<ClientData> readAllRecordTypes() {
		ClientDataList readRecordTypes = dataClient.readList("recordType");
		return readRecordTypes.getDataList();
	}

	private Set<ClientDataRecordGroup> getAllRecordInfosFromRecordTypes(
			List<ClientData> listOfRecordTypes) {
		Set<ClientDataRecordGroup> recordInfoIds = new LinkedHashSet<>();
		Set<String> addedRecordInfoIds = new LinkedHashSet<>();
		for (ClientData recordType : listOfRecordTypes) {
			extractGroupAndNewGroup(recordInfoIds, addedRecordInfoIds,
					(ClientDataRecord) recordType);
		}
		return recordInfoIds;
	}

	private void extractGroupAndNewGroup(Set<ClientDataRecordGroup> recordInfoIds,
			Set<String> addedRecordInfoIds, ClientDataRecord recordType) {
		ClientDataRecordGroup dataRecordGroup = recordType.getDataRecordGroup();

		extractAndAddGroup(recordInfoIds, addedRecordInfoIds, dataRecordGroup, "newMetadataId");
		extractAndAddGroup(recordInfoIds, addedRecordInfoIds, dataRecordGroup, "metadataId");
	}

	private void extractAndAddGroup(Set<ClientDataRecordGroup> recordInfoIds,
			Set<String> addedRecordInfoIds, ClientDataRecordGroup dataRecordGroup,
			String groupNameInData) {
		ClientDataRecordGroup recordInfoGroup = getRecordInfo(dataRecordGroup, groupNameInData);
		if (!addedRecordInfoIds.contains(recordInfoGroup.getId())) {
			addedRecordInfoIds.add(recordInfoGroup.getId());
			recordInfoIds.add(recordInfoGroup);
		}
	}

	private ClientDataRecordGroup getRecordInfo(ClientDataRecordGroup dataRecordGroup,
			String nameInData) {
		ClientDataRecordLink linkNew = (ClientDataRecordLink) dataRecordGroup
				.getFirstChildWithNameInData(nameInData);
		return getRecordInfoFromDataGroupLink(linkNew);
	}

	private ClientDataRecordGroup getRecordInfoFromDataGroupLink(
			ClientDataRecordLink linkToDataGroup) {
		List<ClientDataChild> children = getChildren(linkToDataGroup);
		for (ClientDataChild clientDataChild : children) {
			ClientDataRecord mightBeRecordInfoGroup = readChildren(clientDataChild);
			if (mightBeRecordInfoGroup.getDataRecordGroup()
					.getFirstAtomicValueWithNameInData("nameInData").equals("recordInfo")) {
				return mightBeRecordInfoGroup.getDataRecordGroup();
			}
		}
		throw new RuntimeException(
				"NO RECORDINFO FOUND FOR:" + linkToDataGroup.getLinkedRecordId());
	}

	private List<ClientDataChild> getChildren(ClientDataRecordLink linkToDataGroup) {
		ClientDataRecord metadataGroupRecord = dataClient
				.read(linkToDataGroup.getLinkedRecordType(), linkToDataGroup.getLinkedRecordId());
		ClientDataRecordGroup metadataGroup = metadataGroupRecord.getDataRecordGroup();
		ClientDataGroup childReferencesGroup = metadataGroup
				.getFirstGroupWithNameInData(CHILD_REFERENCES);
		return childReferencesGroup.getAllChildrenWithNameInData(CHILD_REFERENCE);
	}

	private ClientDataRecord readChildren(ClientDataChild clientDataChild) {
		ClientDataRecordLink ref = (ClientDataRecordLink) ((ClientDataGroup) clientDataChild)
				.getFirstChildWithNameInData("ref");
		return dataClient.read(ref.getLinkedRecordType(), ref.getLinkedRecordId());
	}

	private void updateRecordInfos(Set<ClientDataRecordGroup> recordsToModify) {
		for (ClientDataRecordGroup clientDataRecordGroup : recordsToModify) {
			addLinkAndUpdate(clientDataRecordGroup);
		}
	}

	private boolean hasLinkToValidationTypeSinceBefore(
			ClientDataRecordGroup clientDataRecordGroup) {
		ClientDataGroup childRefrences = clientDataRecordGroup
				.getFirstGroupWithNameInData(CHILD_REFERENCES);
		List<ClientDataGroup> allChildRefrences = childRefrences
				.getAllGroupsWithNameInData(CHILD_REFERENCE);
		return hasValidationType(allChildRefrences);
	}

	private boolean hasValidationType(List<ClientDataGroup> allChildRefrences) {
		for (ClientDataGroup childRefrence : allChildRefrences) {
			ClientDataRecordLink refLink = (ClientDataRecordLink) childRefrence
					.getFirstChildWithNameInData("ref");
			if ("validationTypeLink".equals(refLink.getLinkedRecordId())) {
				return true;
			}
		}
		return false;
	}

	private void addLinkAndUpdate(ClientDataRecordGroup clientDataRecordGroup) {
		if (clientDataRecordGroup.containsChildWithNameInData("refParentId")) {
			ClientDataRecord parent = readParentRecord(clientDataRecordGroup);
			addLinkAndUpdate(parent.getDataRecordGroup());
		}
		updateGroup(validationLinkChildReference, clientDataRecordGroup);
	}

	private ClientDataRecord readParentRecord(ClientDataRecordGroup clientDataRecordGroup) {
		ClientDataRecordLink parentLink = (ClientDataRecordLink) clientDataRecordGroup
				.getFirstChildWithNameInData("refParentId");
		return dataClient.read(parentLink.getLinkedRecordType(), parentLink.getLinkedRecordId());
	}

	private void updateGroup(ClientDataGroup validationLinkChildReference,
			ClientDataRecordGroup clientDataRecordGroup) {
		if (!hasLinkToValidationTypeSinceBefore(clientDataRecordGroup)) {
			addValidationTypeToGroup(validationLinkChildReference, clientDataRecordGroup);
		} else {
			System.out
					.println("Already has validation type link: " + clientDataRecordGroup.getId());
		}
	}

	private void addValidationTypeToGroup(ClientDataGroup validationLinkChildReference,
			ClientDataRecordGroup clientDataRecordGroup) {
		ClientDataGroup childReferences = clientDataRecordGroup
				.getFirstGroupWithNameInData(CHILD_REFERENCES);
		childReferences.addChild(validationLinkChildReference);
		dataClient.update(clientDataRecordGroup.getType(), clientDataRecordGroup.getId(),
				clientDataRecordGroup);
		System.out.println("recordInfo: " + clientDataRecordGroup.getId());
	}

}
