package se.uu.ub.cora.change;

import java.util.List;

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

public class UpdateAllRecordsAndLinkValidationType {

	private static final String DEF_TEXT_ID = "defTextId";
	private static final String TEXT_ID = "textId";
	private static final String DEF_TEXT = "DefText";
	private static final String TEXT = "Text";
	private static final String ID = "id";
	private static final String DATA_DIVIDER = "dataDivider";
	private static final String RECORD_INFO = "recordInfo";
	private DataClientFactoryImp dataClientFactory;
	private DataClient dataClient;
	private int count = 0;

	public UpdateAllRecordsAndLinkValidationType(String apptokenUrl, String baseUrl, String user,
			String appToken) {
		dataClientFactory = DataClientFactoryImp.usingAppTokenVerifierUrlAndBaseUrl(apptokenUrl,
				baseUrl);
		dataClient = dataClientFactory.factorUsingUserIdAndAppToken(user, appToken);
	}

	public void updateOnlyRecordsForTheseTypes(List<String> runOnlyThisListOfRecordTypes) {
		ClientDataList listOfRecordTypes = dataClient.readList("recordType");

		for (ClientData recordTypeData : listOfRecordTypes.getDataList()) {
			ClientDataRecord recordTypeDataRecord = (ClientDataRecord) recordTypeData;
			if (runOnlyThisListOfRecordTypes.isEmpty()
					|| runOnlyThisListOfRecordTypes.contains(recordTypeDataRecord.getId())) {
				System.out.println("Listing type: " + recordTypeDataRecord.getId()
						+ " partial counting: " + count);
				if (allowedRecordType(recordTypeDataRecord)) {
					updateAllRecordsForRecordType(recordTypeDataRecord);
				}
				System.out.println();
			}
		}
		System.out.println("Updated records: " + count);
	}

	public void updateAllRecordsExceptTheseTypes(List<String> runOthersThisListOfRecordTypes) {
		ClientDataList listOfRecordTypes = dataClient.readList("recordType");

		for (ClientData recordTypeData : listOfRecordTypes.getDataList()) {
			ClientDataRecord recordTypeDataRecord = (ClientDataRecord) recordTypeData;
			if (runOthersThisListOfRecordTypes.isEmpty()
					|| !runOthersThisListOfRecordTypes.contains(recordTypeDataRecord.getId())) {
				System.out.println("Listing type: " + recordTypeDataRecord.getId()
						+ " partial counting: " + count);
				if (allowedRecordType(recordTypeDataRecord)) {
					updateAllRecordsForRecordType(recordTypeDataRecord);
				}
				System.out.println();
			}
		}
		System.out.println("Updated records " + count);
	}

	public void updateAllRecords(List<String> runOnlyThislistOfRecordTypes) {
		ClientDataList listOfRecordTypes = dataClient.readList("recordType");

		for (ClientData recordTypeData : listOfRecordTypes.getDataList()) {
			ClientDataRecord recordTypeDataRecord = (ClientDataRecord) recordTypeData;
			System.out.println("Listing type: " + recordTypeDataRecord.getId()
					+ " partial counting: " + count);
			if (allowedRecordType(recordTypeDataRecord)) {
				updateAllRecordsForRecordType(recordTypeDataRecord);
			}
			System.out.println();
		}
		System.out.println("Updated records " + count);
	}

	private void updateAllRecordsForRecordType(ClientDataRecord recordTypeDataRecord) {
		ClientDataList listRecordsForType = dataClient.readList(recordTypeDataRecord.getId());
		for (ClientData recordData : listRecordsForType.getDataList()) {
			updateRecord(recordData);
			count++;
		}
	}

	private void updateRecord(ClientData recordData) {
		ClientDataRecord recordDataRecord = (ClientDataRecord) recordData;
		String recordTypeId = recordDataRecord.getType();
		ClientDataRecordGroup dataRecordGroup = recordDataRecord.getDataRecordGroup();
		ClientDataGroup recordInfo = dataRecordGroup.getFirstGroupWithNameInData("recordInfo");

		if (!recordInfo.containsChildWithNameInData("validationType")) {

			recordInfo.addChild(ClientDataProvider.createRecordLinkUsingNameInDataAndTypeAndId(
					"validationType", "validationType", recordTypeId));
			System.out.println("Updating record: " + recordDataRecord.getType() + ":"
					+ recordDataRecord.getId());

			try {

				dataClient.update(dataRecordGroup.getType(), dataRecordGroup.getId(),
						dataRecordGroup);
			} catch (Exception e) {
				System.out.println("Error: " + recordDataRecord.getType() + ":"
						+ recordDataRecord.getId() + ":" + e.getMessage());
			}
		} else {
			System.out.println("Skipping record: " + recordDataRecord.getType() + ":"
					+ recordDataRecord.getId());
		}
	}

	private boolean allowedRecordType(ClientDataRecord dataRecord) {
		return forbiddenRecordTypes(dataRecord)
				&& isImplementingType(dataRecord.getDataRecordGroup());
	}

