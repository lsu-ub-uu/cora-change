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