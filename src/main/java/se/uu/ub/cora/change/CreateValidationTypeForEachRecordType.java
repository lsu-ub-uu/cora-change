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

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.javaclient.JavaClientAppTokenCredentials;
import se.uu.ub.cora.javaclient.JavaClientProvider;
import se.uu.ub.cora.javaclient.data.DataClient;
import se.uu.ub.cora.javaclient.data.DataClientException;

public class CreateValidationTypeForEachRecordType {

	private static final String VALIDATION_DEF_TEXT = "ValidationDefText";
	private static final String ID = "id";
	private static final String DATA_DIVIDER = "dataDivider";
	private static final String RECORD_INFO = "recordInfo";
	private DataClient dataClient;

	public CreateValidationTypeForEachRecordType(String apptokenUrl, String baseUrl) {
		JavaClientAppTokenCredentials appTokenCredentials = new JavaClientAppTokenCredentials(
				baseUrl, apptokenUrl, "systemoneAdmin@system.cora.uu.se",
				"5d3f3ed4-4931-4924-9faa-8eaf5ac6457e");
		dataClient = JavaClientProvider
				.createDataClientUsingJavaClientAppTokenCredentials(appTokenCredentials);

	}

	public void createValidationTypes() {
		ClientDataList listOfRecordTypes = dataClient.readList("recordType");

		for (ClientData recordtypeData : listOfRecordTypes.getDataList()) {
			ClientDataRecord recordTypeRecord = (ClientDataRecord) recordtypeData;

			ClientDataRecordGroup recordTypeRecordGroup = recordTypeRecord.getDataRecordGroup();
			if (isImplementingType(recordTypeRecordGroup)) {
				System.out.println("Convert: " + recordTypeRecordGroup.getId());

				createValidationType(recordTypeRecordGroup);

			} else {
				System.out.println("Skip: " + recordTypeRecordGroup.getId() + " is abstract.");
			}
			System.out.println();
		}
	}

	private void createValidationType(ClientDataRecordGroup recordTypeRecordGroup) {
		if (validateTypeDoesNotExist(recordTypeRecordGroup)) {

			ClientDataRecordGroup validationType = createValidationTypeRecordGroup(
					recordTypeRecordGroup);
			dataClient.create("validationType", validationType);
		} else {
			System.out.println("ValidationType already exists... ");
		}
	}

	private boolean validateTypeDoesNotExist(ClientDataRecordGroup recordTypeRecordGroup) {
		try {
			dataClient.read("validationType", recordTypeRecordGroup.getId());
			return false;
		} catch (DataClientException e) {
			return true;
		}
	}

	private ClientDataRecordGroup createValidationTypeRecordGroup(
			ClientDataRecordGroup readRecordTypeRecordGroup) {
		ClientDataRecordGroup dataRecordGroup = ClientDataProvider
				.createRecordGroupUsingNameInData("validationType");

		ClientDataGroup readRecordInfo = (ClientDataGroup) readRecordTypeRecordGroup
				.getFirstChildWithNameInData(RECORD_INFO);
		ClientDataGroup recordInfo = ClientDataProvider.createGroupUsingNameInData(RECORD_INFO);
		recordInfo.addChild(copyLink(readRecordInfo, DATA_DIVIDER));

		ClientDataAtomic id = ClientDataProvider.createAtomicUsingNameInDataAndValue(ID,
				readRecordTypeRecordGroup.getId());
		recordInfo.addChild(id);

		createAndStoreDefTextForValidationType(readRecordTypeRecordGroup);

		// can not be added until we have created the validation type for validationType
		// recordInfo.addChild(createLink("validationType", "validationType",
		// "validationType"));

		dataRecordGroup.addChild(recordInfo);
		dataRecordGroup.addChild(copyLink((ClientDataGroup) readRecordTypeRecordGroup, "textId"));

		dataRecordGroup.addChild(ClientDataProvider.createRecordLinkUsingNameInDataAndTypeAndId(
				"defTextId", "coraText", readRecordTypeRecordGroup.getId() + VALIDATION_DEF_TEXT));

		dataRecordGroup.addChild(createLink("validatesRecordType", "recordType",
				getValidatesRecordType(readRecordTypeRecordGroup)));
		dataRecordGroup
				.addChild(copyLink((ClientDataGroup) readRecordTypeRecordGroup, "newMetadataId"));
		dataRecordGroup
				.addChild(copyLink((ClientDataGroup) readRecordTypeRecordGroup, "metadataId"));
		dataRecordGroup.addChild(
				copyLink((ClientDataGroup) readRecordTypeRecordGroup, "newPresentationFormId"));
		dataRecordGroup.addChild(
				copyLink((ClientDataGroup) readRecordTypeRecordGroup, "presentationFormId"));

		return dataRecordGroup;
	}

