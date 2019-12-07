## Description of the project
Java Database Connectivity (JDBC) provides Java developers with a standard API that is used to access
databases, regardless of the driver and database product. JDBC presents a uniform interface to databases
- change vendors and your applications only need to change their driver.
### DBMS Description
A Computer Database is a structured collection of records or data that is stored in a computer system. On the other hand, a Database Management System (DBMS) is a complex set of software programs that controls the organization, storage, management, and retrieval of data in a database. DBMS are categorized according to their data structures or types. The DBMS accepts requests for data from the application program and instructs the operating system to transfer the appropriate data.
Extensible Markup Language (XML) (encoding: ISO-8859-1) is a set of rules for encoding documents in machine readable form. It is defined in the XML 1.0 Specification produced by the W3C, and several other related specifications, all gratis open standards

### Project features:
        o Create database
        o Create table
        o Insert into table 
        o Delete from table
        o Drop database
        o Drop table
        o Select from table
        o Update table
        o ٍConditions
        o  Saving and Loading in XML files
### Bonus features:
	 o ٍSupports Strings, Integers, Date and Float data types.
	 o ٍFlexible to add new data types.
	 o ٍSupports assigning time for executing queries.
	 o ٍSupports GUI to interact with the user.
	 o Supporting select with order by up to any number of columns ascending or descending.
---
## Used Design Pattern
#### 1-Factory Design Pattern
We have used Factory design pattern to create Objects from the text representation:
‘12345’ -> Creates a String Object and stores it in the table
12345 -> Creates an Integer Object and stores it in the table
Also we have used Factory Design pattern to generate the correct Commands for the SQL queries which makes the implementation as abstract as possible

#### 2-Singleton Design Pattern

We have used Factory design in classes such as:  All Factory classes, Database Manager and  FilesHandler. Since we only need one instance of each of these classes

#### 3-Facade Design Pattern

Table, DatabaseManager and FilesHandler all use Instance of other classes inside them to do some functionalities.

#### 4-Filter Design Pattern

We have used Filter design pattern in Conditions where there is a ConditionFilter interface and all filter must implement that interface. There where 3 conditions in the project : “=”, “>”, “<”

#### 5-Commands Design Pattern

For each Command, There is a class that implements the Command interface such that every command can be simply executes as command.exec(). There factory classes which generate the correct command for a specific query

---

[For more information click here to see the report](https://docs.google.com/document/d/1uHaXtZpzcscMrKx9u2Kc0eNmUwVzhTeaYYveWppqzqw/edit?usp=sharing)

---