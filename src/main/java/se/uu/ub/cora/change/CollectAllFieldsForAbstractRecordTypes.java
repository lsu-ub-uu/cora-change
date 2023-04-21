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
import se.uu.ub.cora.javaclient.cora.DataClient;
import se.uu.ub.cora.javaclient.cora.DataClientFactoryImp;

public class CollectAllFieldsForAbstractRecordTypes {

	private DataClientFactoryImp dataClientFactory;
	private DataClient dataClient;
	Map<String, List<String>> mapWithListOfGroupIds = new HashMap<>();
	Map<String, ClientDataRecordGroup> metadataRecordGroupToStore = new HashMap<>();
	private Map<String, String> mapWithRecordTypeGroupId = new HashMap<>();

	public CollectAllFieldsForAbstractRecordTypes(String apptokenUrl, String baseUrl) {
		dataClientFactory = DataClientFactoryImp.usingAppTokenVerifierUrlAndBaseUrl(apptokenUrl,
				baseUrl);
		dataClient = dataClientFactory.factorUsingUserIdAndAppToken("141414",
				"63e6bd34-02a1-4c82-8001-158c104cae0e");
	}

	public void collectAndStoreGroupForAbstract() {
		collectAbstractRecordTypes();
		collectRecordTypesRelatedToAbstractRecordTypes();
		System.out.println("mapWithListOfGroupIds" + mapWithListOfGroupIds);

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
			System.out.println();
			System.out.println("Collecting fileds for: " + idToAddStuffTo);
			System.out.println("======================================================");

			ClientDataRecordGroup addToGroupWithGroup = null;
			for (String idToAddStuffFrom : idsToAddStuffFrom) {
				System.out.println();
				System.out.println("Matching with: " + idToAddStuffFrom);
				System.out.println("------------------------------------------------------");

				addToGroupWithGroup = addToGroupWithGroup(idToAddStuffTo, idToAddStuffFrom);

			}
			/**
			 * Just for write out latest addToGroupWithGroup
			 */

			// ClientDataToJsonConverterFactory converterFactory = ClientDataToJsonConverterProvider
			// .createImplementingFactory();
			// ClientDataToJsonConverter converter = converterFactory
			// .factorUsingConvertible(addToGroupWithGroup);
			// String json = converter.toJson();
			//
			// System.out.println(json);
			// System.out.println();
		}
		System.out.println();
		System.out.println("======================================================");
		System.out.println();
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
		List<ClientDataGroup> fromChildReferences = getChildRefrencesList(fromRecordGroup);

