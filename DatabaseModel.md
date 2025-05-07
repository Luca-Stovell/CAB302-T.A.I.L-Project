# Database management system selection
It is alleged that because of "AI integration", the database management system for the TAIL project will need to be carefully chosen. Initial suggested options include: SQL, Databricks.
## Data to be stored 
Listed below are all mentions of data that would need to be stored in the user stories: 
- US4,5(also 1,2,3): lesson plans 
- US6,7: Properties of lesson plans (target demographic, multimedia elements(?))
- US9: "Learning cards" 
- implied many times: access rights or resource availability
- implied, and US14: login credentials (username, password) and role
- US10: Responses to "lesson cards" \- marked low priority
- US12: Worksheets \- marked low priority
- US15: spaced repetition performance (activity, accuracy rate) \- similar to US10, but high priority
- US15: associated accounts, such as parent-child
- US16: student groups/difficulty classes, or, lesson difficulty classes
- US17: notification frequency/timing \- low priority (ignore)
- US19,20,21: worksheets, and properties of worksheets
- U22: class registration and appointment
- US42: worksheet answers
- US43: "flash card" review schedule
- US46: progress

misc:
- Mentioned roles: teacher, student, parent, Administrator
- Deletion mentioned in US19  
  - Rubbish bin system?
- U23: search function

<!--
Low-fidelity sketches:
- 2.0(teacher page): analytics (mentioned U10,U15)
- 3.0(review): lessons, cards, worksheet
-->

## Conclusion
None of the identified data needs anything more complicated than SQL, thus the DBMS chosen is SQLite (default choice)
# Database modelling
Foreword: Apparently, the preferred method of storing a lot of text in SQL is using a huge VARCHAR field.
## Fields, or, Assumptions
Here I will try to extract the requirements from the above list and organise them more sensibly.
- lesson plans, "Learning Cards", and worksheets
  - The format of these depends on what the AI outputs and how we process that.
    - I will assume for now these will be large blocks of text (stored in large VARCHAR or NVARCHAR).
  - Worksheets and cards should probably store the related lessonID
  - Additionally: owner, availability (separate table), various other properties, Title
- Users
  - Username (VARCHAR), password (should be fixed length CHAR), Role (ENUM, I guess), class (INT, or VARCHAR)
  - access rights tables (class and user)
  - Account association table?
 <!-- " you should be able to be in smaller classes within a class!" statements dreamt up by the utterly deranged -->
  
## Set Notation
- lessons: { <u>lessonID</u>, ownerID*, Title, }
- cards: { <u>cardID</u>, ownerID, Title, }
- worksheet: { <u>WorksheetID</u>, ownerID*, Title, }

 The preceding three could be combined into one "materials" table:
- materials: { <u>materialID</u>, ownerID, Title, materialType }
  - ownerID foreign key with userID
  - materialType is an ENUM
  - not shown: properties like creation date or difficulty level, which are optional


- lessonContent: {<u>lessonID*</u>, lessonContent }
- cardContent: { <u>cardID</u>, cardContent }
- worksheetContent: { <u>WorksheetID</u>, worksheetContent }
  - These should be kept separate


- User {<u>userID</u>, Username, Password, role}
  - username is unique


- classTable {userID, classID}
  - shared primary key
- classMaterial {classID, materialID}
- userMaterial {userID, materialID}
  - Possibly unnecessary, could use ownerID or one person classes instead

Notes:
- No mention of parent-student account association
- No analytics/ results yet, depends on implementation
  - These would probably have their own table

## SQL create table commands
Note: in SQLite, ENUM doesn't exist. Neither does varchar (makes no difference, you can still use it)


```
CREATE TABLE IF NOT EXISTS "user" (
userID INTEGER PRIMARY KEY AUTOINCREMENT, /* Possibly redundant, email is unique */
email TEXT UNIQUE,
firstName TEXT,
lastName TEXT,
password TEXT,
role INTEGER /* substitute for enum, could also use TEXT. Also TODO decide if this actually exists*/
);
```
Amendment: added first and last name, changed "userName" to "email"
```
CREATE TABLE IF NOT EXISTS "materials" (
materialID INTEGER PRIMARY KEY AUTOINCREMENT,
ownerID INTEGER,
title TEXT,
materialType INTEGER,
FOREIGN KEY (ownerID) REFERENCES user(userID)
);
```
```
CREATE TABLE IF NOT EXISTS "lessonContent" (
materialID INTEGER PRIMARY KEY,
lessonContent TEXT,
FOREIGN KEY (materialID) REFERENCES materials(materialID)
);
```
```
CREATE TABLE IF NOT EXISTS "cardContent" (
materialID INTEGER PRIMARY KEY,
cardContent TEXT,
FOREIGN KEY (materialID) REFERENCES materials(materialID)
);
```
```
CREATE TABLE IF NOT EXISTS "worksheetContent" (
materialID INTEGER PRIMARY KEY,
worksheetContent TEXT,
FOREIGN KEY (materialID) REFERENCES materials(materialID)
);
```
```
CREATE TABLE IF NOT EXISTS "classTable" (
userID INTEGER,
classID INTEGER,
PRIMARY KEY (userID, classID),
FOREIGN KEY (userID) REFERENCES user(userID)
);
```
```
CREATE TABLE IF NOT EXISTS "classMaterial" (
materialID INTEGER,
classID INTEGER,
PRIMARY KEY (materialID, classID),
FOREIGN KEY (materialID) REFERENCES materials(materialID),
FOREIGN KEY (classID) REFERENCES classTable(classID)
);
```