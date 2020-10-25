package org.example;

import controllers.EmployeeAuthBeanRemote;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Main {
    public static void main(String[] a) throws NamingException {
        final InitialContext ic = new InitialContext();
        final EmployeeAuthBeanRemote employeeAuthBeanRemote = (EmployeeAuthBeanRemote) ic.lookup(EmployeeAuthBeanRemote.class.getName());

        final ManagementClient managementClient = new ManagementClient(employeeAuthBeanRemote);

        managementClient.runApp();
    }
}