		for (ClientDataGroup fromChildReference : fromChildReferences) {

			Optional<ClientDataGroup> oToChildReference = getMatchingChildReferenceOnId(
					toChildReferences, fromChildReference);
			if (oToChildReference.isPresent()) {
				ClientDataGroup toChildReference = oToChildReference.get();
				System.out.print("Match on linkedId: ");
				// fix min max constraints

				// on id, check min max etc
				// sysout if they don't match
				sysOutReferenceInfo(toChildReference, fromChildReference);

			} else {
				Optional<ClientDataGroup> oToChildReference2 = getMatchingChildReferenceOnNameAttrib(
						toChildReferences, fromChildReference);
				if (oToChildReference2.isPresent()) {
					System.out.print("Match on name attrib: ");
					ClientDataGroup toChildReference = oToChildReference2.get();
					sysOutReferenceInfo(toChildReference, fromChildReference);

					String fromLinkedRecordId = getRecordLinkIdFromChildReference(
							fromChildReference);
					ClientDataRecordGroup fromFieldRecordGroup = getFromMetadataRecordGroupFromServer(
							fromLinkedRecordId);

					if (fromFieldRecordGroup.getAttribute("type").getValue().equals("group")) {
						System.out.println("<RECURSIVE>");
						addToGroupWithGroup(getRecordLinkIdFromChildReference(toChildReference),
								fromLinkedRecordId);
						System.out.println("</RECURSIVE>");
					}

					// on name+attribs, check min max etc + barnen
					// Always sysout
					// olika fall
					// grupp, rekursiv
					// atomic, skriv ut regexp för manuell kontroll
					// lista, kolla collection (möjliga val)
				} else {
					// Create a new instance of the linked metadata
					System.out.println(
							"Adding: " + getRecordLinkIdFromChildReference(fromChildReference));
					int size = toChildReferences.getChildren().size();
					fromChildReference.setRepeatId(Integer.toString(size + 1));
					toChildReferences.addChild(fromChildReference);
				}
			}
		}
		return toRecordGroup;

	}

	private void sysOutReferenceInfo(ClientDataGroup toChildReference,
			ClientDataGroup fromChildReference) {

		System.out.println(getRecordLinkIdFromChildReference(toChildReference) + " : "
				+ getRecordLinkIdFromChildReference(fromChildReference));
	}

	private Optional<ClientDataGroup> getMatchingChildReferenceOnId(
			ClientDataGroup toChildReferences, ClientDataGroup fromChildReference) {
		String fromLinkedRecordId = getRecordLinkIdFromChildReference(fromChildReference);

		// simple, if same linked id, just return it
		for (ClientDataGroup toChildReference : toChildReferences
				.getAllGroupsWithNameInData("childReference")) {
			String toLinkedRecordId = getRecordLinkIdFromChildReference(toChildReference);
			if (fromLinkedRecordId.equals(toLinkedRecordId)) {
				return Optional.of(toChildReference);
			}
		}

		return Optional.empty();
	}

	private String getRecordLinkIdFromChildReference(ClientDataGroup fromChildReference) {
		ClientDataRecordLink fromLink = fromChildReference
				.getFirstChildOfTypeAndName(ClientDataRecordLink.class, "ref");
		return fromLink.getLinkedRecordId();
	}

	private Optional<ClientDataGroup> getMatchingChildReferenceOnNameAttrib(
			ClientDataGroup toChildReferences, ClientDataGroup fromChildReference) {
		// TODO: harder, read and see if nameInData and attributes match
		String fromLinkedRecordId = getRecordLinkIdFromChildReference(fromChildReference);
		ClientDataRecordGroup fromFieldRecordGroup = getFromMetadataRecordGroupFromServer(
				fromLinkedRecordId);

		for (ClientDataGroup toChildReference : toChildReferences
				.getAllGroupsWithNameInData("childReference")) {
			String toLinkedRecordId = getRecordLinkIdFromChildReference(toChildReference);
			ClientDataRecordGroup toFieldRecordGroup = getToMetadataRecordGroupFromHolderOrServer(
					toLinkedRecordId);

			if (matchNameInDataAndAttributes(toFieldRecordGroup, fromFieldRecordGroup)) {
				return Optional.of(toChildReference);
			}
		}
		return Optional.empty();
	}

	private boolean matchNameInDataAndAttributes(ClientDataRecordGroup toFieldRecordGroup,
			ClientDataRecordGroup fromFieldRecordGroup) {

		return sameNameInData(toFieldRecordGroup, fromFieldRecordGroup)
				&& sameAttributes(toFieldRecordGroup, fromFieldRecordGroup);
	}

	private boolean sameAttributes(ClientDataRecordGroup toFieldRecordGroup,
			ClientDataRecordGroup fromFieldRecordGroup) {

		return compareFinalValues(toFieldRecordGroup, fromFieldRecordGroup);
	}

	private boolean compareFinalValues(ClientDataRecordGroup toMetadataGroup,
			ClientDataRecordGroup fromMetadataGroup) {

		List<Attribute> toAttributesFinalValues = getFinalValue(toMetadataGroup);
		List<Attribute> fromAttributesFinalValues = getFinalValue(fromMetadataGroup);
		// Write if both recordGroups ha no attributeReferences
		if (!toMetadataGroup.containsChildWithNameInData("attributeReferences")
				&& !fromMetadataGroup.containsChildWithNameInData("attributeReferences")) {
			System.out.println("(!) No attributeReferences " + toMetadataGroup.getId() + " "
					+ fromMetadataGroup.getId());
		}
		return toAttributesFinalValues.containsAll(fromAttributesFinalValues)
				&& fromAttributesFinalValues.containsAll(toAttributesFinalValues);
	}

	private List<Attribute> getFinalValue(ClientDataRecordGroup metadataGroup) {
		List<Attribute> attributeReferencesList = new ArrayList<>();
		if (metadataGroup.containsChildWithNameInData("attributeReferences")) {
			ClientDataGroup attributeReferences = metadataGroup
					.getFirstGroupWithNameInData("attributeReferences");
			List<ClientDataRecordLink> refs = attributeReferences
					.getChildrenOfTypeAndName(ClientDataRecordLink.class, "ref");

			for (ClientDataRecordLink ref : refs) {
				ClientDataRecordGroup metadataCollectionVariable = getFromMetadataRecordGroupFromServer(
						ref.getLinkedRecordId());

				String attKey = metadataCollectionVariable
						.getFirstAtomicValueWithNameInData("nameInData");
				String attlValue = metadataCollectionVariable
						.getFirstAtomicValueWithNameInData("finalValue");

				Attribute attribute = new Attribute(attKey, attlValue);
				attributeReferencesList.add(attribute);

			}
		}
		return attributeReferencesList;
	}

	private boolean sameNameInData(ClientDataRecordGroup toFieldRecordGroup,
			ClientDataRecordGroup fromFieldRecordGroup) {
		return fromFieldRecordGroup.getFirstAtomicValueWithNameInData("nameInData")
				.equals(toFieldRecordGroup.getFirstAtomicValueWithNameInData("nameInData"));
	}

	private ClientDataRecordGroup getFromMetadataRecordGroupFromServer(String idToAddStuffFrom) {
		return dataClient.read("metadata", idToAddStuffFrom).getDataRecordGroup();
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
				String addToDataGroupId = getDefinitionIdForUpdate(recordTypeRecordGroup);

				mapWithRecordTypeGroupId.put(id, addToDataGroupId);
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

			String validatesRecordType = getValidatesRecordType(recordTypeRecordGroup);
			if (isRecordImplementing(recordTypeRecordGroup)
					&& isRelatedToAbstract(validatesRecordType)) {

				String abstractGroupId = mapWithRecordTypeGroupId.get(validatesRecordType);
				List<String> list = mapWithListOfGroupIds.get(abstractGroupId);

				String addToDataGroupId = getDefinitionIdForUpdate(recordTypeRecordGroup);
				list.add(addToDataGroupId);
			}
		}
	}

	private boolean isRecordImplementing(ClientDataRecordGroup recordTypeRecordGroup) {
		return !isRecordTypeAbstract(recordTypeRecordGroup);
	}

	private boolean isRelatedToAbstract(String recordTypeId) {
		return mapWithRecordTypeGroupId.containsKey(recordTypeId);
	}

	private boolean isRecordTypeAbstract(ClientDataRecordGroup recordTypeRecordGroup) {
		return "true".equals(recordTypeRecordGroup.getFirstAtomicValueWithNameInData("abstract"));
	}

	private String getValidatesRecordType(ClientDataRecordGroup recordTypeRecordGroup) {
		if (hasParent(recordTypeRecordGroup)) {
			return getValidatesRecordType(readParentLink(recordTypeRecordGroup));
		}
		return recordTypeRecordGroup.getId();
	}

	private ClientDataRecordGroup readParentLink(ClientDataRecordGroup recordTypeRecordGroup) {
		ClientDataRecordLink parentLink = (ClientDataRecordLink) recordTypeRecordGroup
				.getFirstChildWithNameInData("parentId");
		ClientDataRecord parentRecord = dataClient.read(parentLink.getLinkedRecordType(),
				parentLink.getLinkedRecordId());
		return parentRecord.getDataRecordGroup();
	}

	private record Attribute(String key, String value) {
	}

}
