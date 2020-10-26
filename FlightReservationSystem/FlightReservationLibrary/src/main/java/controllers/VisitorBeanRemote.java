package controllers;

import entities.Customer;
import exceptions.InvalidConstraintException;

import javax.ejb.Remote;

@Remote
public interface VisitorBeanRemote {
    Customer register(String firstName,
                      String lastName,
                      String email,
                      String password,
                      String phoneNumber,
                      String address) throws InvalidConstraintException;
}
