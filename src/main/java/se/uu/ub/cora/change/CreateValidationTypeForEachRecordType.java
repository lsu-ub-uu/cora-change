package se.uu.ub.cora.change;

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.javaclient.cora.DataClient;
import se.uu.ub.cora.javaclient.cora.DataClientFactoryImp;

public class CreateValidationTypeForEachRecordType {

	private DataClientFactoryImp dataClientFactory;
	private DataClient dataClient;

	public CreateValidationTypeForEachRecordType(String apptokenUrl, String baseUrl) {
		dataClientFactory = DataClientFactoryImp.usingAppTokenVerifierUrlAndBaseUrl(apptokenUrl,
				baseUrl);
		dataClient = dataClientFactory.factorUsingUserIdAndAppToken("141414",
				"63e6bd34-02a1-4c82-8001-158c104cae0e");

	}

	public void createValidationTypes() {
		ClientDataList listOfRecordTypes = dataClient.readList("recordType");

		for (ClientData recordtypeData : listOfRecordTypes.getDataList()) {
			ClientDataRecord recordTypeRecord = (ClientDataRecord) recordtypeData;

			ClientDataRecordGroup recordTypeRecordGroup = recordTypeRecord.getDataRecordGroup();
			if (isImplementingType(recordTypeRecordGroup)) {
				System.out.println("Convert " + recordTypeRecordGroup.getId());

				ClientDataRecordGroup validationType = createValidationTypeRecordGroup(
						recordTypeRecordGroup);

				// ClientDataToJsonConverterFactory converterFactory =
				// ClientDataToJsonConverterProvider
				// .createImplementingFactory();

				// ClientDataToJsonConverter converter = converterFactory
				// .factorUsingConvertible(validationType);
				//
				// System.out.println(converter.toJson());

				dataClient.create("validationType", validationType);
			} else {
				System.out.println("Skip " + recordTypeRecordGroup.getId());
			}
		}
	}

	private ClientDataRecordGroup createValidationTypeRecordGroup(
			ClientDataRecordGroup readRecordTypeRecordGroup) {
		ClientDataRecordGroup dataRecordGroup = ClientDataProvider
				.createRecordGroupUsingNameInData("validationType");

		// recordInfo
		// textIdLink
		// defTextIdLink
		// validatesRecordTypeLink
		// newMetadataIdLink
		// metadataIdLink
		// presentationViewIdLink //
		// menuPresentationViewIdLink
		// newPresentationFormIdLink
		// presentationFormIdLink
		// listPresentationViewIdLink
		// autocompletePresentationViewLink
		// recordTypeFilterPresentationLink

		ClientDataGroup readRecordInfo = (ClientDataGroup) readRecordTypeRecordGroup
				.getFirstChildWithNameInData("recordInfo");
		ClientDataGroup recordInfo = ClientDataProvider.createGroupUsingNameInData("recordInfo");
		ClientDataAtomic id = ClientDataProvider.createAtomicUsingNameInDataAndValue("id",
				readRecordTypeRecordGroup.getId());
		recordInfo.addChild(id);
		recordInfo.addChild(copyLink(readRecordInfo, "dataDivider"));
		recordInfo.addChild(createLink("validationType", "validationType",
				getValidatesRecordType(readRecordTypeRecordGroup)));

		dataRecordGroup.addChild(recordInfo);
		dataRecordGroup.addChild(copyLink((ClientDataGroup) readRecordTypeRecordGroup, "textId"));
		dataRecordGroup
				.addChild(copyLink((ClientDataGroup) readRecordTypeRecordGroup, "defTextId"));
		dataRecordGroup.addChild(createLink("validatesRecordType", "recordType",
				getValidatesRecordType(readRecordTypeRecordGroup)));
		dataRecordGroup
				.addChild(copyLink((ClientDataGroup) readRecordTypeRecordGroup, "newMetadataId"));
		dataRecordGroup
				.addChild(copyLink((ClientDataGroup) readRecordTypeRecordGroup, "metadataId"));
		dataRecordGroup.addChild(
				copyLink((ClientDataGroup) readRecordTypeRecordGroup, "presentationViewId"));
		dataRecordGroup.addChild(
				copyLink((ClientDataGroup) readRecordTypeRecordGroup, "menuPresentationViewId"));
		dataRecordGroup.addChild(
				copyLink((ClientDataGroup) readRecordTypeRecordGroup, "newPresentationFormId"));
		dataRecordGroup.addChild(
				copyLink((ClientDataGroup) readRecordTypeRecordGroup, "presentationFormId"));
		dataRecordGroup.addChild(
				copyLink((ClientDataGroup) readRecordTypeRecordGroup, "listPresentationViewId"));
		// dataRecordGroup.addChild(copyLink(recordTypeRecordGroup,
		// "autocompletePresentationView"));
		// dataRecordGroup.addChild(copyLink(recordTypeRecordGroup,
		// "recordTypeFilterPresentation"));
		return dataRecordGroup;
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

	private boolean isImplementingType(ClientDataRecordGroup recordTypeRecordGroup) {
		return recordTypeRecordGroup.containsChildWithNameInData("abstract")
				&& recordTypeRecordGroup.getFirstDataAtomicWithNameInData("abstract").getValue()
						.equals("false");
	}

	private String getValidatesRecordType(ClientDataRecordGroup recordTypeRecordGroup) {
		String validateRecordType;
		if (recordTypeRecordGroup.containsChildWithNameInData("parentId")) {
			ClientDataRecordLink parentLink = (ClientDataRecordLink) recordTypeRecordGroup
					.getFirstChildWithNameInData("parentId");
			validateRecordType = parentLink.getLinkedRecordId();

		} else {
			validateRecordType = recordTypeRecordGroup.getId();
		}
		return validateRecordType;
	}

	private ClientDataRecordLink getLinkIdByNameInData(ClientDataGroup recordTypeRecordGroup,
			String linkNameInData) {
		return (ClientDataRecordLink) recordTypeRecordGroup
				.getFirstChildWithNameInData(linkNameInData);
	}

}
