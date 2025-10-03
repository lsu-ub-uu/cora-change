/*
 * Copyright 2025 Uppsala University Library
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
import se.uu.ub.cora.javaclient.JavaClientAppTokenCredentials;
import se.uu.ub.cora.javaclient.JavaClientProvider;
import se.uu.ub.cora.javaclient.data.DataClient;

public class ChangeValidatesForValidationType {

	private DataClient dataClient;
	private String apptokenUrl;
	private String baseUrl;

	public ChangeValidatesForValidationType(String apptokenUrl, String baseUrl) {
		JavaClientAppTokenCredentials appTokenCredentials = new JavaClientAppTokenCredentials(
				baseUrl, apptokenUrl, "systemoneAdmin@system.cora.uu.se",
				"5d3f3ed4-4931-4924-9faa-8eaf5ac6457e");
		dataClient = JavaClientProvider
				.createDataClientUsingJavaClientAppTokenCredentials(appTokenCredentials);
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
