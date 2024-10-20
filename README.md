# File-Database-Service

Simple File-Based One Table Database with Java Spring Boot and Redis Integration
This Spring Boot application allows users to create and manipulate a simple file-based database with one table.
The app parses SQL-like statements (CREATE TABLE and INSERT INTO) and stores metadata and data into text files. 
It also interacts with Redis to track the number of successful and failed operations.Supported data types for columns are integers and strings.

# Features:
REST Endpoints to accept SQL-like queries.
File-based Table Storage to create table metadata and store table rows.
Redis Integration to log the number of successful (SUCCESS) and unsuccessful (FAILURE) operations.

# Following Enpoints Have Been Created
1. Create(/api/create) - This endpoint is used for creating table. SQL query for creating table can be sent in the body of the request. The application parses the query and inserts the data in the metadat.txt file.
2. Insert(/api/insert) - This endpoint is used for inserting the data in the created tables. SQL query for inserting into table can be sent in the body of the request. The application parses the query and inserts      the data in the tabledata.txt file.
3. Get Data(/api/getdata) - This get endpoint returns all the inserted data from tabledata.txt file in a structured format.
4. Get Success and Failure counts(/api/counters) - This get endpoint returns the number of successfull and unsuccessfull requests that are stored in cache using redis.
