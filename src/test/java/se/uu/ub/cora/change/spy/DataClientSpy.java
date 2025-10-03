/*
 * Copyright 2023 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.change.spy;

import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.clientdata.spies.ClientDataListSpy;
import se.uu.ub.cora.javaclient.data.DataClient;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class DataClientSpy implements DataClient {
	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public DataClientSpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("readList", ClientDataListSpy::new);
	}

	@Override
	public ClientDataRecord create(String recordType, ClientDataRecordGroup dataRecordGroup) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClientDataRecord read(String recordType, String recordId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClientDataList readList(String recordType) {
		return (ClientDataList) MCR.addCallAndReturnFromMRV("recordType", recordType);
	}

	@Override
	public ClientDataRecord update(String recordType, String recordId,
			ClientDataRecordGroup dataRecordGroup) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(String recordType, String recordId) {
		// TODO Auto-generated method stub

	}

	@Override
	public ClientDataList readIncomingLinks(String recordType, String recordId) {
		// TODO Auto-generated method stub
		return null;
	}

}
