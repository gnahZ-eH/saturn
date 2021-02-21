# saturn

[![codecov](https://codecov.io/gh/gnahZ-eH/saturn/branch/master/graph/badge.svg)](https://codecov.io/gh/gnahZ-eH/saturn)

Emoji |          |
------|----------|
🐞    | Bug Fix  |
🛠️    | Optimize |
🧩    | Develop  |
❌    | Delete   |
🧪    | Test     |
🍒    | Merge    |
📜    | README   |
📑    | Config   |
 

### Need to be improved
CsdlParameter in getFunction could be collection


### tips
- should use entity type as main type, not use entity set 
    - entityOperationMap.get(edmEntityType.getName());

### todo
- need to implement other functions in EntityProcessor.readEntityCollection();
- should have OdataEnum interface or not;

class           |      |
----------------|------|
EntityProcessor | todo |
PrimitiveProcessor| todo |
ExpressionVisitor.visitBinaryOperator|todo|

datasource -> entityOperation
^
|
service
^
|
repository

don't need the service
just has repo -> entityOperation


### Request Example

#### Metadata
```
{
	"$Version": "4.01",
	"com.github.saturn.example": {
		"Student": {
			"$Kind": "EntityType",
			"$Key": ["Id"],
			"Id": {
				"$Type": "Edm.Int64"
			},
			"Name": {
				"$Type": "Edm.String"
			},
			"Age": {
				"$Type": "Edm.Int32"
			}
		},
		"SaturnExample": {
			"$Kind": "EntityContainer",
			"Students": {
				"$Kind": "EntitySet",
				"$Type": ".Student"
			}
		}
	}
}
```
#### JSON format
- Read collection
  - http://localhost:8080/saturn-odata/Students?$format=json
- Read Entity By Key
  - http://localhost:8080/saturn-odata/Students(Id=1)?$format=json
- Read some fields
  - http://localhost:8080/saturn-odata/Students?$format=json&$select=Age

#### XML format
http://localhost:8080/saturn-odata/Students