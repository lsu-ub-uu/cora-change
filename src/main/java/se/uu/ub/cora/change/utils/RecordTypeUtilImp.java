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
package se.uu.ub.cora.change.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.javaclient.data.DataClient;

public class RecordTypeUtilImp implements RecordTypeUtil {
	private static final String PARENT_ID = "parentId";
	private DataClient dataClient;

	public static RecordTypeUtilImp usingDataClient(DataClient dataClient) {
		return new RecordTypeUtilImp(dataClient);
	}

	private RecordTypeUtilImp(DataClient dataClient) {
		this.dataClient = dataClient;
	}

	@Override
	public Map<String, String> getMapOfImplementingToParent() {
		List<ClientData> allRecordTypes = readAllRecordTypes();
		Map<String, String> parentMap = buildParentMap(allRecordTypes);
		return buildImplementingToParentMap(parentMap);
	}

	private List<ClientData> readAllRecordTypes() {
		ClientDataList readList = dataClient.readList("recordType");
		return readList.getDataList();
	}

	private Map<String, String> buildParentMap(List<ClientData> allRecordTypes) {
		Map<String, String> parentMap = new HashMap<>();
		for (ClientData recordTypeClientData : allRecordTypes) {
			ClientDataRecord dataRecord = (ClientDataRecord) recordTypeClientData;
			String id = getParentIdOrOwnIdIfNoParent(dataRecord);
			parentMap.put(dataRecord.getId(), id);
		}
		return parentMap;
	}

	private String getParentIdOrOwnIdIfNoParent(ClientDataRecord dataRecord) {
		ClientDataRecordGroup dataRecordGroup = dataRecord.getDataRecordGroup();

		if (hasParent(dataRecordGroup)) {
			ClientDataRecordLink parentLink = dataRecordGroup
					.getFirstChildOfTypeAndName(ClientDataRecordLink.class, PARENT_ID);
			return parentLink.getLinkedRecordId();
		}
		return dataRecord.getId();
	}

	private boolean hasParent(ClientDataRecordGroup dataRecordGroup) {
		return dataRecordGroup.containsChildOfTypeAndName(ClientDataRecordLink.class, PARENT_ID);
	}

	private Map<String, String> buildImplementingToParentMap(Map<String, String> parentMap) {
		Map<String, String> implementingToParentMap = new HashMap<>();
		for (Entry<String, String> entry : parentMap.entrySet()) {
			String parentId = entry.getValue();
			implementingToParentMap.put(entry.getKey(), getTopParent(parentMap, parentId));
		}
		return implementingToParentMap;
	}

	private String getTopParent(Map<String, String> parentMap, String id) {
		String parentId = parentMap.get(id);
		if (id.equals(parentId)) {
			return id;
		} else {
			return getTopParent(parentMap, parentId);
		}
	}

	public DataClient onlyForTestGetDataClient() {
		return dataClient;
	}

}
