package controllers;

import entities.Customer;
import exceptions.InvalidConstraintException;
import services.CustomerService;

import javax.ejb.Stateful;
import javax.inject.Inject;

@Stateful
public class VisitorSessionBean implements VisitorBeanRemote {
    @Inject
    CustomerService customerService;

    @Override
    public Customer register(String firstName,
                             String lastName,
                             String email,
                             String password,
                             String phoneNumber,
                             String address) throws InvalidConstraintException {
        return this.customerService.create(firstName, lastName, email, password, phoneNumber, address);
    }
}
