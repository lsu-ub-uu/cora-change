/*
 * Copyright 2023, 2025 Uppsala University Library
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
import se.uu.ub.cora.javaclient.JavaClientAppTokenCredentials;
import se.uu.ub.cora.javaclient.JavaClientProvider;
import se.uu.ub.cora.javaclient.data.DataClient;

public class RecordTypeSetValidationTypeMandatory {

	private static final String REF_PARENT_ID = "refParentId";
	private static final String REF = "ref";
	private static final String CHILD_REFERENCES = "childReferences";
	private static final String CHILD_REFERENCE = "childReference";
	private DataClient dataClient;
	private int groupsUpdated;

	public RecordTypeSetValidationTypeMandatory(String apptokenUrl, String baseUrl) {
		JavaClientAppTokenCredentials appTokenCredentials = new JavaClientAppTokenCredentials(
				baseUrl, apptokenUrl, "systemoneAdmin@system.cora.uu.se",
				"5d3f3ed4-4931-4924-9faa-8eaf5ac6457e");
		dataClient = JavaClientProvider
				.createDataClientUsingJavaClientAppTokenCredentials(appTokenCredentials);
		groupsUpdated = 0;
	}

	public void updateAllRecordInfosForAllGroupForAllRecordTypes() {
		List<ClientData> listOfRecordTypes = readAllRecordTypes();
		Set<ClientDataRecordGroup> recordInfoRecordsToModify = getAllRecordInfosFromRecordTypes(
				listOfRecordTypes);
		updateRecordInfoRecords(recordInfoRecordsToModify);
		writeReport();
	}

	private void writeReport() {
		System.out.println();
		System.out.println("==============================");
		System.out.println(groupsUpdated + " gorups updated");
	}

	private List<ClientData> readAllRecordTypes() {
		ClientDataList readRecordTypes = dataClient.readList("recordType");
		return readRecordTypes.getDataList();
	}

	private Set<ClientDataRecordGroup> getAllRecordInfosFromRecordTypes(
			List<ClientData> listOfRecordTypes) {
		Set<ClientDataRecordGroup> recordInfoRecordsToModify = new LinkedHashSet<>();
		Set<String> addedRecordInfoIds = new LinkedHashSet<>();
		for (ClientData recordType : listOfRecordTypes) {
			extractGivenGroups(recordInfoRecordsToModify, addedRecordInfoIds,
					(ClientDataRecord) recordType, "newMetadataId", "metadataId");
		}
		return recordInfoRecordsToModify;
	}

	private void extractGivenGroups(Set<ClientDataRecordGroup> recordInfoRecordsToModify,
			Set<String> addedRecordInfoIds, ClientDataRecord recordType, String... groupNames) {
		ClientDataRecordGroup dataRecordGroup = recordType.getDataRecordGroup();
		for (String groupName : groupNames) {
			extractAndAddGroup(recordInfoRecordsToModify, addedRecordInfoIds, dataRecordGroup,
					groupName);
		}
	}

	private void extractAndAddGroup(Set<ClientDataRecordGroup> recordInfoRecordsToModify,
			Set<String> addedRecordInfoIds, ClientDataRecordGroup dataRecordGroup,
			String groupNameInData) {
		ClientDataRecordGroup recordInfoGroup = getRecordInfo(dataRecordGroup, groupNameInData);
		if (!addedRecordInfoIds.contains(recordInfoGroup.getId())) {
			addedRecordInfoIds.add(recordInfoGroup.getId());
			recordInfoRecordsToModify.add(recordInfoGroup);
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
				.getFirstChildWithNameInData(REF);
		return dataClient.read(ref.getLinkedRecordType(), ref.getLinkedRecordId());
	}

	private void updateRecordInfoRecords(Set<ClientDataRecordGroup> recordInfoRecordsToModify) {
		for (ClientDataRecordGroup recordInfoRecordToModify : recordInfoRecordsToModify) {
			addLinkAndUpdate(recordInfoRecordToModify);
		}
	}

	private void addLinkAndUpdate(ClientDataRecordGroup recordInfoRecordToModify) {
		if (recordInfoRecordToModify.containsChildWithNameInData(REF_PARENT_ID)) {
			ClientDataRecord parent = readParentRecord(recordInfoRecordToModify);
			addLinkAndUpdate(parent.getDataRecordGroup());
		}
		updateGroup(recordInfoRecordToModify);
	}

	private ClientDataRecord readParentRecord(ClientDataRecordGroup recordInfoRecordToModify) {
		ClientDataRecordLink parentLink = (ClientDataRecordLink) recordInfoRecordToModify
				.getFirstChildWithNameInData(REF_PARENT_ID);
		return dataClient.read(parentLink.getLinkedRecordType(), parentLink.getLinkedRecordId());
	}

	private void updateGroup(ClientDataRecordGroup recordInfoRecordToModify) {
		if (hasLinkToValidationTypeSinceBefore(recordInfoRecordToModify)) {
			setValidationTypeMandatory(recordInfoRecordToModify);
		} else {
			System.out.println(
					"Already has validation type link: " + recordInfoRecordToModify.getId());
		}
	}

	private boolean hasLinkToValidationTypeSinceBefore(
			ClientDataRecordGroup recordInfoRecordToModify) {
		List<ClientDataGroup> allChildRefrences = getChildRefrencesFromGroup(
				recordInfoRecordToModify);
		return hasValidationType(allChildRefrences);
	}

	private List<ClientDataGroup> getChildRefrencesFromGroup(
			ClientDataRecordGroup recordInfoRecordToModify) {
		ClientDataGroup childRefrences = recordInfoRecordToModify
				.getFirstGroupWithNameInData(CHILD_REFERENCES);
		return childRefrences.getAllGroupsWithNameInData(CHILD_REFERENCE);
	}

	private boolean hasValidationType(List<ClientDataGroup> allChildRefrences) {
		for (ClientDataGroup childRefrence : allChildRefrences) {
			ClientDataRecordLink refLink = (ClientDataRecordLink) childRefrence
					.getFirstChildWithNameInData(REF);
			if ("validationTypeLink".equals(refLink.getLinkedRecordId())) {
				return true;
			}
		}
		return false;
	}

	private void setValidationTypeMandatory(ClientDataRecordGroup recordInfoRecordToModify) {
		ClientDataGroup childReferences = recordInfoRecordToModify
				.getFirstGroupWithNameInData(CHILD_REFERENCES);
		boolean updated = false;
		for (ClientDataChild childreference : childReferences.getChildren()) {
			ClientDataGroup childReferenceGroup = (ClientDataGroup) childreference;
			ClientDataRecordLink refLink = childReferenceGroup
					.getFirstChildOfTypeAndName(ClientDataRecordLink.class, "ref");
			if ("validationTypeLink".equals(refLink.getLinkedRecordId())) {
				String repeatMin = childReferenceGroup
						.getFirstAtomicValueWithNameInData("repeatMin");
				if ("0".equals(repeatMin)) {

					childReferenceGroup.removeFirstChildWithNameInData("repeatMin");
					childReferenceGroup.addChild(ClientDataProvider
							.createAtomicUsingNameInDataAndValue("repeatMin", "1"));
					updated = true;
				}
			}
		}
		if (updated) {

			dataClient.update(recordInfoRecordToModify.getType(), recordInfoRecordToModify.getId(),
					recordInfoRecordToModify);
			System.out.println("recordInfo: " + recordInfoRecordToModify.getId());
			groupsUpdated++;
		}
	}
}
