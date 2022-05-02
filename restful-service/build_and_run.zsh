#!/bin/zsh
mvn clean package
java -jar target/restful-service-0.0.1-SNAPSHOT.jar
