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
import se.uu.ub.cora.clientdata.converter.ClientDataToJsonConverter;
import se.uu.ub.cora.clientdata.converter.ClientDataToJsonConverterFactory;
import se.uu.ub.cora.clientdata.converter.ClientDataToJsonConverterProvider;
import se.uu.ub.cora.javaclient.cora.DataClient;
import se.uu.ub.cora.javaclient.cora.DataClientFactoryImp;

public class SetFinalValueForValidationType {

	private static final String REF = "ref";
	private static final String CHILD_REFERENCES = "childReferences";
	private static final String CHILD_REFERENCE = "childReference";
	private static final String RECORD_INFO = "recordInfo";
	private static final String DATA_DIVIDER = "dataDivider";
	private static final String DEF_TEXT = "DefText";
	private static final String TEXT = "Text";
	private static final String METADATA = "metadata";
	private static final String TEXT_SV = "RecordInfo för {0}";
	private static final String TEXT_EN = "RecordInfo for {0}";
	private static final String DEF_TEXT_SV = "Gruppen postinformation innehåller teknisk "
			+ "information om posten, som t ex. Id, när posten skapades, vilken del av systemet "
			+ "posten tillhör mm. Den här recordInfo tillhör {0}";
	private static final String DEF_TEXT_EN = "The record info group contains technical "
			+ "information about the record, such as, id, when the record was created, which part "
			+ "of the system the record belongs to etc. This recordInfo belongs to {0}";
	private static final String RECORD_INFO_ID_PATTERN = "recordInfoFor{0}{1}Group";
	private static final String PRESENTATION_ID_PATTERN = "recordInfoFor{0}{1}PGroup";
	private static final String VAL_TYPE_LINK_ID_PATTERN = "validationType{0}Link";
	private static final String TEXT_ID_PATTERN = "{0}{1}";
	private DataClientFactoryImp dataClientFactory;
	private DataClient dataClient;
	private int repeatId = 1000;

	private ClientDataToJsonConverterFactory dataToJsonConverterFactory;

	public SetFinalValueForValidationType(String apptokenUrl, String baseUrl) {
		dataClientFactory = DataClientFactoryImp.usingAppTokenVerifierUrlAndBaseUrl(apptokenUrl,
				baseUrl);
		dataClient = dataClientFactory.factorUsingUserIdAndAppToken(
				"jsClientUser@system.cora.uu.se", "5d3f3ed4-4931-4924-9faa-8eaf5ac6457e");
		dataToJsonConverterFactory = ClientDataToJsonConverterProvider.createImplementingFactory();

	}

	// 1 read all validationtypes
	// 2 read both new and update group
	// 3 read recordInfos
	// 4 read validationType link
	// 5 create as new validationType link with final value
	// 6 create new recordInfos for new and update with new link
	// 7 update newGroup and updateGroup with new recordInfos.
	// 8 update with presentations

	public void addFinalValueForValidationType() {
		System.out.println("Start");

		ClientDataList listOfValidationTypes = dataClient.readList("validationType");
		loopValidationTypes(listOfValidationTypes);

		System.out.println("Maybe finished :-)");
	}

	private void loopValidationTypes(ClientDataList listOfValidationTypes) {
		for (ClientData validationTypeData : listOfValidationTypes.getDataList()) {
			ClientDataRecordGroup validationTypeRecordGroup = getDataRecordGroup(
					validationTypeData);

			printHeaderValidationType(validationTypeRecordGroup);

			createValidationTypeLinkAndUpdateRelatedMetadataAndPresentations(
					validationTypeRecordGroup);

			printExtraLine();

		}
	}

	private ClientDataRecordGroup getDataRecordGroup(ClientData validationTypeData) {
		ClientDataRecord validationTypeRecord = (ClientDataRecord) validationTypeData;
		ClientDataRecordGroup validationTypeRecordGroup = validationTypeRecord.getDataRecordGroup();
		return validationTypeRecordGroup;
	}

