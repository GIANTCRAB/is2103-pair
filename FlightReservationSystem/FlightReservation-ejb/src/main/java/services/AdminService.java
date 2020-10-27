package services;

import entities.EmployeeRole;
import entities.Employee;
import exceptions.NotAuthenticatedException;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@LocalBean
@Stateless
public class AdminService {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;
    
    public Employee create(EmployeeRole employeeRole, String firstName, String lastName, String password, String username) {
        final Employee newEmployee = new Employee();
        newEmployee.setEmployeeRole(employeeRole);
        newEmployee.setFirstName(firstName);
        newEmployee.setLastName(lastName);
        newEmployee.setPassword(password);
        newEmployee.setUsername(username);
        
        this.em.persist(newEmployee);
        this.em.flush();
        
        return newEmployee;
    }
    
    public List<Employee> getEmployees() {
        final TypedQuery<Employee> searchQuery = this.em.createQuery("select e from Employee e", Employee.class);

        return searchQuery.getResultList();
    }
}
