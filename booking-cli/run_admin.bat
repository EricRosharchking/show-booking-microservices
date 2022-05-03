setlocal EnableDelayedExpansion
if "%1"=="dev" (
	!call java -jar target/booking-cli-0.0.1-SNAPSHOT.jar admin!
) else (
	!call java -jar -Dspring.profiles.active=production target/booking-cli-0.0.1-SNAPSHOT.jar admin!
)