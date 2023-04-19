package se.uu.ub.cora.change;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataParent;
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
	Map<String, ClientDataRecordGroup> metadataRecordGroupToStore = new HashMap<>();

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

			metadataRecordGroupToStore.put(idToAddStuffTo, addToRecordGroup);

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

	private ClientDataRecordGroup addToGroupWithGroup(String toId, String fromId) {
		ClientDataRecordGroup toRecordGroup = getToMetadataRecordGroupFromHolderOrServer(toId);
		ClientDataGroup toChildReferences = toRecordGroup
				.getFirstChildOfTypeAndName(ClientDataGroup.class, "childReferences");

		ClientDataRecordGroup fromRecordGroup = getFromMetadataRecordGroupFromServer(fromId);
		// ClientDataGroup definitionForUpdate = getDefinitionIdForUpdate(addFrom);
		List<ClientDataGroup> fromChildReferences = getChildRefrencesList(fromRecordGroup);

		// for (ClientDataChild fromChildReferenceChild : fromChildReferences
		// .getAllChildrenWithNameInData("childReference")) {
		for (ClientDataGroup fromChildReference : fromChildReferences) {
			// ClientDataRecordLink fromRefLink = fromChildReference
			// .getFirstChildOfTypeAndName(ClientDataRecordLink.class, "ref");
			// // ClientDataRecord field = dataClient.read(fromRefLink.getLinkedRecordType(),
			// // fromRefLink.getLinkedRecordId());
			// // ClientDataRecordGroup fieldDataRecordGroup = field.getDataRecordGroup();
			// ClientDataRecordGroup childDataRecordGroup = getFromMetadataRecordGroupFromServer(
			// fromRefLink.getLinkedRecordId());
			//
			// ClientDataAttribute fieldType = childDataRecordGroup.getAttribute("type");
			// if (fieldType.equals("group")) {
			// addToGroupWithGroup(fromId, childDataRecordGroup.getId());
			// }
			//
			// toChildReferences.addChild(fromChildReferences);
			Optional<ClientDataGroup> oToChildReference = getMatchingChildReference(
					toChildReferences, fromChildReference);
			if (oToChildReference.isPresent()) {
				// fix min max constraints

			} else {
				int size = toChildReferences.getChildren().size();
				fromChildReference.setRepeatId(Integer.toString(size + 1));
				toChildReferences.addChild(fromChildReference);
			}
		}
		return toRecordGroup;

	}

	private Optional<ClientDataGroup> getMatchingChildReference(ClientDataGroup toChildReferences,
			ClientDataGroup fromChildReference) {
		ClientDataRecordLink fromLink = fromChildReference
				.getFirstChildOfTypeAndName(ClientDataRecordLink.class, "ref");
		String fromLinkedRecordId = fromLink.getLinkedRecordId();
		// simple, if same linked id, just return it
		for (ClientDataGroup toChildReference : toChildReferences
				.getAllGroupsWithNameInData("childReference")) {
			ClientDataRecordLink toLink = toChildReference
					.getFirstChildOfTypeAndName(ClientDataRecordLink.class, "ref");
			String toLinkedRecordId = toLink.getLinkedRecordId();
			if (fromLinkedRecordId.equals(toLinkedRecordId)) {
				return Optional.of(toChildReference);
			}
		}

		// TODO: harder, read and see if nameInData and attributes match

		return Optional.empty();
	}

	private ClientDataRecordGroup getFromMetadataRecordGroupFromServer(String idToAddStuffFrom) {
		ClientDataRecordGroup addFrom = dataClient.read("metadata", idToAddStuffFrom)
				.getDataRecordGroup();
		return addFrom;
	}

	private ClientDataRecordGroup getToMetadataRecordGroupFromHolderOrServer(
			String idToAddStuffTo) {
		ClientDataRecordGroup addTo = null;
		if (metadataRecordGroupToStore.containsKey(idToAddStuffTo)) {
			addTo = metadataRecordGroupToStore.get(idToAddStuffTo);
		} else {
			addTo = dataClient.read("metadata", idToAddStuffTo).getDataRecordGroup();

		}
		return addTo;
	}

	private List<ClientDataGroup> getChildRefrencesList(ClientDataParent definitionForUpdate) {
		ClientDataGroup childReferencesGroup = definitionForUpdate
				.getFirstChildOfTypeAndName(ClientDataGroup.class, "childReferences");
		return childReferencesGroup.getAllGroupsWithNameInData("childReference");
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
