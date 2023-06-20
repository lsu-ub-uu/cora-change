package se.uu.ub.cora.change;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataChild;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.clientdata.converter.ClientDataToJsonConverterFactory;
import se.uu.ub.cora.clientdata.converter.ClientDataToJsonConverterProvider;
import se.uu.ub.cora.javaclient.cora.DataClient;
import se.uu.ub.cora.javaclient.cora.DataClientFactoryImp;

public class SetFinalValueForValidationType {

	private static final String METADATA = "metadata";
	private static final String TEXT_SV = "RecordInfo för {0}";
	private static final String TEXT_EN = "RecordInfo for {0}";
	private static final String DEF_TEXT_SV = "Gruppen postinformation innehåller teknisk "
			+ "information om posten, som t ex. Id, när posten skapades, vilken del av systemet "
			+ "posten tillhör mm. Den här recordInfo tillhör {0}";
	private static final String DEF_TEXT_EN = "The record info group contains technical "
			+ "information about the record, such as, id, when the record was created, which part "
			+ "of the system the record belongs to etc. This recordInfo belongs to {0}";
	private String apptokenUrl;
	private String baseUrl;
	private DataClientFactoryImp dataClientFactory;
	private DataClient dataClient;
	private int repeatId = 1000;

	private static final String REF = "ref";
	private static final String CHILD_REFERENCES = "childReferences";
	private static final String CHILD_REFERENCE = "childReference";
	private static final String RECORD_INFO = "recordInfo";
	private static final String DATA_DIVIDER = "dataDivider";

	private ClientDataToJsonConverterFactory dataToJsonConverterFactory;

	public SetFinalValueForValidationType(String apptokenUrl, String baseUrl) {
		this.apptokenUrl = apptokenUrl;
		this.baseUrl = baseUrl;
		dataClientFactory = DataClientFactoryImp.usingAppTokenVerifierUrlAndBaseUrl(apptokenUrl,
				baseUrl);
		dataClient = dataClientFactory.factorUsingUserIdAndAppToken("141414",
				"63e6bd34-02a1-4c82-8001-158c104cae0e");
		dataToJsonConverterFactory = ClientDataToJsonConverterProvider.createImplementingFactory();

	}

	// 1 read all validationtypes
	// 2 read both new and update group
	// 3 read recordInfos
	// 4 read validationType link
	// 5 create as new validationType link with final value
	// 6 create new recordInfos for new and update with new link
	// 7 update newGroup and updateGroup with new recordInfos.

	public void addFinalValueForValidationType() {
		// TODO Auto-generated method stub

		System.out.println("Start");

		ClientDataList listOfValidationTypes = dataClient.readList("validationType");
		for (ClientData validationTypeData : listOfValidationTypes.getDataList()) {
			ClientDataRecord validationTypeRecord = (ClientDataRecord) validationTypeData;
			ClientDataRecordGroup validationTypeRecordGroup = validationTypeRecord
					.getDataRecordGroup();
			// String id = validationTypeRecordGroup.getId();
			System.out.println(validationTypeRecordGroup.getId());
			System.out.println("-------------------------------------");

			readDefinitions(validationTypeRecordGroup);

			System.out.println();

		}

		System.out.println("Maybe finished :-)");

	}

	private void readDefinitions(ClientDataRecordGroup validationTypeRecordGroup) {

		try {
			ClientDataRecord orginalNewDefRecordInfo = getRecordInfo(validationTypeRecordGroup,
					"newMetadataId");
			ClientDataRecord originalUpdateDefRecordInfo = getRecordInfo(validationTypeRecordGroup,
					"metadataId");
			ClientDataRecord oldValidationTypeRecordLinkRecord = readValidationTypeLinkFromRecordInfo(
					originalUpdateDefRecordInfo.getDataRecordGroup());

			String newValidationTypeLinkId = copyAndCreateValidationTypeLink(
					validationTypeRecordGroup.getId(), oldValidationTypeRecordLinkRecord);

			String copyOfNewRecordInfoId = copyAndCreateRecordInfo(
					validationTypeRecordGroup.getId(), orginalNewDefRecordInfo,
					newValidationTypeLinkId, "New");
			String copyUpdateRecordInfoId = copyAndCreateRecordInfo(
					validationTypeRecordGroup.getId(), orginalNewDefRecordInfo,
					newValidationTypeLinkId, "Update");

			updateDefinitionReplacingRecordInfo(validationTypeRecordGroup,
					orginalNewDefRecordInfo.getId(), copyOfNewRecordInfoId, "newMetadataId");
			updateDefinitionReplacingRecordInfo(validationTypeRecordGroup,
					originalUpdateDefRecordInfo.getId(), copyUpdateRecordInfoId, "metadataId");

		} catch (Exception e) {
			System.err.println(e);
		}
	}

