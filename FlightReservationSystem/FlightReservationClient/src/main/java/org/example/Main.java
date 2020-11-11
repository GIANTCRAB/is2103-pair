package org.example;

import controllers.CustomerBeanRemote;
import controllers.VisitorBeanRemote;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Main {
    public static void main(String[] a) throws NamingException {
        final InitialContext ic = new InitialContext();
        final VisitorBeanRemote visitorBeanRemote = (VisitorBeanRemote) ic.lookup(VisitorBeanRemote.class.getName());
        final CustomerBeanRemote customerBeanRemote = (CustomerBeanRemote) ic.lookup(CustomerBeanRemote.class.getName());

        final SystemClient reservationClient = new ReservationClient(visitorBeanRemote, customerBeanRemote);

        reservationClient.runApp();
    }
}
