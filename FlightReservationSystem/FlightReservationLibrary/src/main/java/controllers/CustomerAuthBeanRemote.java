package controllers;

import entities.Customer;
import exceptions.IncorrectCredentialsException;

import javax.ejb.Remote;

@Remote
public interface CustomerAuthBeanRemote {
    Customer login(String email, String password) throws IncorrectCredentialsException;
}
