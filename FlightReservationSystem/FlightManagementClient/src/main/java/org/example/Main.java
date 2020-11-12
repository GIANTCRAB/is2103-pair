package org.example;

import controllers.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Main {
    public static void main(String[] a) throws NamingException {
        final InitialContext ic = new InitialContext();
        final FlightRouteBeanRemote flightRouteBeanRemote = (FlightRouteBeanRemote) ic.lookup(FlightRouteBeanRemote.class.getName());
        final AircraftConfigurationBeanRemote aircraftConfigurationBeanRemote = (AircraftConfigurationBeanRemote) ic.lookup(AircraftConfigurationBeanRemote.class.getName());
        final FareBeanRemote fareBeanRemote = (FareBeanRemote) ic.lookup(FareBeanRemote.class.getName());
        final FlightBeanRemote flightBeanRemote = (FlightBeanRemote) ic.lookup(FlightBeanRemote.class.getName());
        final FlightSchedulePlanBeanRemote flightSchedulePlanBeanRemote = (FlightSchedulePlanBeanRemote) ic.lookup(FlightSchedulePlanBeanRemote.class.getName());
        final SalesManagerBeanRemote salesManagerBeanRemote = (SalesManagerBeanRemote) ic.lookup(SalesManagerBeanRemote.class.getName());
        
        final ManagementClient managementClient = new ManagementClient(flightRouteBeanRemote, aircraftConfigurationBeanRemote, fareBeanRemote, flightBeanRemote, flightSchedulePlanBeanRemote, salesManagerBeanRemote);

        managementClient.runApp();
    }
}
