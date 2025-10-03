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
import java.util.Optional;
import java.util.Set;

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataAttribute;
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

public class PresentationAddValidationTypeLink {

	private static final String REF_GROUP = "refGroup";
	private static final String REF = "ref";
	private static final String CHILD_REFERENCES = "childReferences";
	private static final String CHILD_REFERENCE = "childReference";
	private DataClient dataClient;
	private ClientDataGroup inputLink;
	private ClientDataGroup outputLink;
	private int groupsUpdated;
	private ClientDataGroup referenceToValidationLinkText;

	public PresentationAddValidationTypeLink(String apptokenUrl, String baseUrl) {
		JavaClientAppTokenCredentials appTokenCredentials = new JavaClientAppTokenCredentials(
				baseUrl, apptokenUrl, "systemoneAdmin@system.cora.uu.se",
				"5d3f3ed4-4931-4924-9faa-8eaf5ac6457e");
		dataClient = JavaClientProvider
				.createDataClientUsingJavaClientAppTokenCredentials(appTokenCredentials);
		groupsUpdated = 0;
		inputLink = createNewValidationLinkPresentation("validationTypePLink");
		outputLink = createNewValidationLinkPresentation("validationTypeOutputPLink");
		referenceToValidationLinkText = createTextReferenceForValidationLink();
	}

	private ClientDataGroup createNewValidationLinkPresentation(String validationTypePLinkId) {
		ClientDataGroup childReference = ClientDataProvider
				.createGroupUsingNameInData(CHILD_REFERENCE);
		childReference.setRepeatId("111");

		ClientDataGroup refGroup = ClientDataProvider.createGroupUsingNameInData(REF_GROUP);
		refGroup.setRepeatId("111");
		childReference.addChild(refGroup);

		ClientDataRecordLink link = ClientDataProvider.createRecordLinkUsingNameInDataAndTypeAndId(
				REF, "presentationRecordLink", validationTypePLinkId);
		link.addAttributeByIdWithValue("type", "presentation");
		refGroup.addChild(link);

		return childReference;
	}

	private ClientDataGroup createTextReferenceForValidationLink() {
		ClientDataGroup childReference = ClientDataProvider
				.createGroupUsingNameInData(CHILD_REFERENCE);
		childReference.setRepeatId("112");

		ClientDataGroup refGroup = ClientDataProvider.createGroupUsingNameInData(REF_GROUP);
		refGroup.setRepeatId("112");
		childReference.addChild(refGroup);

		ClientDataRecordLink link = ClientDataProvider.createRecordLinkUsingNameInDataAndTypeAndId(
				REF, "coraText", "validationTypeLinkText");
		link.addAttributeByIdWithValue("type", "text");
		refGroup.addChild(link);

		return childReference;
	}