	private void printHeaderValidationType(ClientDataRecordGroup validationTypeRecordGroup) {
		System.out.println(validationTypeRecordGroup.getId());
		System.out.println("-------------------------------------");
	}

	private void createValidationTypeLinkAndUpdateRelatedMetadataAndPresentations(
			ClientDataRecordGroup validationTypeRecordGroup) {

		try {
			// ReadDefinitions for new and update
			ClientDataRecordGroup definitionForNewRecord = readRecordFromMetadaLink(
					validationTypeRecordGroup, "newMetadataId");
			ClientDataRecordGroup definitionForUpdateRecord = readRecordFromMetadaLink(
					validationTypeRecordGroup, "metadataId");

			// Read RecordInfos
			ClientDataRecord orginalNewDefRecordInfo = findChildReferenceUsingNameInData(
					definitionForNewRecord, "recordInfo");
			ClientDataRecord originalUpdateDefRecordInfo = findChildReferenceUsingNameInData(
					definitionForUpdateRecord, "recordInfo");
			String orginalNewDefRecordInfoId = orginalNewDefRecordInfo.getId();
			String originalUpdateDefRecordInfoId = originalUpdateDefRecordInfo.getId();

			// Create ValidationTypeLink
			String newValidationTypeLinkId = createValidationTypeLink(validationTypeRecordGroup,
					originalUpdateDefRecordInfo);

			updatedDefinitionWithNewValidationTypeLink("New", validationTypeRecordGroup.getId(),
					definitionForNewRecord, orginalNewDefRecordInfo, newValidationTypeLinkId);

			updatedDefinitionWithNewValidationTypeLink("Update", validationTypeRecordGroup.getId(),
					definitionForUpdateRecord, originalUpdateDefRecordInfo,
					newValidationTypeLinkId);

			// Presenentation
			System.out.println("readPresentations1");
			ClientDataRecordGroup presentNew = readRecordFromMetadaLink(validationTypeRecordGroup,
					"newPresentationFormId");
			System.out.println("readPresentations2");
			ClientDataRecordGroup presentUpdate = readRecordFromMetadaLink(
					validationTypeRecordGroup, "presentationFormId");

			System.out.println("findChildReferences1");
			ClientDataRecord originalPresentNew = findChildReferenceForPresentationUsingNameInData(
					presentNew, orginalNewDefRecordInfoId);
			System.out.println("findChildReferences2");
			ClientDataRecord originalPresentUpdate = findChildReferenceForPresentationUsingNameInData(
					presentUpdate, originalUpdateDefRecordInfoId);

			String recordinfoPNameForNew = MessageFormat.format(PRESENTATION_ID_PATTERN, "New",
					firstLetterUpperCase(validationTypeRecordGroup.getId()));
			String recordInfoPNameForUpdate = MessageFormat.format(PRESENTATION_ID_PATTERN,
					"Update", firstLetterUpperCase(validationTypeRecordGroup.getId()));

			System.out.println("replace1 ( definitionRecord:" + presentNew.getId()
					+ " childReferenceTobeReplaced: " + originalPresentNew.getId()
					+ " childReferenceToReplaceWith: " + recordinfoPNameForNew + " )");
			replaceChildReferenceAndUpdateInStorageForPresentation("presentation", presentNew,
					originalPresentNew.getId(), recordinfoPNameForNew);

			System.out.println("replace2");
			replaceChildReferenceAndUpdateInStorageForPresentation("presentation", presentUpdate,
					originalPresentUpdate.getId(), recordInfoPNameForUpdate);

		} catch (Exception e) {
			System.err.println(e);
		}
	}

	private void systemOutPrintlnBoldGreen(String string) {
		System.out.println("\033[0;1m\u001B[32m" + string + "\u001B[0m");
	}

