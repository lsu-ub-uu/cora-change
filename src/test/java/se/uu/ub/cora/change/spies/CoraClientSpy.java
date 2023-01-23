package se.uu.ub.cora.change.spies;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.javaclient.cora.CoraClient;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class CoraClientSpy implements CoraClient {
	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public CoraClientSpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("readListAsDataRecords",
				ArrayList<ClientDataRecord>::new);
	}

	@Override
	public String create(String recordType, String json) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String create(String recordType, ClientDataGroup dataGroup) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String read(String recordType, String recordId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String update(String recordType, String recordId, String json) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String delete(String recordType, String recordId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String readList(String recordType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String readIncomingLinks(String recordType, String recordId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClientDataRecord readAsDataRecord(String recordType, String recordId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String update(String recordType, String recordId, ClientDataGroup dataGroup) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ClientDataRecord> readListAsDataRecords(String recordType) {
		return (List<ClientDataRecord>) MCR.addCallAndReturnFromMRV("recordType", recordType);
	}

	@Override
	public String indexData(ClientDataRecord clientDataRecord) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String indexData(String recordType, String recordId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String indexDataWithoutExplicitCommit(String recordType, String recordId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String removeFromIndex(String recordType, String recordId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String indexRecordsOfType(String recordType, String settingsAsJson) {
		// TODO Auto-generated method stub
		return null;
	}

}
