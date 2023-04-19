package se.uu.ub.cora.change;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataAttribute;
import se.uu.ub.cora.clientdata.ClientDataChild;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.clientdata.converter.ClientDataToJsonConverter;
import se.uu.ub.cora.clientdata.converter.ClientDataToJsonConverterFactory;
import se.uu.ub.cora.clientdata.converter.ClientDataToJsonConverterProvider;
import se.uu.ub.cora.javaclient.cora.DataClient;
import se.uu.ub.cora.javaclient.cora.DataClientFactoryImp;

public class CollectAllFieldsForAbstractRecordTypes {

	private DataClientFactoryImp dataClientFactory;
	private DataClient dataClient;
	Map<String, List<String>> mapWithListOfGroupIds = new HashMap<>();
	Map<String, ClientDataRecordGroup> metadataToStore = new HashMap<>();

	public CollectAllFieldsForAbstractRecordTypes(String apptokenUrl, String baseUrl) {
		// TODO Auto-generated constructor stub
		dataClientFactory = DataClientFactoryImp.usingAppTokenVerifierUrlAndBaseUrl(apptokenUrl,
				baseUrl);
		dataClient = dataClientFactory.factorUsingUserIdAndAppToken("141414",
				"63e6bd34-02a1-4c82-8001-158c104cae0e");
	}

	public void collectAndStoreGroupForAbstract() {
		collectAbstractRecordTypes();
		collectRecordTypesRelatedToAbstractRecordTypes();
		System.out.println(mapWithListOfGroupIds);

		agregateMetadataGroupPerRecordType();
	}

	private void agregateMetadataGroupPerRecordType() {

		for (Entry<String, List<String>> mapWithListOfGroupId : mapWithListOfGroupIds.entrySet()) {
			String idToAddStuffTo = mapWithListOfGroupId.getKey();
			List<String> idsToAddStuffFrom = mapWithListOfGroupId.getValue();

			ClientDataRecord addToRecord = dataClient.read("metadata", idToAddStuffTo);
			ClientDataRecordGroup addToRecordGroup = addToRecord.getDataRecordGroup();
			resetChildReferences(addToRecordGroup);

			metadataToStore.put(idToAddStuffTo, addToRecordGroup);

			ClientDataRecordGroup addToGroupWithGroup = null;
			for (String idToAddStuffFrom : idsToAddStuffFrom) {

				// ClientDataRecordGroup addFrom = dataClient.read("metadata", idToAddStuffFrom)
				// .getDataRecordGroup();
				// ClientDataGroup idToAddStuffFromGroup = getDefinitionIdForUpdate(addFrom);

				/**
				 * WRONG, fix tomorrow, mixed metadata and recordTypes...
				 */
				addToGroupWithGroup = addToGroupWithGroup(idToAddStuffTo, idToAddStuffFrom);

			}
			/**
			 * Just for write out latest addToGroupWithGroup
			 */

			ClientDataToJsonConverterFactory converterFactory = ClientDataToJsonConverterProvider
					.createImplementingFactory();
			ClientDataToJsonConverter converter = converterFactory
					.factorUsingConvertible(addToGroupWithGroup);
			String json = converter.toJson();

			System.out.println(json);
			System.out.println();
		}

	}

	private void resetChildReferences(ClientDataRecordGroup addToRecordGroup) {
		addToRecordGroup.removeFirstChildWithTypeAndName(ClientDataGroup.class, "childReferences");
		ClientDataGroup childReferences = ClientDataProvider
				.createGroupUsingNameInData("childReferences");
		addToRecordGroup.addChild(childReferences);
	}

	private String getDefinitionIdForUpdate(ClientDataRecordGroup addTo) {
		ClientDataRecordLink link = addTo.getFirstChildOfTypeAndName(ClientDataRecordLink.class,
				"metadataId");
		return link.getLinkedRecordId();
	}

