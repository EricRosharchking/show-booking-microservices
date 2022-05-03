# show-booking-microservices

### Assumptions

1. After removing, seats cannot be added back except from adding new rows
2. Run the jar file with "admin" or "buyer" to access different users' functionalities, there is no need to register or init users

## Use Case

Build a simple application for the use case of book a show.

Build a command CLI to initialize create number of buyers and allow each of them to select 1 or more available seats and buy tickets.  
 
#### The application shall cater to the below 2 types of users & their requirements;

Admin – The users should be able to create, update, view the list of shows and seat allocations.
Commands to be implemented for Admin :
1. Setup  <Show Number> <Number of Rows> <Number of seats per row>  <Cancellation window in minutes>   (To setup the number of seats per show)
2. View <Show Number>     (Display Show Number, Ticket#, Buyer Phone#, Seat Numbers allocated to the buyer)
3. Remove <Show Number> <count of seats to be reduced>   (Reduce non-utilized seats for a show. See restriction/rule in next section).
4. Add <Show Number> <number of rows to be added>  (Add seat capacity for a show. See restriction/rule in next section)
 
Buyers – The users should be able retrieve list of available seats for a show, select 1 or more seats , buy and cancel tickets. 
Commands to be implemented for Buyers :
1. Availability  <Show Number>    (List all available seat numbers for a show. E.g A1, F4 etc)
2. Book  <Show Number> <Phone#> <Comma separated list of seats>  (This must generate a unique ticket # and display)
3. Cancel <Show Number>  <Phone#>  <Ticket#>  (See restriction in the section below)
                     
## Points to note:

Assume max seats per row is 10 and max rows are 26. Example seat number A1,  H5 etc. The “Add” command for admin must ensure rows cannot be added beyond the upper limit of 26.

After booking, User can cancel the seats within a time window of 2 minutes (configurable).   Cancellation after that is not allowed.

Only one booking per phone# is allowed per show.

The “Remove” command for admin can block out non-sequential seats if unutilized. Show error if number of seats unutilized is less than seats to be removed.

Just make an assumption if anything is not mentioned here. The same can be highlighted in the readme notes when submitting

## Requirements
Implement the solution as a Spring Boot Microservice (Java 8+ as backend services). The data shall be in-memory.  

Implement the above use case considering object oriented principles and development best practices. The implementation should be a tested working executable. 

The project codes to be upload to Github and shared back to us for offline review by Wednesday, 4 May 2022.