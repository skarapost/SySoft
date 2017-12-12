# SySoft

## Generic Description

SySoft is a convenient interface for storing every kind of information. The information is stored in a Sqlite provided database. The user can create, rename or delete fields. Search function is supported, as well.

## Installation/Execution

1) Build the project using maven.
2) Search out ```sysoft.jar``` file in the ```target``` directory.
3) Execute it keeping ```Records.sqlite``` file from the ```resources``` directory in the same path as ```sysoft.jar```.

## Functions

SySoft can store every kind of information. In the resources directory there is a database with an already constructed database with a table, as well. Sqlite database contains 4 fields that cannot be deleted. An id field which is not visible, and other 3 fields. On top of this, the most significant operations are:
1) Create a new field.
2) Delete a field.
3) Rename a field.
4) Extraction of a record to QR code.
5) Update a record.
6) Delete a record.
7) Create a record.
8) Search using 2 different selected fields.
9) Show all the records.