	private ClientDataRecordGroup addToGroupWithGroup(String idToAddStuffTo,
			String idToAddStuffFrom) {
		ClientDataRecordGroup addTo = null;
		if (metadataToStore.containsKey(idToAddStuffTo)) {
			addTo = metadataToStore.get(idToAddStuffTo);
		} else {
			addTo = dataClient.read("metadata", idToAddStuffTo).getDataRecordGroup();

		}

		ClientDataGroup childReferencesAddTo = addTo
				.getFirstChildOfTypeAndName(ClientDataGroup.class, "childReferences");

		ClientDataRecordGroup addFrom = dataClient.read("recordType", idToAddStuffFrom)
				.getDataRecordGroup();
		ClientDataGroup definitionForUpdate = getDefinitionIdForUpdate(addFrom);
		ClientDataGroup childReferences = getChildReferncesGroup(definitionForUpdate);
		for (ClientDataChild childReferenceChild : childReferences
				.getAllChildrenWithNameInData("childReference")) {
			ClientDataGroup childReference = (ClientDataGroup) childReferenceChild;
			ClientDataRecordLink refLink = childReference
					.getFirstChildOfTypeAndName(ClientDataRecordLink.class, "ref");
			ClientDataRecord field = dataClient.read(refLink.getLinkedRecordType(),
					refLink.getLinkedRecordId());
			ClientDataRecordGroup fieldDataRecordGroup = field.getDataRecordGroup();
			ClientDataAttribute fieldType = fieldDataRecordGroup.getAttribute("type");
			if (fieldType.equals("group")) {
				addToGroupWithGroup(idToAddStuffFrom, fieldDataRecordGroup.getId());
			}

			childReferencesAddTo.addChild(childReferences);
		}
		return addTo;

	}

	private ClientDataGroup getChildReferncesGroup(ClientDataGroup definitionForUpdate) {
		return definitionForUpdate.getFirstChildOfTypeAndName(ClientDataGroup.class,
				"childReferences");
	}

	private void collectAbstractRecordTypes() {
		ClientDataList listOfRecordTypes = dataClient.readList("recordType");

		for (ClientData recordTypeData : listOfRecordTypes.getDataList()) {

			ClientDataRecord recordTypeRecord = (ClientDataRecord) recordTypeData;
			ClientDataRecordGroup recordTypeRecordGroup = recordTypeRecord.getDataRecordGroup();

			if (isRecordTypeAbstract(recordTypeRecordGroup) && !hasParent(recordTypeRecordGroup)) {
				String id = recordTypeRecordGroup.getId();
				ClientDataRecordGroup addTo1 = dataClient.read("recordType", id)
						.getDataRecordGroup();
				String addToDataGroupId = getDefinitionIdForUpdate(addTo1);

				mapWithListOfGroupIds.put(addToDataGroupId, new ArrayList<>());
			}

		}
	}

	private boolean hasParent(ClientDataRecordGroup recordTypeRecordGroup) {
		return recordTypeRecordGroup.containsChildWithNameInData("parentId");
	}

	private void collectRecordTypesRelatedToAbstractRecordTypes() {
		ClientDataList listOfRecordTypes = dataClient.readList("recordType");

		for (ClientData recordTypeData : listOfRecordTypes.getDataList()) {

			ClientDataRecord recordTypeRecord = (ClientDataRecord) recordTypeData;
			ClientDataRecordGroup recordTypeRecordGroup = recordTypeRecord.getDataRecordGroup();

			if (isRecordImplementing(recordTypeRecordGroup)
					&& isRelatedToAbstract(recordTypeRecordGroup)) {
				List<String> list = mapWithListOfGroupIds
						.get(getValidatesRecordType(recordTypeRecordGroup));
				String id = recordTypeRecordGroup.getId();
				ClientDataRecordGroup addTo1 = dataClient.read("recordType", id)
						.getDataRecordGroup();
				String addToDataGroupId = getDefinitionIdForUpdate(addTo1);

				list.add(addToDataGroupId);
			}
		}
	}

	private boolean isRecordImplementing(ClientDataRecordGroup recordTypeRecordGroup) {
		return !isRecordTypeAbstract(recordTypeRecordGroup);
	}

	private boolean isRelatedToAbstract(ClientDataRecordGroup recordTypeRecordGroup) {
		return mapWithListOfGroupIds.containsKey(getValidatesRecordType(recordTypeRecordGroup));
	}

	private boolean isRecordTypeAbstract(ClientDataRecordGroup recordTypeRecordGroup) {
		return "true".equals(recordTypeRecordGroup.getFirstAtomicValueWithNameInData("abstract"));
	}

	private String getValidatesRecordType(ClientDataRecordGroup recordTypeRecordGroup) {
		if (hasParent(recordTypeRecordGroup)) {
			return getValidatesRecordType(readParentLink(recordTypeRecordGroup));
		}
		// System.out.println("Validates recordType: " + recordTypeRecordGroup.getId());
		return recordTypeRecordGroup.getId();
	}

	private ClientDataRecordGroup readParentLink(ClientDataRecordGroup recordTypeRecordGroup) {
		ClientDataRecordLink parentLink = (ClientDataRecordLink) recordTypeRecordGroup
				.getFirstChildWithNameInData("parentId");
		ClientDataRecord parentRecord = dataClient.read(parentLink.getLinkedRecordType(),
				parentLink.getLinkedRecordId());
		return parentRecord.getDataRecordGroup();
	}

}
