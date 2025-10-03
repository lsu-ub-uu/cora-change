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
package se.uu.ub.cora.change.spy;

import java.util.Optional;

import se.uu.ub.cora.javaclient.rest.RestClient;
import se.uu.ub.cora.javaclient.rest.RestResponse;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class RestClientSpy implements RestClient {

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public RestClientSpy() {
		var restResponse = new RestResponse(200, "someResponse", Optional.empty(),
				Optional.empty());

		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("readRecordAsJson", () -> restResponse);
		MRV.setDefaultReturnValuesSupplier("createRecordFromJson", () -> restResponse);
		MRV.setDefaultReturnValuesSupplier("updateRecordFromJson", () -> restResponse);
		MRV.setDefaultReturnValuesSupplier("deleteRecord", () -> restResponse);
		MRV.setDefaultReturnValuesSupplier("readRecordListAsJson", () -> restResponse);
		MRV.setDefaultReturnValuesSupplier("readIncomingLinksAsJson", () -> restResponse);
		MRV.setDefaultReturnValuesSupplier("readRecordListWithFilterAsJson", () -> restResponse);
		MRV.setDefaultReturnValuesSupplier("batchIndexWithFilterAsJson", () -> restResponse);
		MRV.setDefaultReturnValuesSupplier("searchRecordWithSearchCriteriaAsJson",
				() -> restResponse);
		MRV.setDefaultReturnValuesSupplier("validateRecordAsJson", () -> restResponse);
		MRV.setDefaultReturnValuesSupplier("download", () -> restResponse);
	}

	@Override
	public RestResponse readRecordAsJson(String recordType, String recordId) {
		return (RestResponse) MCR.addCallAndReturnFromMRV("recordType", recordType, "recordId",
				recordId);
	}

	@Override
	public RestResponse createRecordFromJson(String recordType, String json) {
		return (RestResponse) MCR.addCallAndReturnFromMRV("recordType", recordType, "json", json);
	}

	@Override
	public RestResponse updateRecordFromJson(String recordType, String recordId, String json) {
		return (RestResponse) MCR.addCallAndReturnFromMRV("recordType", recordType, "recordId",
				recordId, "json", json);
	}

	@Override
	public RestResponse deleteRecord(String recordType, String recordId) {
		return (RestResponse) MCR.addCallAndReturnFromMRV("recordType", recordType, "recordId",
				recordId);
	}

	@Override
	public RestResponse readRecordListAsJson(String recordType) {
		return (RestResponse) MCR.addCallAndReturnFromMRV("recordType", recordType);
	}

	@Override
	public RestResponse readIncomingLinksAsJson(String recordType, String recordId) {
		return (RestResponse) MCR.addCallAndReturnFromMRV("recordType", recordType, "recordId",
				recordId);
	}

	@Override
	public RestResponse readRecordListWithFilterAsJson(String recordType, String filter) {
		return (RestResponse) MCR.addCallAndReturnFromMRV("recordType", recordType, "filter",
				filter);
	}

	@Override
	public RestResponse batchIndexWithFilterAsJson(String recordType, String indexSettingsAsJson) {
		return (RestResponse) MCR.addCallAndReturnFromMRV("recordType", recordType,
				"indexSettingsAsJson", indexSettingsAsJson);
	}

	@Override
	public RestResponse searchRecordWithSearchCriteriaAsJson(String searchId, String json) {
		return (RestResponse) MCR.addCallAndReturnFromMRV("searchId", searchId, "json", json);
	}

	@Override
	public RestResponse validateRecordAsJson(String json) {
		return (RestResponse) MCR.addCallAndReturnFromMRV("json", json);
	}

	@Override
	public RestResponse download(String type, String id, String representation) {
		return (RestResponse) MCR.addCallAndReturnFromMRV("type", type, "id", id, "representation",
				representation);
	}

}
