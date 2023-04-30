-- working stuff
SELECT data FROM record r 
WHERE "data" -> 'children' @> '[{"name": "abstract", "value": "true"}]' and id ='metadata';


UPDATE record 
SET data = jsonb_set(data, '{children, 1, value}', '"false"'::jsonb, false) 
WHERE data -> 'children' @> '[{"name": "abstract", "value": "true"}]' AND id = 'metadata';



-- https://dev.to/rap2hpoutre/postgresql-update-objects-deep-in-a-jsonb-structure-a-fast-and-reliable-way-25mb

SELECT question, dataIndex-1
FROM record r
CROSS JOIN jsonb_array_elements(r."data" -> 'children')
    WITH ORDINALITY arr(question, dataIndex)
WHERE question  @> '{"name": "abstract", "value": "true"}' and id = 'metadata' ;


with questions_table as (
    SELECT question, dataIndex
FROM record r
CROSS JOIN jsonb_array_elements(r."data" -> 'children')
    WITH ORDINALITY arr(question, dataIndex)
WHERE question  @> '{"name": "abstract", "value": "true"}' and id = 'metadata'
), path_table as (
select
  ('{children, '||dataIndex-1||', value}')::text[]  as path
from questions_table
)
UPDATE record  
SET data = jsonb_set(data, path, '"false"'::jsonb, false) 
from path_table
WHERE data -> 'children' @> '[{"name": "abstract", "value": "true"}]' AND id = 'metadata';


update record 
set type = 'metadata'
where type in ('metadataGroup', 'metadataCollectionVariable','metadataItemCollection','metadataNumberVariable',
'metadataRecordLink','metadataResourceLink','metadataTextVariable');

select distinct type from record;
appToken
collectIndexTerm
collectPermissionTerm
collectStorageTerm
coraText
coraUser
demo
example
genericCollectionItem
guiElementLink
indexBatchJob
loginLDAP
loginToken
loginUnit
loginWebRedirect
metadataCollectionVariable
metadataGroup
metadataItemCollection
metadataNumberVariable
metadataRecordLink
metadataResourceLink
metadataTextVariable
permissionRole
permissionRule
presentationCollectionVar
presentationGroup
presentationNumberVar
presentationRecordLink
presentationRepeatingContainer
presentationResourceLink
presentationSurroundingContainer
presentationVar
recordType
search
searchTerm
system
systemOneUser
textSystemOne
validationType

-- result of change to abstract level for Cora + SystemOne
Optional[{presentationVar=presentation, collectPermissionTerm=collectTerm, metadata=metadata, presentationSurroundingContainer=presentation, coraText=text, presentationNumberVar=presentation, sound=binary, validationType=validationType, guiElement=guiElement, loginUnit=loginUnit, systemOneUser=user, collectStorageTerm=collectTerm, presentation=presentation, loginLDAP=login, metadataRecordLink=metadata, presentationCollectionVar=presentation, searchTerm=searchTerm, systemSecret=systemSecret, text=text, workOrder=workOrder, image=binary, permissionRole=permissionRole, textSystemOne=text, appToken=appToken, loginToken=login, permissionRule=permissionRule, system=system, metadataTextVariable=metadata, binary=binary, permissionUnit=permissionUnit, validationOrder=validationOrder, genericBinary=binary, collectTerm=collectTerm, indexBatchJob=indexBatchJob, metadataGroup=metadata, presentationRecordLink=presentation, login=login, demo=demo, genericCollectionItem=metadata, metadataNumberVariable=metadata, metadataResourceLink=metadata, example=example, guiElementLink=guiElement, loginWebRedirect=login, search=search, presentationResourceLink=presentation, collectIndexTerm=collectTerm, recordType=recordType, coraUser=user, metadataCollectionItem=metadata, presentationRepeatingContainer=presentation, metadataCollectionVariable=metadata, presentationGroup=presentation, user=user, metadataItemCollection=metadata}]


