package services;

import entities.Employee;
import entities.EmployeeRole;
import exceptions.IncorrectCredentialsException;
import exceptions.NotAuthenticatedException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;

@LocalBean
@Stateless
public class AuthService {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;
    @Inject
    private Pbkdf2PasswordHash passwordHash;

    public Employee employeeLogin(String username, String password) throws IncorrectCredentialsException {
        final TypedQuery<Employee> searchQuery = this.em.createQuery("select e from Employee e where e.username = ?1", Employee.class)
                .setParameter(1, username);

        try {
            final Employee searchResult = searchQuery.getSingleResult();

            // WARNING: If password isn't hashed, just allow it to compare anyways. Don't do this in real world! Always hash your passwords
            try {
                if (this.passwordHash.verify(password.toCharArray(), searchResult.getPassword())) {
                    return searchResult;
                }
            } catch (IllegalArgumentException e) {
                if (password.equals(searchResult.getPassword())) {
                    return searchResult;
                }
            }
        } catch (NoResultException ignored) {
        }

        throw new IncorrectCredentialsException();
    }

    public void checkPermission(Employee employee, EmployeeRole employeeRoleNeeded) throws NotAuthenticatedException {
        final Employee foundEmployee = this.em.find(Employee.class, employee.getEmployeeId());
        if (foundEmployee != null && foundEmployee.getEmployeeRole().equals(employeeRoleNeeded)) {
            return;
        }

        throw new NotAuthenticatedException();
    }
}
