package se.uu.ub.cora.change;

import java.util.List;

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataChild;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.javaclient.cora.DataClient;
import se.uu.ub.cora.javaclient.cora.DataClientFactoryImp;

public class SetFinalValueForValidationType {

	private String apptokenUrl;
	private String baseUrl;
	private DataClientFactoryImp dataClientFactory;
	private DataClient dataClient;

	private static final String REF_PARENT_ID = "refParentId";
	private static final String REF = "ref";
	private static final String CHILD_REFERENCES = "childReferences";
	private static final String CHILD_REFERENCE = "childReference";

	public SetFinalValueForValidationType(String apptokenUrl, String baseUrl) {
		this.apptokenUrl = apptokenUrl;
		this.baseUrl = baseUrl;
		dataClientFactory = DataClientFactoryImp.usingAppTokenVerifierUrlAndBaseUrl(apptokenUrl,
				baseUrl);
		dataClient = dataClientFactory.factorUsingUserIdAndAppToken("141414",
				"63e6bd34-02a1-4c82-8001-158c104cae0e");

	}

	// 1 read all validationtypes
	// 2 read both new and update group
	// 3 read recordInfos
	// 4 read validationType link
	// 5 create as new validationType link with final value
	// 6 create new recordTypes for new and update with new link
	// 7 update newGroup and updateGroup with new recordInfos.

	public void addFinalValueForValidationType() {
		// TODO Auto-generated method stub

		ClientDataList listOfValidationTypes = dataClient.readList("validationType");
		for (ClientData validationTypeData : listOfValidationTypes.getDataList()) {
			ClientDataRecord validationTypeRecord = (ClientDataRecord) validationTypeData;
			ClientDataRecordGroup validationTypeRecordGroup = validationTypeRecord
					.getDataRecordGroup();
			// String id = validationTypeRecordGroup.getId();

			readDefinitions(validationTypeRecordGroup);

		}

	}

	private void readDefinitions(ClientDataRecordGroup validationTypeRecordGroup) {

		try {
			ClientDataRecordGroup newDefRecordInfo = getRecordInfo(validationTypeRecordGroup,
					"newMetadataId");
			ClientDataRecordGroup updateDefRecordInfo = getRecordInfo(validationTypeRecordGroup,
					"metadataId");
			ClientDataRecord oldValidationTypeRecordLinkRecord = readValidationTypeLinkFromRecordInfo(
					updateDefRecordInfo);

			createNewValidationTypeLink(validationTypeRecordGroup,
					oldValidationTypeRecordLinkRecord);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void createNewValidationTypeLink(ClientDataRecordGroup validationTypeRecordGroup,
			ClientDataRecord oldValidationTypeRecordLinkRecord) {
		ClientDataGroup oldValidationTypeLinkGroup = ClientDataProvider
				.createGroupFromRecordGroup(oldValidationTypeRecordLinkRecord.getDataRecordGroup());

		ClientDataRecordGroup newValidationTypeLinkRecordGroup = ClientDataProvider
				.createRecordGroupFromDataGroup(oldValidationTypeLinkGroup);
		newValidationTypeLinkRecordGroup
				.setId("ValidationType" + validationTypeRecordGroup.getId() + "Link");
		ClientDataAtomic finalValue = ClientDataProvider.createAtomicUsingNameInDataAndValue(
				"finalValue", validationTypeRecordGroup.getId());
		newValidationTypeLinkRecordGroup.addChild(finalValue);

		dataClient.create("metadata", newValidationTypeLinkRecordGroup);
	}

	private ClientDataRecord readValidationTypeLinkFromRecordInfo(
			ClientDataRecordGroup recordInfoGroup) throws Exception {
		ClientDataGroup childReferences = recordInfoGroup
				.getFirstChildOfTypeAndName(ClientDataGroup.class, "childReferences");

		List<ClientDataChild> allChildReferences = childReferences
				.getAllChildrenWithNameInData("childReference");

		for (ClientDataChild childReference : allChildReferences) {
			ClientDataRecordLink ref = ((ClientDataGroup) childReference)
					.getFirstChildOfTypeAndName(ClientDataRecordLink.class, "ref");

			if ("validationTypeLink".equals(ref.getLinkedRecordId())) {
				return dataClient.read(ref.getLinkedRecordType(), ref.getLinkedRecordId());
			}
		}
		throw new RuntimeException("failed to find validationTypeLink");
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

}