	private void systemOutPrintlnBoldYellow(String string) {
		System.out.println("\033[0;1m\u001B[33m" + string + "\u001B[0m");
	}

	private void updatedDefinitionWithNewValidationTypeLink(String mode, String validationTypeId,
			ClientDataRecordGroup definitionRecord, ClientDataRecord orginalDefinitionRecordInfo,
			String newValidationTypeLinkId) {
		String orginalNewDefRecordInfoId = orginalDefinitionRecordInfo.getId();
		String copyOfNewRecordInfoId = copyAndCreateRecordInfo(orginalDefinitionRecordInfo,
				validationTypeId, newValidationTypeLinkId, mode);
		replaceChildReferenceAndUpdateInStorage(METADATA, definitionRecord,
				orginalNewDefRecordInfoId, copyOfNewRecordInfoId);
	}

	private String createValidationTypeLink(ClientDataRecordGroup validationTypeRecordGroup,
			ClientDataRecord originalUpdateDefRecordInfo) throws Exception {
		ClientDataRecord oldValidationTypeRecordLinkRecord = readValidationTypeLinkFromRecordInfo(
				originalUpdateDefRecordInfo.getDataRecordGroup());
		return copyAndCreateValidationTypeLink(validationTypeRecordGroup.getId(),
				oldValidationTypeRecordLinkRecord);
	}

	private ClientDataRecord findChildReferenceUsingNameInData(
			ClientDataRecordGroup definitionRecord, String nameInDataToMatch) {
		List<ClientDataChild> childReferences = getChildReferences(definitionRecord);
		for (ClientDataChild childReference : childReferences) {
			ClientDataRecord mightBeRecordInfoGroup = readRefLinkFromStorage(childReference);
			if (mightBeRecordInfoGroup.getDataRecordGroup()
					.getFirstAtomicValueWithNameInData("nameInData").equals(nameInDataToMatch)) {
				return mightBeRecordInfoGroup;
			}
		}
		throw new RuntimeException("NO RECORDINFO FOUND FOR:" + definitionRecord.getId());
	}

	private ClientDataRecord findChildReferenceForPresentationUsingNameInData(
			ClientDataRecordGroup definitionRecord, String nameInDataToMatch) {
		List<ClientDataChild> childReferences = getChildReferences(definitionRecord);
		for (ClientDataChild childReference : childReferences) {
			ClientDataChild refGroup = ((ClientDataGroup) childReference)
					.getFirstChildWithNameInData("refGroup");
			ClientDataRecordLink ref = (ClientDataRecordLink) ((ClientDataGroup) refGroup)
					.getFirstChildWithNameInData(REF);
			if (ref.getAttributeValue("type").get().equals("presentation")) {
				systemOutPrintlnBoldYellow("Ref linkId: " + ref.getLinkedRecordId());
				ClientDataRecord mightBeRecordInfoGroup = dataClient.read(ref.getLinkedRecordType(),
						ref.getLinkedRecordId());
				ClientDataRecordGroup recordInfoGroup = mightBeRecordInfoGroup.getDataRecordGroup();
				ClientDataRecordLink presentationOfLink = recordInfoGroup
						.getFirstChildOfTypeAndName(ClientDataRecordLink.class, "presentationOf");

				ClientDataRecord presentationOfLinkDR = dataClient.read(
						presentationOfLink.getLinkedRecordType(),
						presentationOfLink.getLinkedRecordId());
				ClientDataRecordGroup presentationOfLinkDRG = presentationOfLinkDR
						.getDataRecordGroup();
				if (presentationOfLinkDRG.getFirstAtomicValueWithNameInData("nameInData")
						.equals("recordInfo")) {
					return mightBeRecordInfoGroup;
				}
			}
		}
		throw new RuntimeException("NO RECORDINFO FOUND FOR:" + definitionRecord.getId());
	}

