package services;

import entities.Customer;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

@LocalBean
@Stateless
public class CustomerService {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;
    @Inject
    private Pbkdf2PasswordHash passwordHash;

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    public Customer findById(Long id) throws InvalidEntityIdException {
        final Customer customer = this.em.find(Customer.class, id);

        if (customer == null) {
            throw new InvalidEntityIdException();
        }

        return customer;
    }

    public Customer create(String firstName,
                           String lastName,
                           String email,
                           String password,
                           String phoneNumber,
                           String address) throws InvalidConstraintException {
        final Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setPassword(this.passwordHash.generate(password.toCharArray()));
        customer.setPhoneNumber(phoneNumber);
        customer.setAddress(address);

        Set<ConstraintViolation<Customer>> violations = this.validator.validate(customer);
        // There are invalid data
        if (!violations.isEmpty()) {
            throw new InvalidConstraintException(violations.toString());
        }
        this.em.persist(customer);
        this.em.flush();

        return customer;
    }
}