	private void updateDefinitionReplacingRecordInfo(
			ClientDataRecordGroup validationTypeRecordGroup, String originalRecordInfoId,
			String newRecordInfoId, String childNameInDataToReplace) {
		ClientDataRecordGroup originalDefinition = readRecordFromMetadaLink(
				validationTypeRecordGroup, childNameInDataToReplace);
		replaceChildReference(originalDefinition.getId(), originalDefinition, newRecordInfoId);

		dataClient.update(METADATA, originalRecordInfoId, originalDefinition);
		System.out.println("Group updated: " + originalRecordInfoId);
	}

	private String copyAndCreateRecordInfo(String validationTypeId,
			ClientDataRecord originalRecordInfo, String newValidationTypeId, String mode) {

		ClientDataRecordGroup newRecordInfo = copyAsNew(originalRecordInfo);
		removeObsoleteChildrenForRecordInfo(newRecordInfo);
		removeObsoleteChildrenFromDataRecordGroup(newRecordInfo, "text", "defText");
		replaceChildReference("validationTypeLink", newRecordInfo, newValidationTypeId);
		newRecordInfo
				.setId("recordInfoFor" + mode + firstLetterUpperCase(validationTypeId) + "Group");
		// createAndStoreText(newRecordInfo, "Text");
		// createAndStoreText(newRecordInfo, "DefText");

		// CONVERT TO JSON
		// ClientDataToJsonConverter factorUsingConvertible = dataToJsonConverterFactory
		// .factorUsingConvertible(newRecordInfo);
		// System.out.println(factorUsingConvertible.toJson());
		ClientDataRecord created = dataClient.create(METADATA, newRecordInfo);
		System.out.println("Created RecordInfo: " + created.getId());

		return created.getId();

	}

	private String copyAndCreateValidationTypeLink(String validationTypeId,
			ClientDataRecord oldValidationTypeRecordLinkRecord) {

		ClientDataRecordGroup newValidationTypeLinkRecordGroup = copyToNewValidationTypeLinkRecord(
				validationTypeId, oldValidationTypeRecordLinkRecord);
		try {

			ClientDataRecord created = dataClient.create(METADATA,
					newValidationTypeLinkRecordGroup);
			System.out.println("Create validationTypeLink: " + created.getId());
			return created.getId();
		} catch (Exception e) {
			System.err.println("Create validationTypeLink alreadyExists: "
					+ newValidationTypeLinkRecordGroup.getId());
			return newValidationTypeLinkRecordGroup.getId();
		}
	}

	private void replaceChildReference(String childNameToReplace, ClientDataRecordGroup dataGroup,
			String linkId) {
		ClientDataGroup childReferenceValidationType = createChildReferenceGroup(METADATA, linkId);

		List<ClientDataChild> listOfChildReferences = getListOfChildReferences(dataGroup);

		dataGroup.removeFirstChildWithNameInData("childReferences");

		Optional<ClientDataChild> oChildReference = Optional.empty();

		System.out.println("trying to find: " + childNameToReplace);
		for (ClientDataChild childReference : listOfChildReferences) {
			ClientDataRecordLink ref = ((ClientDataGroup) childReference)
					.getFirstChildOfTypeAndName(ClientDataRecordLink.class, "ref");

			if (childNameToReplace.equals(ref.getLinkedRecordId())) {
				oChildReference = Optional.of(childReference);
			}
		}
		if (oChildReference.isPresent()) {
			System.out.println("Found childrefernece: " + childNameToReplace);
			listOfChildReferences.remove(oChildReference.get());
			listOfChildReferences.add(childReferenceValidationType);
			System.out.println("Replace " + childNameToReplace + ": " + linkId);

			ClientDataGroup newChildReferences = ClientDataProvider
					.createGroupUsingNameInData("childReferences");
			newChildReferences.addChildren(listOfChildReferences);
			dataGroup.addChild(newChildReferences);

		} else {
			throw new RuntimeException("Failed to replace/find " + childNameToReplace);
		}
	}

	private List<ClientDataChild> getListOfChildReferences(ClientDataRecordGroup dataGroup) {
		ClientDataGroup childReferences = dataGroup
				.getFirstChildOfTypeAndName(ClientDataGroup.class, "childReferences");

		List<ClientDataChild> listOfChildReferences = childReferences
				.getAllChildrenWithNameInData("childReference");
		return new ArrayList<>(listOfChildReferences);
	}

