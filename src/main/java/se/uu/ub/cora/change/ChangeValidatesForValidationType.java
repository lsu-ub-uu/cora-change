package se.uu.ub.cora.change;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Optional;

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.javaclient.cora.DataClient;
import se.uu.ub.cora.javaclient.cora.DataClientFactoryImp;

public class ChangeValidatesForValidationType {

	private static final String VALIDATION_DEF_TEXT = "ValidationDefText";
	private static final String ID = "id";
	private static final String DATA_DIVIDER = "dataDivider";
	private static final String RECORD_INFO = "recordInfo";
	private DataClientFactoryImp dataClientFactory;
	private DataClient dataClient;
	private String apptokenUrl;
	private String baseUrl;

	public ChangeValidatesForValidationType(String apptokenUrl, String baseUrl) {
		this.apptokenUrl = apptokenUrl;
		this.baseUrl = baseUrl;
		dataClientFactory = DataClientFactoryImp.usingAppTokenVerifierUrlAndBaseUrl(apptokenUrl,
				baseUrl);
		dataClient = dataClientFactory.factorUsingUserIdAndAppToken(
				"jsClientUser@system.cora.uu.se", "5d3f3ed4-4931-4924-9faa-8eaf5ac6457e");

	}

	public void changeValidationTypes() {
		ClientDataList listOfValidationTypes = dataClient.readList("validationType");
		for (ClientData validationTypeData : listOfValidationTypes.getDataList()) {
			ClientDataRecord validationTypeRecord = (ClientDataRecord) validationTypeData;

			ClientDataRecordGroup validationTypeRecordGroup = validationTypeRecord
					.getDataRecordGroup();
			String id = validationTypeRecordGroup.getId();
			validationTypeRecordGroup.removeChildrenWithTypeAndName(ClientDataRecordLink.class,
					"validatesRecordType");
			ClientDataRecordLink newLink = ClientDataProvider
					.createRecordLinkUsingNameInDataAndTypeAndId("validatesRecordType",
							"recordType", id);

			validationTypeRecordGroup.addChild(newLink);

			dataClient.update("validationType", id, validationTypeRecordGroup);
		}
	}

	public void changeValidationTypesToAbstractLevel(String system) {
		Optional<Map<String, String>> oMapFromDisk = readFromDisk(apptokenUrl, baseUrl, system);
		if (oMapFromDisk.isPresent()) {
			Map<String, String> mapFromDisk = oMapFromDisk.get();
			ClientDataList listOfValidationTypes = dataClient.readList("validationType");
			for (ClientData validationTypeData : listOfValidationTypes.getDataList()) {
				changeValidationTypeToAbstractForOneRecord(mapFromDisk, validationTypeData);
			}
		} else {
			System.err.println("Not possible to read map for system: " + system);
		}

	}

	private void changeValidationTypeToAbstractForOneRecord(Map<String, String> mapFromDisk,
			ClientData validationTypeData) {
		ClientDataRecord validationTypeRecord = (ClientDataRecord) validationTypeData;
		ClientDataRecordGroup validationTypeRecordGroup = validationTypeRecord.getDataRecordGroup();

		ClientDataRecordLink validatesLink = validationTypeRecordGroup
				.getFirstChildOfTypeAndName(ClientDataRecordLink.class, "validatesRecordType");
		String currentValidatesId = validatesLink.getLinkedRecordId();
		System.out.println("Checking: " + currentValidatesId);

		if (mapFromDisk.containsKey(currentValidatesId)) {
			String id = mapFromDisk.get(currentValidatesId);
			if (!currentValidatesId.equals(id)) {
				validationTypeRecordGroup.removeChildrenWithTypeAndName(ClientDataRecordLink.class,
						"validatesRecordType");
				ClientDataRecordLink newLink = ClientDataProvider
						.createRecordLinkUsingNameInDataAndTypeAndId("validatesRecordType",
								"recordType", id);
				validationTypeRecordGroup.addChild(newLink);
				dataClient.update("validationType", validationTypeRecordGroup.getId(),
						validationTypeRecordGroup);
				systemOutPrintlnBoldGreen("UpdatedvalidatesRecordTypeId: %s --> %s"
						.formatted(currentValidatesId, id));
			} else {
				System.out.println("Skipping same in map: " + currentValidatesId);
			}
		} else {
			System.err.println(
					"Current validatesRecordTypeId not found in map: " + currentValidatesId);
		}
	}

	private void systemOutPrintlnBoldGreen(String string) {
		System.out.println("\033[0;1m\u001B[32m" + string + "\u001B[0m");
	}

	private Optional<Map<String, String>> readFromDisk(String appTokenUrl, String baseUrl,
			String system) {
		String filePath = System.getProperty("user.home") + "/workspace/cora-change/impToParent"
				+ system + ".ser";

		Optional<Map<String, String>> readMapFromFile = readMapFromFile(filePath);
		System.out.println(readMapFromFile);
		return readMapFromFile;
	}

	public Optional<Map<String, String>> readMapFromFile(String filePath) {

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
			Map<String, String> readMap = (Map<String, String>) ois.readObject();
			return Optional.of(readMap);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	public void changeAbstractRecordTypesToImplementing() {
		ClientDataList listOfRecordTypes = dataClient.readList("recordType");
		for (ClientData recordTypeData : listOfRecordTypes.getDataList()) {
			ClientDataRecord recordTypeRecord = (ClientDataRecord) recordTypeData;
			ClientDataRecordGroup recordTypeRecordGroup = recordTypeRecord.getDataRecordGroup();

			ClientDataAtomic abstractAtomic = recordTypeRecordGroup
					.getFirstChildOfTypeAndName(ClientDataAtomic.class, "abstract");
			if ("true".equals(abstractAtomic.getValue())) {
				recordTypeRecordGroup.removeChildrenWithTypeAndName(ClientDataAtomic.class,
						"abstract");
				ClientDataAtomic newAtomic = ClientDataProvider
						.createAtomicUsingNameInDataAndValue("abstract", "false");

				recordTypeRecordGroup.addChild(newAtomic);

				// if (!"metadata".equals(recordTypeRecordGroup.getId())) {
				systemOutPrintlnBoldGreen(
						"changing to implementing for: " + recordTypeRecordGroup.getId());
				dataClient.update("recordType", recordTypeRecordGroup.getId(),
						recordTypeRecordGroup);
				// }
			}
		}
	}
}