	private ClientDataRecordGroup readRecordFromMetadaLink(ClientDataRecordGroup dataRecordGroup,
			String nameInData) {
		ClientDataRecordLink linkNew = (ClientDataRecordLink) dataRecordGroup
				.getFirstChildWithNameInData(nameInData);
		return readMetadata(linkNew);
	}

	private ClientDataRecordGroup readMetadata(ClientDataRecordLink linkToDataGroup) {
		ClientDataRecord metadataGroupRecord = dataClient
				.read(linkToDataGroup.getLinkedRecordType(), linkToDataGroup.getLinkedRecordId());
		return metadataGroupRecord.getDataRecordGroup();
	}

	private List<ClientDataChild> getChildReferences(ClientDataRecordGroup metadataGroup) {
		ClientDataGroup childReferencesGroup = metadataGroup
				.getFirstGroupWithNameInData(CHILD_REFERENCES);
		return childReferencesGroup.getAllChildrenWithNameInData(CHILD_REFERENCE);
	}

	private ClientDataRecord readRefLinkFromStorage(ClientDataChild clientDataChild) {
		ClientDataRecordLink ref = (ClientDataRecordLink) ((ClientDataGroup) clientDataChild)
				.getFirstChildWithNameInData(REF);
		return dataClient.read(ref.getLinkedRecordType(), ref.getLinkedRecordId());
	}

	private void printExtraLine() {
		System.out.println();
	}

	private void replaceChildReferenceAndUpdateInStorageForPresentation(String type,
			ClientDataRecordGroup definitionRecord, String childReferenceTobeReplaced,
			String childReferenceToReplaceWith) {

		replaceChildReferenceForPresentation(type, definitionRecord, childReferenceTobeReplaced,
				childReferenceToReplaceWith);

		dataClient.update(type, definitionRecord.getId(), definitionRecord);
		// System.out.println("PGroup updated: " + definitionRecord.getId());
		systemOutPrintlnBoldGreen("PGroup updated: " + definitionRecord.getId());
	}

	private void replaceChildReferenceAndUpdateInStorage(String type,
			ClientDataRecordGroup definitionRecord, String childReferenceTobeReplaced,
			String childReferenceToReplaceWith) {

		replaceChildReference(type, definitionRecord, childReferenceTobeReplaced,
				childReferenceToReplaceWith);

		dataClient.update(type, definitionRecord.getId(), definitionRecord);
		// System.out.println("Group updated: " + definitionRecord.getId());
		systemOutPrintlnBoldGreen("Group updated: " + definitionRecord.getId());
	}

