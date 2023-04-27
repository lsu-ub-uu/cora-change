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

	private static final String RECORD_PART_CONSTRAINT = "recordPartConstraint";
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
			// resetChildReferences(addToRecordGroup);

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
				checkPropertiesAndWriteOutIfNotMatch(fromChildReference, toChildReference);
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
					checkPropertiesAndWriteOutIfNotMatch(fromChildReference, toChildReference);

					String fromLinkedRecordId = getRecordLinkIdFromChildReference("ref",
							fromChildReference);
					ClientDataRecordGroup fromFieldRecordGroup = getFromMetadataRecordGroupFromServer(
							fromLinkedRecordId);

					if (fromFieldRecordGroup.getAttribute("type").getValue().equals("group")) {
						System.out.println("<RECURSIVE>");
						addToGroupWithGroup(
								getRecordLinkIdFromChildReference("ref", toChildReference),
								fromLinkedRecordId);
						System.out.println("</RECURSIVE>");
					}
					if (fromFieldRecordGroup.getAttribute("type").getValue()
							.equals("textVariable")) {

						String toLinkedRecordId = getRecordLinkIdFromChildReference("ref",
								toChildReference);
						ClientDataRecordGroup toFieldRecordGroup = getFromMetadataRecordGroupFromServer(
								toLinkedRecordId);

						ClientDataGroup toGroup = ClientDataProvider
								.createGroupFromRecordGroup(toFieldRecordGroup);
						ClientDataGroup fromGroup = ClientDataProvider
								.createGroupFromRecordGroup(fromFieldRecordGroup);
						checkNameInDataAndWriteOut("regEx", toGroup, fromGroup);
					}
					if (fromFieldRecordGroup.getAttribute("type").getValue()
							.equals("numberVariable")) {
						System.out.println("Comparing number variables: ");

						String toLinkedRecordId = getRecordLinkIdFromChildReference("ref",
								toChildReference);
						ClientDataRecordGroup toFieldRecordGroup = getFromMetadataRecordGroupFromServer(
								toLinkedRecordId);

						ClientDataGroup toGroup = ClientDataProvider
								.createGroupFromRecordGroup(toFieldRecordGroup);
						ClientDataGroup fromGroup = ClientDataProvider
								.createGroupFromRecordGroup(fromFieldRecordGroup);
						checkNameInDataAndWriteOut("min", toGroup, fromGroup);
						checkNameInDataAndWriteOut("max", toGroup, fromGroup);
						checkNameInDataAndWriteOut("warningMin", toGroup, fromGroup);
						checkNameInDataAndWriteOut("warningMax", toGroup, fromGroup);
						checkNameInDataAndWriteOut("numberOfDecimals", toGroup, fromGroup);
					}
					if (fromFieldRecordGroup.getAttribute("type").getValue().equals("recordLink")) {
						System.out.print("Comparing recordLink variables: ");

						String toLinkedRecordId = getRecordLinkIdFromChildReference("ref",
								toChildReference);
						ClientDataRecordGroup toFieldRecordGroup = getFromMetadataRecordGroupFromServer(
								toLinkedRecordId);

						// linkedRecordTypeLink

						ClientDataGroup toGroup = ClientDataProvider
								.createGroupFromRecordGroup(toFieldRecordGroup);
						ClientDataGroup fromGroup = ClientDataProvider
								.createGroupFromRecordGroup(fromFieldRecordGroup);

						String to = "NOT FOUND";
						String from = "NOT FOUND";

						if (fromGroup.containsChildOfTypeAndName(ClientDataRecordLink.class,
								"linkedRecordType")) {
							// if (fromGroup.containsChildWithNameInData("linkedRecordType")) {
							// from =
							// fromGroup.getFirstAtomicValueWithNameInData("linkedRecordType");
							from = fromGroup.getFirstChildOfTypeAndName(ClientDataRecordLink.class,
									"linkedRecordType").getLinkedRecordId();
						}
						if (toGroup.containsChildOfTypeAndName(ClientDataRecordLink.class,
								"linkedRecordType")) {
							// C if (toGroup.containsChildWithNameInData("linkedRecordType")) {
							// to = toGroup.getFirstAtomicValueWithNameInData("linkedRecordType");
							to = toGroup.getFirstChildOfTypeAndName(ClientDataRecordLink.class,
									"linkedRecordType").getLinkedRecordId();
						}
						if (!to.equals(from)) {
							System.out.print("(" + "linkedRecordType" + " not matching. to: " + to
									+ " from:" + from + ") ");
						}
					}
					if (fromFieldRecordGroup.getAttribute("type").getValue()
							.equals("collectionVariable")) {
						System.out.println("Comparing list variables: ");

						boolean compareCollectionVariableValues = compareCollectionVariableValues(
								toChildReference, fromChildReference);
						if (compareCollectionVariableValues) {
							System.out.println("List variables are different");
						} else {
							System.out.println("List variables are the same");
						}

					}
					sysOutReferenceInfo(toChildReference, fromChildReference);

					// on name+attribs, check min max etc + barnen
					// Always sysout
					// olika fall
					// grupp, rekursiv
					// atomic, skriv ut regexp för manuell kontroll
					// lista, kolla collection (möjliga val)
				} else {
					// Create a new instance of the linked metadata
					System.out.println("Adding: "
							+ getRecordLinkIdFromChildReference("ref", fromChildReference));
					int size = toChildReferences.getChildren().size();
					fromChildReference.setRepeatId(Integer.toString(size + 1));
					toChildReferences.addChild(fromChildReference);
				}
			}
		}
		return toRecordGroup;

	}

	private void checkNameInDataAndWriteOut(String nameInData, ClientDataGroup toGroup,
			ClientDataGroup fromGroup) {
		String to = "";
		String from = "";
		if (fromGroup.containsChildWithNameInData(nameInData)) {
			from = fromGroup.getFirstAtomicValueWithNameInData(nameInData);
		}
		if (toGroup.containsChildWithNameInData(nameInData)) {
			to = toGroup.getFirstAtomicValueWithNameInData(nameInData);
		}
		if (!to.equals(from)) {
			System.out
					.print("(" + nameInData + " not matching. to: " + to + " from:" + from + ") ");
		}
	}

	private void checkPropertiesAndWriteOutIfNotMatch(ClientDataGroup fromChildReference,
			ClientDataGroup toChildReference) {
		checkCardinalityAndWriteOut(toChildReference, fromChildReference);
		checkChildRefCollectTermOfTypeWriteOut("index", toChildReference, fromChildReference);
		checkChildRefCollectTermOfTypeWriteOut("permission", toChildReference, fromChildReference);
		checkChildRefCollectTermOfTypeWriteOut("storage", toChildReference, fromChildReference);
		checkNameInDataAndWriteOut(RECORD_PART_CONSTRAINT, toChildReference, fromChildReference);
		// checkChildRecordPartConstraintsAndWriteOut(toChildReference, fromChildReference);

	}

	// sysOutReferenceInfo(toChildReference, fromChildReference);

	private void checkCardinalityAndWriteOut(ClientDataGroup toChildReference,
			ClientDataGroup fromChildReference) {
		int toMin = valueOfRepeat(toChildReference.getFirstAtomicValueWithNameInData("repeatMin"));
		int fromMin = valueOfRepeat(
				fromChildReference.getFirstAtomicValueWithNameInData("repeatMin"));
		if (toMin > fromMin) {
			System.out.print(
					"(Cardinality Min not matching to: " + toMin + " > from: " + fromMin + ") ");
		}

		int toMax = valueOfRepeat(toChildReference.getFirstAtomicValueWithNameInData("repeatMax"));
		int fromMax = valueOfRepeat(
				fromChildReference.getFirstAtomicValueWithNameInData("repeatMax"));
		if (toMax < fromMax) {
			System.out.print(
					"(Cardinality Max not matching to: " + toMax + " < from: " + fromMax + " ) ");
		}

	}

	private int valueOfRepeat(String sToMax) {
		int toMax = 0;
		if ("X".equals(sToMax.toUpperCase())) {
			toMax = Integer.MAX_VALUE;
		} else {
			toMax = Integer.parseInt(sToMax);
		}
		return toMax;
	}

	private void checkChildRefCollectTermOfTypeWriteOut(String type,
			ClientDataGroup toChildReference, ClientDataGroup fromChildReference) {
		List<String> toCollectTermLinkIds = getCollectTermLinkIds(type, toChildReference);
		List<String> fromCollectTermLinkIds = getCollectTermLinkIds(type, fromChildReference);

		if (!(toCollectTermLinkIds.containsAll(fromCollectTermLinkIds)
				&& fromCollectTermLinkIds.containsAll(toCollectTermLinkIds))) {
			System.out.print("(" + type + " not matching. to: " + toCollectTermLinkIds + " from:"
					+ fromCollectTermLinkIds + ") ");
		}
	}

	private List<String> getCollectTermLinkIds(String type, ClientDataGroup fromChildReference) {
		List<String> collectTermLinkIds = new ArrayList<>();

		if (fromChildReference.containsChildWithNameInData("childRefCollectTerm")) {
			List<ClientDataRecordLink> collectTerms = fromChildReference
					.getChildrenOfTypeAndName(ClientDataRecordLink.class, "childRefCollectTerm");

			for (ClientDataRecordLink collectTerm : collectTerms) {
				Optional<String> attributeValue = collectTerm.getAttributeValue("type");
				if (attributeValue.isPresent() && attributeValue.get().equals(type)) {
					collectTermLinkIds.add(collectTerm.getLinkedRecordId());
				}
			}
		}
		return collectTermLinkIds;
	}

	private void sysOutReferenceInfo(ClientDataGroup toChildReference,
			ClientDataGroup fromChildReference) {

		System.out.println(getRecordLinkIdFromChildReference("ref", toChildReference) + " : "
				+ getRecordLinkIdFromChildReference("ref", fromChildReference));
	}

	private Optional<ClientDataGroup> getMatchingChildReferenceOnId(
			ClientDataGroup toChildReferences, ClientDataGroup fromChildReference) {
		String fromLinkedRecordId = getRecordLinkIdFromChildReference("ref", fromChildReference);

		// simple, if same linked id, just return it
		for (ClientDataGroup toChildReference : toChildReferences
				.getAllGroupsWithNameInData("childReference")) {
			String toLinkedRecordId = getRecordLinkIdFromChildReference("ref", toChildReference);
			if (fromLinkedRecordId.equals(toLinkedRecordId)) {
				return Optional.of(toChildReference);
			}
		}

		return Optional.empty();
	}

	private String getRecordLinkIdFromChildReference(String nameInData,
			ClientDataGroup fromChildReference) {
		ClientDataRecordLink fromLink = fromChildReference
				.getFirstChildOfTypeAndName(ClientDataRecordLink.class, nameInData);
		return fromLink.getLinkedRecordId();
	}

	private Optional<ClientDataGroup> getMatchingChildReferenceOnNameAttrib(
			ClientDataGroup toChildReferences, ClientDataGroup fromChildReference) {
		// TODO: harder, read and see if nameInData and attributes match
		String fromLinkedRecordId = getRecordLinkIdFromChildReference("ref", fromChildReference);
		ClientDataRecordGroup fromFieldRecordGroup = getFromMetadataRecordGroupFromServer(
				fromLinkedRecordId);

		for (ClientDataGroup toChildReference : toChildReferences
				.getAllGroupsWithNameInData("childReference")) {
			String toLinkedRecordId = getRecordLinkIdFromChildReference("ref", toChildReference);
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

		return compareAttibuteValues(toFieldRecordGroup, fromFieldRecordGroup);
	}

	private boolean compareAttibuteValues(ClientDataRecordGroup toMetadataGroup,
			ClientDataRecordGroup fromMetadataGroup) {

		List<Attribute> toAttributesValues = getAttributeValues(toMetadataGroup);
		List<Attribute> fromAttributesValues = getAttributeValues(fromMetadataGroup);

		System.out.println("From:" + fromAttributesValues);
		System.out.println("To: " + toAttributesValues);
		return attributesFromIsNarrowerThanTo(toAttributesValues, fromAttributesValues);
	}

	private boolean compareCollectionVariableValues(ClientDataGroup toMetadataGroup,
			ClientDataGroup fromMetadataGroup) {

		List<Attribute> toAttributesValues = getAttributeValuesForReference(toMetadataGroup);
		List<Attribute> fromAttributesValues = getAttributeValuesForReference(fromMetadataGroup);

		System.out.println("From:" + fromAttributesValues);
		System.out.println("To: " + toAttributesValues);
		return attributesFromIsNarrowerThanTo(toAttributesValues, fromAttributesValues);
	}

	private boolean attributesFromIsNarrowerThanTo(List<Attribute> toAttributesValues,
			List<Attribute> fromAttributesValues) {
		if (toAttributesValues.size() != fromAttributesValues.size()) {
			return false;
		}
		for (Attribute toAttribute : toAttributesValues) {
			boolean narrower = false;
			for (Attribute fromAttribute : fromAttributesValues) {
				if (toAttribute.key().equals(fromAttribute.key())) {
					if (toAttribute.values().containsAll(fromAttribute.values())) {
						// ok
						narrower = true;
					}
				}
			}
			if (!narrower) {
				return false;
			}
		}
		return true;
	}

	private List<Attribute> getAttributeValues(ClientDataRecordGroup metadataGroup) {
		if (!metadataGroup.containsChildWithNameInData("attributeReferences")) {
			return new ArrayList<>();
		}
		ClientDataGroup attributeReferences = metadataGroup
				.getFirstGroupWithNameInData("attributeReferences");
		return getAttributeValuesForReference(attributeReferences);
	}

	private List<Attribute> getAttributeValuesForReference(ClientDataGroup attributeReferences) {
		List<Attribute> attributeReferencesList = new ArrayList<>();
		List<ClientDataRecordLink> refs = attributeReferences
				.getChildrenOfTypeAndName(ClientDataRecordLink.class, "ref");

		for (ClientDataRecordLink ref : refs) {
			ClientDataRecordGroup metadataCollectionVariable = getFromMetadataRecordGroupFromServer(
					ref.getLinkedRecordId());

			String attKey = metadataCollectionVariable
					.getFirstAtomicValueWithNameInData("nameInData");

			if (metadataCollectionVariable.containsChildWithNameInData("finalValue")) {
				String attlValue = metadataCollectionVariable
						.getFirstAtomicValueWithNameInData("finalValue");
				Attribute attribute = new Attribute(attKey, List.of(attlValue));
				attributeReferencesList.add(attribute);
			} else {
				// NOT final value find possible values
				List<String> possibleValues = getPossibleValuesFromCollectionVariable(
						metadataCollectionVariable);
				Attribute attribute = new Attribute(attKey, possibleValues);
				attributeReferencesList.add(attribute);
			}
		}
		// return attributeReferencesList;
		return attributeReferencesList;
	}

	private void extracted(List<Attribute> attributeReferencesList,
			ClientDataGroup attributeReferences) {
		List<ClientDataRecordLink> refs = attributeReferences
				.getChildrenOfTypeAndName(ClientDataRecordLink.class, "ref");

		for (ClientDataRecordLink ref : refs) {
			ClientDataRecordGroup metadataCollectionVariable = getFromMetadataRecordGroupFromServer(
					ref.getLinkedRecordId());

			String attKey = metadataCollectionVariable
					.getFirstAtomicValueWithNameInData("nameInData");

			if (metadataCollectionVariable.containsChildWithNameInData("finalValue")) {
				String attlValue = metadataCollectionVariable
						.getFirstAtomicValueWithNameInData("finalValue");
				Attribute attribute = new Attribute(attKey, List.of(attlValue));
				attributeReferencesList.add(attribute);
			} else {
				// NOT final value find possible values
				List<String> possibleValues = getPossibleValuesFromCollectionVariable(
						metadataCollectionVariable);
				Attribute attribute = new Attribute(attKey, possibleValues);
				attributeReferencesList.add(attribute);
			}
		}
	}

	private List<String> getPossibleValuesFromCollectionVariable(
			ClientDataRecordGroup metadataCollectionVariable) {
		ClientDataRecordLink linkToCollection = metadataCollectionVariable
				.getFirstChildOfTypeAndName(ClientDataRecordLink.class, "refCollection");
		ClientDataRecordGroup collection = getFromMetadataRecordGroupFromServer(
				linkToCollection.getLinkedRecordId());
		ClientDataGroup itemRefrences = collection
				.getFirstGroupWithNameInData("collectionItemReferences");
		List<ClientDataRecordLink> linkToCollectionItems = itemRefrences
				.getChildrenOfTypeAndName(ClientDataRecordLink.class, "ref");
		List<String> attributeValues = new ArrayList<>();
		for (ClientDataRecordLink clientDataRecordLink : linkToCollectionItems) {
			ClientDataRecordGroup collectionItem = getFromMetadataRecordGroupFromServer(
					clientDataRecordLink.getLinkedRecordId());
			String attributeValue = collectionItem.getFirstAtomicValueWithNameInData("nameInData");
			attributeValues.add(attributeValue);
		}

		return attributeValues;
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

	private record Attribute(String key, List<String> values) {
	}

}
