#!/bin/zsh
mvn clean package
if [[ $1 = "dev" ]]
then
	java -jar -Dspring.profiles.active=production target/booking-cli-0.0.1-SNAPSHOT.jar admin
else
	java -jar target/booking-cli-0.0.1-SNAPSHOT.jar admin
fi
