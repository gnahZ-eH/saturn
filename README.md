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