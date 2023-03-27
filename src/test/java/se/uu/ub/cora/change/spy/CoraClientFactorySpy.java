package se.uu.ub.cora.change.spy;

import se.uu.ub.cora.javaclient.cora.CoraClientFactory;
import se.uu.ub.cora.javaclient.cora.DataClient;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class CoraClientFactorySpy implements CoraClientFactory {

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public CoraClientFactorySpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("factorUsingUserIdAndAppToken", DataClientSpy::new);
		MRV.setDefaultReturnValuesSupplier("factorUsingAuthToken", DataClientSpy::new);
	}

	@Override
	public DataClient factorUsingUserIdAndAppToken(String userId, String appToken) {
		return (DataClient) MCR.addCallAndReturnFromMRV("userId", userId, "appToken", appToken);
	}

	@Override
	public DataClient factorUsingAuthToken(String authToken) {
		return (DataClient) MCR.addCallAndReturnFromMRV("authToken", authToken);
	}

}
