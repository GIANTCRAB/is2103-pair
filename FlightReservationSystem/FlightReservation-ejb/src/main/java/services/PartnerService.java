package services;

import entities.Partner;
import exceptions.InvalidEntityIdException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
public class PartnerService {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;

    public Partner findById(Long id) throws InvalidEntityIdException {
        final Partner partner = this.em.find(Partner.class, id);

        if (partner == null) {
            throw new InvalidEntityIdException();
        }

        return partner;
    }
}