	public void updateAllPresentationsAndAddAValidationType() {
		List<ClientData> allRecordTypes = readAllRecordTypes();
		Set<ClientDataRecordGroup> allPresentationsForRecordInfosFromRecordTypes = getAllPresentationsForRecordInfosFromRecordTypes(
				allRecordTypes);

		updateRecordInfoPGroupRecords(allPresentationsForRecordInfosFromRecordTypes);
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

	private Set<ClientDataRecordGroup> getAllPresentationsForRecordInfosFromRecordTypes(
			List<ClientData> listOfRecordTypes) {
		Set<ClientDataRecordGroup> recordInfoRecordsToModify = new LinkedHashSet<>();
		Set<String> addedRecordInfoIds = new LinkedHashSet<>();
		for (ClientData recordType : listOfRecordTypes) {
			extractPresentationGroups(recordInfoRecordsToModify, addedRecordInfoIds,
					(ClientDataRecord) recordType, "presentationViewId", "newPresentationFormId",
					"presentationFormId");
		}
		return recordInfoRecordsToModify;
	}

	private void extractPresentationGroups(Set<ClientDataRecordGroup> recordInfoRecordsToModify,
			Set<String> addedRecordInfoIds, ClientDataRecord recordType,
			String... presentationNames) {
		ClientDataRecordGroup dataRecordGroup = recordType.getDataRecordGroup();
		for (String presentationName : presentationNames) {
			extractAndAddGroup(recordInfoRecordsToModify, addedRecordInfoIds, dataRecordGroup,
					presentationName);
		}
	}

	private void extractAndAddGroup(Set<ClientDataRecordGroup> recordInfoRecordsToModify,
			Set<String> addedRecordInfoIds, ClientDataRecordGroup dataRecordGroup,
			String presentationName) {
		Optional<ClientDataRecordGroup> oRecordInfoPGroup = getPresentationGroup(dataRecordGroup,
				presentationName);
		if (oRecordInfoPGroup.isPresent()) {
			ClientDataRecordGroup recordInfoPGroup = oRecordInfoPGroup.get();
			if (!addedRecordInfoIds.contains(recordInfoPGroup.getId())) {
				addedRecordInfoIds.add(recordInfoPGroup.getId());
				recordInfoRecordsToModify.add(recordInfoPGroup);
			}
		}
	}

	private Optional<ClientDataRecordGroup> getPresentationGroup(
			ClientDataRecordGroup dataRecordGroup, String nameInData) {
		ClientDataRecordLink linkNew = (ClientDataRecordLink) dataRecordGroup
				.getFirstChildWithNameInData(nameInData);
		return getPresentationGroupForRecordInfo(linkNew);
	}

	private Optional<ClientDataRecordGroup> getPresentationGroupForRecordInfo(
			ClientDataRecordLink linkToDataGroup) {
		List<ClientDataChild> childRefrences = getChildren(linkToDataGroup);
		System.out.print(".");
		for (ClientDataChild childReference : childRefrences) {
			ClientDataGroup refGrorup = (ClientDataGroup) ((ClientDataGroup) childReference)
					.getFirstChildWithNameInData(REF_GROUP);
			ClientDataRecordLink refLink = (ClientDataRecordLink) refGrorup
					.getFirstChildWithNameInData("ref");
			ClientDataAttribute attribute = refLink.getAttribute("type");
			if (attribute.getValue().equals("presentation")) {
				ClientDataRecordGroup presentationGroup = dataClient
						.read(refLink.getLinkedRecordType(), refLink.getLinkedRecordId())
						.getDataRecordGroup();

				if (presentationGroup.containsChildWithNameInData("presentationOf")) {
					// System.out.println("NO presentation of.... " + refLink.getLinkedRecordId());
					ClientDataRecordLink presentationOf = (ClientDataRecordLink) presentationGroup
							.getFirstChildWithNameInData("presentationOf");
					ClientDataRecordGroup metadataGroupForPresentation = dataClient
							.read(presentationOf.getLinkedRecordType(),
									presentationOf.getLinkedRecordId())
							.getDataRecordGroup();
					if (metadataGroupForPresentation.getFirstAtomicValueWithNameInData("nameInData")
							.equals("recordInfo")) {
						return Optional.of(presentationGroup);
					}
				}
			}
		}
		return Optional.empty();
	}

	private List<ClientDataChild> getChildren(ClientDataRecordLink linkToDataGroup) {
		ClientDataRecord metadataGroupRecord = dataClient
				.read(linkToDataGroup.getLinkedRecordType(), linkToDataGroup.getLinkedRecordId());
		ClientDataRecordGroup metadataGroup = metadataGroupRecord.getDataRecordGroup();
		ClientDataGroup childReferencesGroup = metadataGroup
				.getFirstGroupWithNameInData(CHILD_REFERENCES);
		return childReferencesGroup.getAllChildrenWithNameInData(CHILD_REFERENCE);
	}

	private void updateRecordInfoPGroupRecords(
			Set<ClientDataRecordGroup> recordInfoRecordsToModify) {
		for (ClientDataRecordGroup recordInfoRecordToModify : recordInfoRecordsToModify) {
			addLinkAndUpdate(recordInfoRecordToModify);
		}
	}

	private void addLinkAndUpdate(ClientDataRecordGroup recordInfoRecordToModify) {
		// if (recordInfoRecordToModify.containsChildWithNameInData(REF_PARENT_ID)) {
		// ClientDataRecord parent = readParentRecord(recordInfoRecordToModify);
		// addLinkAndUpdate(parent.getDataRecordGroup());
		// }
		updateGroup(recordInfoRecordToModify);
	}

	// private ClientDataRecord readParentRecord(ClientDataRecordGroup recordInfoRecordToModify) {
	// ClientDataRecordLink parentLink = (ClientDataRecordLink) recordInfoRecordToModify
	// .getFirstChildWithNameInData(REF_PARENT_ID);
	// return dataClient.read(parentLink.getLinkedRecordType(), parentLink.getLinkedRecordId());
	// }

	private void updateGroup(ClientDataRecordGroup recordInfoRecordToModify) {
		if (!hasLinkToValidationTypePresentationSinceBefore(recordInfoRecordToModify)) {
			addValidationTypePresentationToGroup(recordInfoRecordToModify);
		} else {
			System.out.println(
					"Already has validation type link: " + recordInfoRecordToModify.getId());
		}
	}

	private void addValidationTypePresentationToGroup(
			ClientDataRecordGroup presentationRecordGroup) {
		ClientDataGroup childReferences = presentationRecordGroup
				.getFirstGroupWithNameInData(CHILD_REFERENCES);

		childReferences.addChild(referenceToValidationLinkText);

		if (presentationRecordGroup.getFirstAtomicValueWithNameInData("mode").equals("input")) {
			childReferences.addChild(inputLink);
			System.out.println("Adding inputLink: " + presentationRecordGroup.getId());
		} else {
			childReferences.addChild(outputLink);
			System.out.println("Adding outputLink: " + presentationRecordGroup.getId());
		}

		dataClient.update(presentationRecordGroup.getType(), presentationRecordGroup.getId(),
				presentationRecordGroup);
		System.out.println("recordInfo: " + presentationRecordGroup.getId());
		groupsUpdated++;
	}

	private boolean hasLinkToValidationTypePresentationSinceBefore(
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
			ClientDataGroup refGroup = childRefrence.getFirstGroupWithNameInData(REF_GROUP);
			ClientDataRecordLink refLink = (ClientDataRecordLink) refGroup
					.getFirstChildWithNameInData(REF);
			if (isAValidationTypePresentation(refLink)) {
				return true;
			}
		}
		return false;
	}

	private boolean isAValidationTypePresentation(ClientDataRecordLink refLink) {
		return ("validationTypePLink".equals(refLink.getLinkedRecordId()))
				|| ("validationTypeOutputPLink".equals(refLink.getLinkedRecordId()));
	}

}