	private ClientDataRecordGroup copyToNewValidationTypeLinkRecord(String validationTypeId,
			ClientDataRecord oldRecord) {
		ClientDataRecordGroup newValidationTypeLinkRecordGroup = copyAsNew(oldRecord);

		String validationTypeIdUpperCase = firstLetterUpperCase(validationTypeId);

		newValidationTypeLinkRecordGroup
				.setId("validationType" + validationTypeIdUpperCase + "Link");

		ClientDataAtomic finalValue = ClientDataProvider
				.createAtomicUsingNameInDataAndValue("finalValue", validationTypeId);

		removeObsoleteChildrenForRecordInfo(newValidationTypeLinkRecordGroup);

		newValidationTypeLinkRecordGroup.addChild(finalValue);
		return newValidationTypeLinkRecordGroup;
	}

	private String firstLetterUpperCase(String validationTypeId) {
		return validationTypeId.substring(0, 1).toUpperCase() + validationTypeId.substring(1);
	}

	private ClientDataRecordGroup copyAsNew(ClientDataRecord oldRecord) {
		ClientDataGroup oldRecorGroup = ClientDataProvider
				.createGroupFromRecordGroup(oldRecord.getDataRecordGroup());
		return ClientDataProvider.createRecordGroupFromDataGroup(oldRecorGroup);
	}

	private void removeObsoleteChildrenForRecordInfo(
			ClientDataRecordGroup newValidationTypeLinkRecordGroup) {
		ClientDataGroup firstChildWithNameInData = newValidationTypeLinkRecordGroup
				.getFirstGroupWithNameInData("recordInfo");

		removeObsoleteChildrenFromDataGroup(firstChildWithNameInData, "createdBy", "updated",
				"type", "tsCreated");
	}

	private void removeObsoleteChildrenFromDataGroup(ClientDataGroup dataGroup,
			String... listOfChildrenToRemove) {
		for (String childName : listOfChildrenToRemove) {
			dataGroup.removeAllChildrenWithNameInData(childName);
			System.out.println("removed from DataGroup: " + childName);
		}
	}

	private void removeObsoleteChildrenFromDataRecordGroup(ClientDataRecordGroup dataRecordGroup,
			String... listOfChildrenToRemove) {
		for (String childName : listOfChildrenToRemove) {
			dataRecordGroup.removeAllChildrenWithNameInData(childName);
			System.out.println("removed from DataRecordGroup: " + childName);
		}
	}

	private ClientDataRecord readValidationTypeLinkFromRecordInfo(
			ClientDataRecordGroup recordInfoGroup) throws Exception {

		ClientDataRecordLink link = findValidationTypeLink(recordInfoGroup);
		return dataClient.read(link.getLinkedRecordType(), link.getLinkedRecordId());
	}

	private ClientDataRecordLink findValidationTypeLink(ClientDataRecordGroup recordInfoGroup) {
		List<ClientDataChild> allChildReferences = getListOfChildReferences(recordInfoGroup);

		for (ClientDataChild childReference : allChildReferences) {
			ClientDataRecordLink ref = ((ClientDataGroup) childReference)
					.getFirstChildOfTypeAndName(ClientDataRecordLink.class, "ref");

			if ("validationTypeLink".equals(ref.getLinkedRecordId())) {
				return ref;
			}
		}
		throw new RuntimeException("failed to find validationTypeLink");
	}

	private ClientDataRecord getRecordInfo(ClientDataRecordGroup dataRecordGroup,
			String nameInData) {
		ClientDataRecordGroup definitionRecord = readRecordFromMetadaLink(dataRecordGroup,
				nameInData);
		List<ClientDataChild> children = getListChildReferences(definitionRecord);
		for (ClientDataChild clientDataChild : children) {
			ClientDataRecord mightBeRecordInfoGroup = readChildren(clientDataChild);
			if (mightBeRecordInfoGroup.getDataRecordGroup()
					.getFirstAtomicValueWithNameInData("nameInData").equals("recordInfo")) {
				return mightBeRecordInfoGroup;
			}
		}
		throw new RuntimeException("NO RECORDINFO FOUND FOR:" + nameInData);
	}

	private ClientDataRecordGroup readRecordFromMetadaLink(ClientDataRecordGroup dataRecordGroup,
			String nameInData) {
		ClientDataRecordLink linkNew = (ClientDataRecordLink) dataRecordGroup
				.getFirstChildWithNameInData(nameInData);
		ClientDataRecordGroup definitionRecord = readMetadata(linkNew);
		return definitionRecord;
	}

	private ClientDataRecordGroup readMetadata(ClientDataRecordLink linkToDataGroup) {
		ClientDataRecord metadataGroupRecord = dataClient
				.read(linkToDataGroup.getLinkedRecordType(), linkToDataGroup.getLinkedRecordId());
		return metadataGroupRecord.getDataRecordGroup();
	}

	private List<ClientDataChild> getListChildReferences(ClientDataRecordGroup metadataGroup) {
		ClientDataGroup childReferencesGroup = metadataGroup
				.getFirstGroupWithNameInData(CHILD_REFERENCES);
		return childReferencesGroup.getAllChildrenWithNameInData(CHILD_REFERENCE);
	}

