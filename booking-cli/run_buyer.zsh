#!/bin/zsh
if [[ $1 = "dev" ]]
then
	java -jar target/booking-cli-0.0.1-SNAPSHOT.jar buyer
else
	java -jar -Dspring.profiles.active=production target/booking-cli-0.0.1-SNAPSHOT.jar buyer
fi
