package services;

import entities.Flight;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
public class FlightService {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(Flight flight) {
        this.em.remove(flight);
    }
}
