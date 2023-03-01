package se.uu.ub.cora.change;

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.javaclient.cora.DataClient;
import se.uu.ub.cora.javaclient.cora.DataClientFactoryImp;

public class UpdateAllRecordsAndLinkValidationType {

	private static final String ID = "id";
	private static final String DATA_DIVIDER = "dataDivider";
	private static final String RECORD_INFO = "recordInfo";
	private DataClientFactoryImp dataClientFactory;
	private DataClient dataClient;

	public UpdateAllRecordsAndLinkValidationType(String apptokenUrl, String baseUrl) {
		dataClientFactory = DataClientFactoryImp.usingAppTokenVerifierUrlAndBaseUrl(apptokenUrl,
				baseUrl);
		dataClient = dataClientFactory.factorUsingUserIdAndAppToken("141414",
				"63e6bd34-02a1-4c82-8001-158c104cae0e");
	}

	public void updateAllRecords() {
		ClientDataList listOfRecordTypes = dataClient.readList("recordType");
		int count = 0;

		for (ClientData data : listOfRecordTypes.getDataList()) {
			ClientDataRecord dataRecord = (ClientDataRecord) data;

			System.out.println(
					"Counting type: " + dataRecord.getId() + " partial counting: " + count);
			// WE SHOULD SKIP ABSTRACT TYPES
			if (noErrorOnListingTheRecordTypes(dataRecord)) {
				ClientDataList listRecordsForType = dataClient.readList(dataRecord.getId());
				for (ClientData recordData : listRecordsForType.getDataList()) {
					count++;
				}
				count++;
			}
		}
		System.out.println("Records in the system: " + count);
	}

	private boolean noErrorOnListingTheRecordTypes(ClientDataRecord dataRecord) {
		return !dataRecord.getId().equals("sound") && !dataRecord.getId().equals("systemSecret")
				&& !dataRecord.getId().equals("image") && !dataRecord.getId().equals("appToken")
				&& !dataRecord.getId().equals("binary");
	}

}