Checking: indexBatchJob
Skipping same in map: indexBatchJob
Checking: presentationVar
UpdatedvalidatesRecordTypeId: presentationVar --> presentation
Checking: collectPermissionTerm
UpdatedvalidatesRecordTypeId: collectPermissionTerm --> collectTerm
Checking: presentationSurroundingContainer
UpdatedvalidatesRecordTypeId: presentationSurroundingContainer --> presentation
Checking: coraText
UpdatedvalidatesRecordTypeId: coraText --> text
Checking: presentationNumberVar
UpdatedvalidatesRecordTypeId: presentationNumberVar --> presentation
Checking: sound
UpdatedvalidatesRecordTypeId: sound --> binary
Checking: validationType
Skipping same in map: validationType
Checking: metadataGroup
UpdatedvalidatesRecordTypeId: metadataGroup --> metadata
Checking: loginUnit
Skipping same in map: loginUnit
Checking: presentationRecordLink
UpdatedvalidatesRecordTypeId: presentationRecordLink --> presentation
Checking: systemOneUser
UpdatedvalidatesRecordTypeId: systemOneUser --> user
Checking: demo
Skipping same in map: demo
Checking: genericCollectionItem
UpdatedvalidatesRecordTypeId: genericCollectionItem --> metadata
Checking: metadataNumberVariable
UpdatedvalidatesRecordTypeId: metadataNumberVariable --> metadata
Checking: metadataResourceLink
UpdatedvalidatesRecordTypeId: metadataResourceLink --> metadata
Checking: example
Skipping same in map: example
Checking: collectStorageTerm
UpdatedvalidatesRecordTypeId: collectStorageTerm --> collectTerm
Checking: guiElementLink
UpdatedvalidatesRecordTypeId: guiElementLink --> guiElement
Checking: loginWebRedirect
UpdatedvalidatesRecordTypeId: loginWebRedirect --> login
Checking: loginLDAP
UpdatedvalidatesRecordTypeId: loginLDAP --> login
Checking: metadataRecordLink
UpdatedvalidatesRecordTypeId: metadataRecordLink --> metadata
Checking: presentationCollectionVar
UpdatedvalidatesRecordTypeId: presentationCollectionVar --> presentation
Checking: search
Skipping same in map: search
Checking: presentationResourceLink
UpdatedvalidatesRecordTypeId: presentationResourceLink --> presentation
Checking: searchTerm
Skipping same in map: searchTerm
Checking: collectIndexTerm
UpdatedvalidatesRecordTypeId: collectIndexTerm --> collectTerm
Checking: systemSecret
Skipping same in map: systemSecret
Checking: workOrder
Skipping same in map: workOrder
Checking: image
UpdatedvalidatesRecordTypeId: image --> binary
Checking: permissionRole
Skipping same in map: permissionRole
Checking: textSystemOne
UpdatedvalidatesRecordTypeId: textSystemOne --> text
Checking: appToken
Skipping same in map: appToken
Checking: recordType
Skipping same in map: recordType
Checking: loginToken
UpdatedvalidatesRecordTypeId: loginToken --> login
Checking: coraUser
UpdatedvalidatesRecordTypeId: coraUser --> user
Checking: permissionRule
Skipping same in map: permissionRule
Checking: system
Skipping same in map: system
Checking: metadataTextVariable
UpdatedvalidatesRecordTypeId: metadataTextVariable --> metadata
Checking: presentationRepeatingContainer
UpdatedvalidatesRecordTypeId: presentationRepeatingContainer --> presentation
Checking: metadataCollectionVariable
UpdatedvalidatesRecordTypeId: metadataCollectionVariable --> metadata
Checking: permissionUnit
Skipping same in map: permissionUnit
Checking: validationOrder
Skipping same in map: validationOrder
Checking: genericBinary
UpdatedvalidatesRecordTypeId: genericBinary --> binary
Checking: presentationGroup
UpdatedvalidatesRecordTypeId: presentationGroup --> presentation
Checking: metadataItemCollection
UpdatedvalidatesRecordTypeId: metadataItemCollection --> metadata
PASSED: runChangeValidationTypesToAbstractLevel

===============================================
    Default test
    Tests run: 1, Failures: 0, Skips: 0
===============================================



{"name": "text", "children": [{"name": "recordInfo", "children": [{"name": "id", "value": "abstractCollectionVarDefText"}, {"name": "type", "children": [{"name": "linkedRecordType", "value": "recordType"}, {"name": "linkedRecordId", "value": "coraText"}]}, {"name": "dataDivider", "children": [{"name": "linkedRecordType", "value": "system"}, {"name": "linkedRecordId", "value": "cora"}]}, {"name": "validationType", "children": [{"name": "linkedRecordType", "value": "validationType"}, {"name": "linkedRecordId", "value": "coraText"}]}, {"name": "updated", "children": [{"name": "updatedBy", "children": [{"name": "linkedRecordType", "value": "user"}, {"name": "linkedRecordId", "value": "141414"}]}, {"name": "tsUpdated", "value": "2018-04-16T11:54:12.974000Z"}], "repeatId": "0"}, {"name": "updated", "children": [{"name": "updatedBy", "children": [{"name": "linkedRecordType", "value": "user"}, {"name": "linkedRecordId", "value": "141414"}]}, {"name": "tsUpdated", "value": "2023-03-02T15:22:34.753649Z"}], "repeatId": "1"}, {"name": "createdBy", "children": [{"name": "linkedRecordType", "value": "systemOneUser"}, {"name": "linkedRecordId", "value": "12345"}]}, {"name": "tsCreated", "value": "2018-02-21T07:48:21.832000Z"}]}, {"name": "textPart", "children": [{"name": "text", "value": "En posttyp kan vara abstrakt eller inte, är den abstrakt kan man inte mata in data för typen, utan måste då skapa barn till den abstrakta typen, som det går att mata in data för. Ett exempel på en abstrakt posttyp är auktoritetsposter som sedan har barnen person, plats, etc. Syftet med en abstrakt typ är att gruppera ihop andra posttyper som logiskt hänger ihop och kan använda en gemensam högre definition av sitt data."}], "attributes": {"lang": "sv", "type": "default"}}, {"name": "textPart", "children": [{"name": "text", "value": "A record type may be abstract or not, it is not possible to enter data for an abstract record type, one must create a child for the abstract type where one can enter data. One example of an abstract record type is authority records, with person, place, etc. as children. The purpose of an abstract record type is to group other record types into a logical unit that uses a higher common definition of their data."}], "attributes": {"lang": "en", "type": "alternative"}}]}

