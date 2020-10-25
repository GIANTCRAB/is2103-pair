package services;

import entities.Airport;
import exceptions.InvalidEntityIdException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
public class AirportService {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;

    public Airport findAirportByCode(String iataCode) throws InvalidEntityIdException {
        final Airport airport = this.em.find(Airport.class, iataCode);

        if (airport == null) {
            throw new InvalidEntityIdException();
        }

        return airport;
    }
}