	private void createAndStoreDefTextForValidationType(
			ClientDataRecordGroup readRecordTypeRecordGroup) {
		ClientDataGroup readRecordInfo = (ClientDataGroup) readRecordTypeRecordGroup
				.getFirstChildWithNameInData(RECORD_INFO);
		ClientDataRecordGroup dataRecordGroupText = ClientDataProvider
				.createRecordGroupUsingNameInData("text");
		ClientDataGroup recordInfoText = ClientDataProvider.createGroupUsingNameInData(RECORD_INFO);
		recordInfoText.addChild(copyLink(readRecordInfo, DATA_DIVIDER));

		ClientDataAtomic idText = ClientDataProvider.createAtomicUsingNameInDataAndValue(ID,
				readRecordTypeRecordGroup.getId() + VALIDATION_DEF_TEXT);
		recordInfoText.addChild(idText);
		dataRecordGroupText.addChild(recordInfoText);
		// sv
		ClientDataGroup textPartSv = ClientDataProvider.createGroupUsingNameInData("textPart");
		dataRecordGroupText.addChild(textPartSv);
		textPartSv.addAttributeByIdWithValue("type", "default");
		textPartSv.addAttributeByIdWithValue("lang", "sv");
		ClientDataAtomic defTextSv = ClientDataProvider.createAtomicUsingNameInDataAndValue("text",
				"Validerings typ för posttypen " + readRecordTypeRecordGroup.getId());
		textPartSv.addChild(defTextSv);
		// en
		ClientDataGroup textPartEn = ClientDataProvider.createGroupUsingNameInData("textPart");
		dataRecordGroupText.addChild(textPartEn);
		textPartEn.addAttributeByIdWithValue("type", "alternative");
		textPartEn.addAttributeByIdWithValue("lang", "en");
		ClientDataAtomic defTextEn = ClientDataProvider.createAtomicUsingNameInDataAndValue("text",
				"Validation type for the record type " + readRecordTypeRecordGroup.getId());
		textPartEn.addChild(defTextEn);

		dataClient.create("coraText", dataRecordGroupText);
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

	private boolean isImplementingType(ClientDataRecordGroup recordTypeRecordGroup) {
		return recordTypeRecordGroup.containsChildWithNameInData("abstract")
				&& recordTypeRecordGroup.getFirstDataAtomicWithNameInData("abstract").getValue()
						.equals("false");
	}

	private String getValidatesRecordType(ClientDataRecordGroup recordTypeRecordGroup) {
		if (recordTypeRecordGroup.containsChildWithNameInData("parentId")) {
			return getValidatesRecordType(readParentLink(recordTypeRecordGroup));
		}
		System.out.println("Validates recordType: " + recordTypeRecordGroup.getId());
		return recordTypeRecordGroup.getId();
	}

	private ClientDataRecordGroup readParentLink(ClientDataRecordGroup recordTypeRecordGroup) {
		ClientDataRecordLink parentLink = (ClientDataRecordLink) recordTypeRecordGroup
				.getFirstChildWithNameInData("parentId");
		ClientDataRecord parentRecord = dataClient.read(parentLink.getLinkedRecordType(),
				parentLink.getLinkedRecordId());
		return parentRecord.getDataRecordGroup();
	}

	private ClientDataRecordLink getLinkIdByNameInData(ClientDataGroup recordTypeRecordGroup,
			String linkNameInData) {
		return (ClientDataRecordLink) recordTypeRecordGroup
				.getFirstChildWithNameInData(linkNameInData);
	}

	public void deleteValidationTypeTexts() {
		ClientDataList listOfRecordTypes = dataClient.readList("text");
		for (ClientData recordtypeData : listOfRecordTypes.getDataList()) {
			ClientDataRecord textRecord = (ClientDataRecord) recordtypeData;
			if (textRecord.getId().endsWith(VALIDATION_DEF_TEXT)) {
				System.out.println("Delete: " + textRecord.getId());
				dataClient.delete("coraText", textRecord.getId());
			}
		}
	}

	public void deleteValidationTypes() {
		ClientDataList listOfRecordTypes = dataClient.readList("validationType");
		for (ClientData recordtypeData : listOfRecordTypes.getDataList()) {
			ClientDataRecord validationTypeRecord = (ClientDataRecord) recordtypeData;
			System.out.println("Delete: " + validationTypeRecord.getId());
			dataClient.delete("validationType", validationTypeRecord.getId());
		}
	}

}
