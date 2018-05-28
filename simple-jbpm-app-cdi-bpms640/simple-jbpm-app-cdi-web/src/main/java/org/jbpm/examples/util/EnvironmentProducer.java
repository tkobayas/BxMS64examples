package org.jbpm.examples.util;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

@ApplicationScoped
public class EnvironmentProducer {

    private EntityManagerFactory emf;
   
    
    @PersistenceUnit(unitName = "org.jbpm.domain")
    @ApplicationScoped
    @Produces
    public EntityManagerFactory getEntityManagerFactory() {
        if (this.emf == null) {
            this.emf = Persistence.createEntityManagerFactory("org.jbpm.domain");
        }
        return this.emf;
    }
   
}