	private String copyAndCreateRecordInfo(ClientDataRecord originalRecordInfo,
			String validationTypeId, String newValidationTypeLinkId, String mode) {

		ClientDataRecordGroup newRecordInfo = copyAsNew(originalRecordInfo);
		removeObsoleteChildrenForRecordInfo(newRecordInfo);
		removeObsoleteChildrenFromDataRecordGroup(newRecordInfo, "textId", "defTextId");
		replaceChildReference(METADATA, newRecordInfo, "validationTypeLink",
				newValidationTypeLinkId);
		String recordInfoId = MessageFormat.format(RECORD_INFO_ID_PATTERN, mode,
				firstLetterUpperCase(validationTypeId));
		newRecordInfo.setId(recordInfoId);

		createAndStoreText(newRecordInfo, TEXT);
		createAndStoreText(newRecordInfo, DEF_TEXT);

		newRecordInfo.addChild(
				ClientDataProvider.createRecordLinkUsingNameInDataAndTypeAndId("textId", "text",
						MessageFormat.format(TEXT_ID_PATTERN, newRecordInfo.getId(), TEXT)));
		newRecordInfo.addChild(
				ClientDataProvider.createRecordLinkUsingNameInDataAndTypeAndId("defTextId", "text",
						MessageFormat.format(TEXT_ID_PATTERN, newRecordInfo.getId(), DEF_TEXT)));

		// convertToJson(newRecordInfo);

		try {
			ClientDataRecord created = dataClient.create(METADATA, newRecordInfo);
			System.out.println("Created RecordInfo: " + created.getId());
			return created.getId();
		} catch (Exception e) {
			System.err.println("Created RecordInfo already exists: " + newRecordInfo.getId());
			System.err.println(e.getMessage());
			return recordInfoId;
		}

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

	private void replaceChildReference(String type, ClientDataRecordGroup definitionGroup,
			String childToBeReplaced, String childToBeReplacedWith) {

		ClientDataGroup childReferenceValidationType = createChildReferenceGroup(type,
				childToBeReplacedWith);

		List<ClientDataChild> listOfChildReferences = getListOfChildReferences(definitionGroup);

		definitionGroup.removeFirstChildWithNameInData(CHILD_REFERENCES);

		Optional<ClientDataChild> oChildReference = Optional.empty();

		// System.out.println("trying to find: " + childToBeReplaced);
		for (ClientDataChild childReference : listOfChildReferences) {
			ClientDataRecordLink ref = ((ClientDataGroup) childReference)
					.getFirstChildOfTypeAndName(ClientDataRecordLink.class, "ref");

			if (childToBeReplaced.equals(ref.getLinkedRecordId())) {
				oChildReference = Optional.of(childReference);
			}
		}
		if (oChildReference.isPresent()) {
			listOfChildReferences.remove(oChildReference.get());
			listOfChildReferences.add(childReferenceValidationType);

			ClientDataGroup newChildReferences = ClientDataProvider
					.createGroupUsingNameInData(CHILD_REFERENCES);
			newChildReferences.addChildren(listOfChildReferences);
			definitionGroup.addChild(newChildReferences);
			System.out.println(
					"Child replaced from " + childToBeReplaced + " to " + childToBeReplacedWith);

		} else {

			throw new RuntimeException("Failed to replace/find " + childToBeReplaced);
		}
	}

	private void replaceChildReferenceForPresentation(String type,
			ClientDataRecordGroup definitionGroup, String childToBeReplaced,
			String childToBeReplacedWith) {
		// TODO: wrong childreference type, should be other for presentations....
		// ClientDataGroup childReferenceValidationType = createChildReferenceGroup(type,
		// childToBeReplacedWith);
		ClientDataGroup childReferenceValidationType = createChildReferenceGroupForPresentation(
				type, childToBeReplacedWith);

		List<ClientDataChild> listOfChildReferences = getListOfChildReferences(definitionGroup);

		definitionGroup.removeFirstChildWithNameInData(CHILD_REFERENCES);

		Optional<ClientDataChild> oChildReference = Optional.empty();

		// System.out.println("trying to find: " + childToBeReplaced);
		for (ClientDataChild childReference : listOfChildReferences) {
			ClientDataChild refGroup = ((ClientDataGroup) childReference)
					.getFirstChildWithNameInData("refGroup");
			ClientDataRecordLink ref = ((ClientDataGroup) refGroup)
					.getFirstChildOfTypeAndName(ClientDataRecordLink.class, "ref");

			if (childToBeReplaced.equals(ref.getLinkedRecordId())) {
				oChildReference = Optional.of(childReference);
			}
		}
		if (oChildReference.isPresent()) {
			listOfChildReferences.remove(oChildReference.get());
			listOfChildReferences.add(childReferenceValidationType);

			ClientDataGroup newChildReferences = ClientDataProvider
					.createGroupUsingNameInData(CHILD_REFERENCES);
			newChildReferences.addChildren(listOfChildReferences);
			definitionGroup.addChild(newChildReferences);
			System.out.println(
					"Child replaced from " + childToBeReplaced + " to " + childToBeReplacedWith);

		} else {

			throw new RuntimeException("Failed to replace/find " + childToBeReplaced);
		}
	}

	private ClientDataGroup createChildReferenceGroupForPresentation(String linkType,
			String linkId) {
		ClientDataGroup referenceGroup = createChildReferenceGroup();
		ClientDataGroup refGroup = ClientDataProvider.createGroupUsingNameInData("refGroup");
		referenceGroup.addChild(refGroup);
		refGroup.setRepeatId("0");

		ClientDataRecordLink ref = ClientDataProvider
				.createRecordLinkUsingNameInDataAndTypeAndId("ref", linkType, linkId);
		ref.addAttributeByIdWithValue("type", "presentation");
		refGroup.addChild(ref);

		return referenceGroup;
	}

	private void convertToJson(ClientDataRecordGroup definitionGroup) {
		ClientDataToJsonConverter factorUsingConvertible = dataToJsonConverterFactory
				.factorUsingConvertible(definitionGroup);
		System.out.println(factorUsingConvertible.toJson());
	}

	private List<ClientDataChild> getListOfChildReferences(ClientDataRecordGroup dataGroup) {
		ClientDataGroup childReferences = dataGroup
				.getFirstChildOfTypeAndName(ClientDataGroup.class, CHILD_REFERENCES);

		List<ClientDataChild> listOfChildReferences = childReferences
				.getAllChildrenWithNameInData(CHILD_REFERENCE);
		return new ArrayList<>(listOfChildReferences);
	}

	private ClientDataRecordGroup copyToNewValidationTypeLinkRecord(String validationTypeId,
			ClientDataRecord oldRecord) {
		ClientDataRecordGroup newValidationTypeLinkRecordGroup = copyAsNew(oldRecord);

		String validationTypeIdUpperCase = firstLetterUpperCase(validationTypeId);

		newValidationTypeLinkRecordGroup
				.setId(MessageFormat.format(VAL_TYPE_LINK_ID_PATTERN, validationTypeIdUpperCase));

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
		}
	}

