package controllers;

import entities.Customer;
import exceptions.IncorrectCredentialsException;
import services.AuthService;

import javax.ejb.Stateful;
import javax.inject.Inject;

@Stateful
public class CustomerAuthSessionBean implements CustomerAuthBeanRemote {
    @Inject
    AuthService authService;

    @Override
    public Customer login(String email, String password) throws IncorrectCredentialsException {
        return this.authService.customerLogin(email, password);
    }
}
