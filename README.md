# QueryProcessing
Entity beans fetching, joining, filtering, sorting and aggregation

Java 15, Eclipse 2020-12(4.18.0), MySQL 8.0.23

Start application by running main method in start.Runner class.
MySQL JDBC driver mysql-connector-java-8.0.23.jar should be added to classpath.
File local.properties looks like
uri=jdbc:mysql://localhost:3306/mydb?serverTimezone=UTC
user=user
password=password
schema=mydb

Project artefacts:
1. sample log of execution: QueryProcessing-log
2. MySQL DB model: QueryProcessing-model

Known drawbacks/TODOs
1. Cover classes with unit tests instead of manual testing
2. Add user interface instead of fixed use case scenario.