	private void removeObsoleteChildrenFromDataRecordGroup(ClientDataRecordGroup dataRecordGroup,
			String... listOfChildrenToRemove) {
		for (String childName : listOfChildrenToRemove) {
			dataRecordGroup.removeFirstChildWithNameInData(childName);
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

	private void createAndStoreText(ClientDataRecordGroup readRecordTypeRecordGroup,
			String textMode) {
		String messageSv = TEXT_SV;
		String messageEn = TEXT_EN;
		if (DEF_TEXT.equals(textMode)) {
			messageSv = DEF_TEXT_SV;
			messageEn = DEF_TEXT_EN;
		}

		ClientDataRecordGroup dataRecordGroupText = ClientDataProvider
				.createRecordGroupUsingNameInData("text");

		dataRecordGroupText.setId(
				MessageFormat.format(TEXT_ID_PATTERN, readRecordTypeRecordGroup.getId(), textMode));
		dataRecordGroupText.setDataDivider(readRecordTypeRecordGroup.getDataDivider());

		setValidationTypeIntoRecordInfo(dataRecordGroupText);

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

		// convertToJson(dataRecordGroupText);
		try {
			ClientDataRecord created = dataClient.create("text", dataRecordGroupText);
			System.out.println("Created text: " + created.getId());
		} catch (Exception e) {
			System.out.println("Created text: " + dataRecordGroupText.getId());
		}
	}

	private void setValidationTypeIntoRecordInfo(ClientDataRecordGroup dataRecordGroupText) {
		ClientDataGroup recordInfo = dataRecordGroupText
				.getFirstChildOfTypeAndName(ClientDataGroup.class, RECORD_INFO);
		recordInfo.addChild(ClientDataProvider.createRecordLinkUsingNameInDataAndTypeAndId(
				"validationType", "validationType", "coraText"));
	}

	private ClientDataGroup createChildReferenceGroup(String linkType, String linkId) {
		ClientDataGroup referenceGroup = createChildReferenceGroup();
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

	private ClientDataGroup createChildReferenceGroup() {
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
