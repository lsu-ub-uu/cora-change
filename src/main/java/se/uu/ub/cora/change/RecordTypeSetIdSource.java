/*
 * Copyright 2023, 2025 Uppsala University Library
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
package se.uu.ub.cora.change;

import java.text.MessageFormat;
import java.util.List;

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.javaclient.JavaClientAppTokenCredentials;
import se.uu.ub.cora.javaclient.JavaClientProvider;
import se.uu.ub.cora.javaclient.data.DataClient;

public class RecordTypeSetIdSource {

	private DataClient dataClient;

	public RecordTypeSetIdSource(JavaClientAppTokenCredentials appTokenCred) {
		setUpJavaClient(appTokenCred);

	}

	private void setUpJavaClient(JavaClientAppTokenCredentials appTokenCred) {
		dataClient = JavaClientProvider
				.createDataClientUsingJavaClientAppTokenCredentials(appTokenCred);
	}

	public void addSourceIdToAllRecordTypes() {
		for (ClientData recordType : readAllRecordTypes()) {
			ClientDataRecordGroup dataRecordGroup = ((ClientDataRecord) recordType)
					.getDataRecordGroup();
			if (!dataRecordGroup.containsChildWithNameInData("idSource")) {
				try {
					updateRecordWithSourceIdAndLog(dataRecordGroup);
					String message = "Record updated with type: {0} id: {1}";
					System.out.println(MessageFormat.format(message, dataRecordGroup.getType(),
							dataRecordGroup.getId()));
				} catch (Exception e) {
					String message = "Record could not be updated with type: {0} id: {1}";
					System.out.println(MessageFormat.format(message, dataRecordGroup.getType(),
							dataRecordGroup.getId()));
				}
			} else {
				printIdSourceAlreadyExists(dataRecordGroup);
			}
		}
	}

	private void printIdSourceAlreadyExists(ClientDataRecordGroup dataRecordGroup) {
		String idSourceValue = dataRecordGroup.getFirstAtomicValueWithNameInData("idSource");
		String message = "Id source already({2}) exists in type: {0} id: {1}";
		System.out.println(MessageFormat.format(message, dataRecordGroup.getType(),
				dataRecordGroup.getId(), idSourceValue));
	}

	private List<ClientData> readAllRecordTypes() {
		ClientDataList readRecordTypes = dataClient.readList("recordType");
		return readRecordTypes.getDataList();
	}

	private void updateRecordWithSourceIdAndLog(ClientDataRecordGroup dataRecordGroup) {
		addIdSource(dataRecordGroup);
		dataClient.update("recordType", dataRecordGroup.getId(), dataRecordGroup);

	}

	private void addIdSource(ClientDataRecordGroup dataRecordGroup) {
		String idSourceValue = getIdSourceValue(dataRecordGroup);

		ClientDataAtomic idSource = ClientDataProvider
				.createAtomicUsingNameInDataAndValue("idSource", idSourceValue);
		dataRecordGroup.addChild(idSource);
	}

	private String getIdSourceValue(ClientDataRecordGroup dataRecordGroup) {
		String userSuppliedId = dataRecordGroup.getFirstAtomicValueWithNameInData("userSuppliedId");

		if (("true").equals(userSuppliedId)) {
			return "userSupplied";
		}
		return "timestamp";
	}

	public void removeUserSupplierFromAllRecordTypes() {
		for (ClientData recordType : readAllRecordTypes()) {
			ClientDataRecordGroup dataRecordGroup = ((ClientDataRecord) recordType)
					.getDataRecordGroup();
			if (dataRecordGroup.containsChildWithNameInData("idSource")) {
				dataRecordGroup.removeFirstChildWithNameInData("userSuppliedId");
				dataClient.update("recordType", dataRecordGroup.getId(), dataRecordGroup);
			} else {
				String message = "IdSource must be created first for typr:{0}, and id:{1}";
				System.out.println(MessageFormat.format(message, dataRecordGroup.getType(),
						dataRecordGroup.getId()));
			}
		}
	}

}