	private ClientDataRecord readChildren(ClientDataChild clientDataChild) {
		ClientDataRecordLink ref = (ClientDataRecordLink) ((ClientDataGroup) clientDataChild)
				.getFirstChildWithNameInData(REF);
		return dataClient.read(ref.getLinkedRecordType(), ref.getLinkedRecordId());
	}

	private void createAndStoreText(ClientDataRecordGroup readRecordTypeRecordGroup, String mode) {
		String messageSv = TEXT_SV;
		String messageEn = TEXT_EN;
		if ("DefText".equals(mode)) {
			messageSv = DEF_TEXT_SV;
			messageEn = DEF_TEXT_EN;
		}

		ClientDataGroup readRecordInfo = (ClientDataGroup) readRecordTypeRecordGroup
				.getFirstChildWithNameInData(RECORD_INFO);

		ClientDataRecordGroup dataRecordGroupText = ClientDataProvider
				.createRecordGroupUsingNameInData("text");
		ClientDataGroup recordInfoText = ClientDataProvider.createGroupUsingNameInData(RECORD_INFO);
		recordInfoText.addChild(copyLink(readRecordInfo, DATA_DIVIDER));

		ClientDataAtomic idText = ClientDataProvider.createAtomicUsingNameInDataAndValue("id",
				readRecordTypeRecordGroup.getId() + mode);
		recordInfoText.addChild(idText);
		dataRecordGroupText.addChild(recordInfoText);
		// sv
		ClientDataGroup textPartSv = ClientDataProvider.createGroupUsingNameInData("textPart");
		dataRecordGroupText.addChild(textPartSv);
		textPartSv.addAttributeByIdWithValue("type", "default");
		textPartSv.addAttributeByIdWithValue("lang", "sv");
		ClientDataAtomic textSv = ClientDataProvider.createAtomicUsingNameInDataAndValue("text",
				MessageFormat.format(messageSv, readRecordTypeRecordGroup.getId()));
		textPartSv.addChild(textSv);
		// en
		ClientDataGroup textPartEn = ClientDataProvider.createGroupUsingNameInData("textPart");
		dataRecordGroupText.addChild(textPartEn);
		textPartEn.addAttributeByIdWithValue("type", "alternative");
		textPartEn.addAttributeByIdWithValue("lang", "en");
		ClientDataAtomic textEn = ClientDataProvider.createAtomicUsingNameInDataAndValue("text",
				MessageFormat.format(messageEn, readRecordTypeRecordGroup.getId()));
		textPartEn.addChild(textEn);

		ClientDataRecord created = dataClient.create("coraText", dataRecordGroupText);
		System.out.println("Created text: " + created.getId());
	}

	private ClientDataRecordLink copyLink(ClientDataGroup recordTypeRecordGroup,
			String nameInData) {
		ClientDataRecordLink link = getLinkIdByNameInData(recordTypeRecordGroup, nameInData);
		return createLink(nameInData, link.getLinkedRecordType(), link.getLinkedRecordId());
	}

	private ClientDataRecordLink createLink(String nameInData, String linkType, String linkId) {

		return ClientDataProvider.createRecordLinkUsingNameInDataAndTypeAndId(nameInData, linkType,
				linkId);

	}

	private ClientDataRecordLink getLinkIdByNameInData(ClientDataGroup recordTypeRecordGroup,
			String linkNameInData) {
		return (ClientDataRecordLink) recordTypeRecordGroup
				.getFirstChildWithNameInData(linkNameInData);
	}

	private ClientDataGroup createChildReferenceGroup(String linkType, String linkId) {
		ClientDataGroup referenceGroup = createChildReference();
		ClientDataAtomic repeatMin = ClientDataProvider
				.createAtomicUsingNameInDataAndValue("repeatMin", "1");
		ClientDataAtomic repeatMax = ClientDataProvider
				.createAtomicUsingNameInDataAndValue("repeatMax", "1");

		ClientDataRecordLink recordLink = ClientDataProvider
				.createRecordLinkUsingNameInDataAndTypeAndId("ref", linkType, linkId);
		referenceGroup.addChild(repeatMax);
		referenceGroup.addChild(repeatMin);
		referenceGroup.addChild(recordLink);
		return referenceGroup;
	}

	private ClientDataGroup createChildReference() {
		ClientDataGroup referenceGroup = ClientDataProvider
				.createGroupUsingNameInData(CHILD_REFERENCE);
		referenceGroup.setRepeatId(getRepeatId());
		return referenceGroup;
	}

	private String getRepeatId() {
		int currentRepeatId = repeatId;
		repeatId++;
		return String.valueOf(currentRepeatId);
	}

}
