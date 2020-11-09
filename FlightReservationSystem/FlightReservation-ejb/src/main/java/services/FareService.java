package services;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
public class FareService {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;
}
