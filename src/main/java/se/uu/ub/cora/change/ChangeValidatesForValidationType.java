package se.uu.ub.cora.change;

import se.uu.ub.cora.clientdata.ClientData;
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

	public ChangeValidatesForValidationType(String apptokenUrl, String baseUrl) {
		dataClientFactory = DataClientFactoryImp.usingAppTokenVerifierUrlAndBaseUrl(apptokenUrl,
				baseUrl);
		dataClient = dataClientFactory.factorUsingUserIdAndAppToken("141414",
				"63e6bd34-02a1-4c82-8001-158c104cae0e");

	}

	public void changeValidationTypes() {
		ClientDataList listOfValidationTypes = dataClient.readList("validationType");

		for (ClientData validationTypeData : listOfValidationTypes.getDataList()) {
			ClientDataRecord validationTypeRecord = (ClientDataRecord) validationTypeData;

			ClientDataRecordGroup validationTypeRecordGroup = validationTypeRecord
					.getDataRecordGroup();
			// if (isImplementingType(validationTypeRecordGroup)) {
			// System.out.println("Convert: " + validationTypeRecordGroup.getId());
			//
			// createValidationType(validationTypeRecordGroup);
			//
			// } else {
			// System.out.println("Skip: " + validationTypeRecordGroup.getId() + " is abstract.");
			// }
			// System.out.println();
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

}