	private boolean isImplementingType(ClientDataRecordGroup recordTypeRecordGroup) {
		return recordTypeRecordGroup.containsChildWithNameInData("abstract")
				&& recordTypeRecordGroup.getFirstDataAtomicWithNameInData("abstract").getValue()
						.equals("false");
	}

	private boolean forbiddenRecordTypes(ClientDataRecord dataRecord) {
		return !dataRecord.getId().equals("systemSecret") && !dataRecord.getId().equals("appToken");
	}

	public void addMissingTextsAndDefTexts() {
		List<String> recordTypesToLoop = List.of("permissionRole", "permissionRule");

		for (String recordType : recordTypesToLoop) {
			ClientDataList readList = dataClient.readList(recordType);

			for (ClientData recordTypeData : readList.getDataList()) {
				ClientDataRecord recordDataRecord = (ClientDataRecord) recordTypeData;
				ClientDataRecordGroup dataRecordGroup = recordDataRecord.getDataRecordGroup();

				System.out.println("Reading record: " + dataRecordGroup.getType() + ":"
						+ dataRecordGroup.getId());

				updateText(recordType, dataRecordGroup, TEXT_ID, TEXT);
				updateText(recordType, dataRecordGroup, DEF_TEXT_ID, DEF_TEXT);
				try {
					dataClient.update(recordType, dataRecordGroup.getId(), dataRecordGroup);
				} catch (Exception e) {
					System.out.println("Error on record: " + dataRecordGroup.getType() + ":"
							+ dataRecordGroup.getId());
				}

			}
		}
	}

	private void updateText(String recordType, ClientDataRecordGroup dataRecordGroup,
			String nameInData, String typeOfText) {
		if (textMissing(nameInData, dataRecordGroup)) {
			String textId = createAndStoreDefTextForValidationType(recordType, dataRecordGroup,
					typeOfText);
			dataRecordGroup.removeFirstChildWithNameInData(nameInData);
			ClientDataRecordLink link = ClientDataProvider
					.createRecordLinkUsingNameInDataAndTypeAndId(nameInData, "coraText", textId);
			dataRecordGroup.addChild(link);
			System.out.println("Update " + nameInData + " record: " + dataRecordGroup.getType()
					+ ":" + dataRecordGroup.getId());
		}
	}

	private boolean textMissing(String nameInData, ClientDataRecordGroup dataRecordGroup) {
		return !dataRecordGroup.containsChildWithNameInData(nameInData);
	}

	private String createAndStoreDefTextForValidationType(String recordType,
			ClientDataRecordGroup readRecordTypeRecordGroup, String typeOfText) {
		ClientDataGroup readRecordInfo = (ClientDataGroup) readRecordTypeRecordGroup
				.getFirstChildWithNameInData(RECORD_INFO);
		ClientDataRecordGroup dataRecordGroupText = ClientDataProvider
				.createRecordGroupUsingNameInData("text");
		ClientDataGroup recordInfoText = ClientDataProvider.createGroupUsingNameInData(RECORD_INFO);
		recordInfoText.addChild(copyLink(readRecordInfo, DATA_DIVIDER));

		ClientDataAtomic idText = ClientDataProvider.createAtomicUsingNameInDataAndValue(ID,
				readRecordTypeRecordGroup.getId() + capitalize(recordType) + typeOfText);
		recordInfoText.addChild(idText);
		dataRecordGroupText.addChild(recordInfoText);
		// sv
		ClientDataGroup textPartSv = ClientDataProvider.createGroupUsingNameInData("textPart");
		dataRecordGroupText.addChild(textPartSv);
		textPartSv.addAttributeByIdWithValue("type", "default");
		textPartSv.addAttributeByIdWithValue("lang", "sv");
		ClientDataAtomic defTextSv = ClientDataProvider.createAtomicUsingNameInDataAndValue("text",
				"Text för " + readRecordTypeRecordGroup.getId());
		textPartSv.addChild(defTextSv);
		// en
		ClientDataGroup textPartEn = ClientDataProvider.createGroupUsingNameInData("textPart");
		dataRecordGroupText.addChild(textPartEn);
		textPartEn.addAttributeByIdWithValue("type", "alternative");
		textPartEn.addAttributeByIdWithValue("lang", "en");
		ClientDataAtomic defTextEn = ClientDataProvider.createAtomicUsingNameInDataAndValue("text",
				"Text for " + readRecordTypeRecordGroup.getId());
		textPartEn.addChild(defTextEn);

		ClientDataRecord create = dataClient.create("coraText", dataRecordGroupText);
		return create.getId();
	}

	private String capitalize(String recordType) {
		return recordType.substring(0, 1).toUpperCase() + recordType.substring(1);
	}

	private ClientDataRecordLink getLinkIdByNameInData(ClientDataGroup recordTypeRecordGroup,
			String linkNameInData) {
		return (ClientDataRecordLink) recordTypeRecordGroup
				.getFirstChildWithNameInData(linkNameInData);
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

}
