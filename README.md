# National University of Singapore IS2103 Pair Project

Java EE application using (Glassfish + MySQL).

## Instructions

 0. Make sure you have Glassfish and MySQL server setup. (See `jta-data-source` in FlightReservation-ejb's `persistence.xml`)
 1. Change directory to FlightReservationSystem using `cd FlightReservationSystem`
 2. Set `GLASSFISH_HOME` in `deploy.sh` to your Glassfish home directory.
 3. Run `deploy.sh` file to build and deploy the EJB module.

### Run FlightManagementClient

Command: `./run-management.sh`

### Run FlightReservationClient

TODO
Command: `./run-reservation.sh`