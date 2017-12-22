package com.redhat.gss;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManagerFactory;

import org.jbpm.process.audit.jms.AsyncAuditLogReceiver;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.kie.server.services.api.KieServerExtension;
import org.kie.server.services.impl.KieServerImpl;
import org.kie.server.services.impl.KieServerLocator;
import org.kie.server.services.jbpm.JbpmKieServerExtension;

@TransactionManagement(TransactionManagementType.CONTAINER)
@MessageDriven(name = "KieAuditJMSProcessor", activationConfig = {
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/queue/KIE.AUDIT"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "1")})
public class KieAuditJMSProcessor extends AsyncAuditLogReceiver {

    public KieAuditJMSProcessor() {
        super(null);
        System.out.println("KieAuditJMSProcessor()");
    }

    public KieAuditJMSProcessor(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
        System.out.println("KieAuditJMSProcessor(EntityManagerFactory entityManagerFactory)");
    }

    @PostConstruct
    public void initialize() {
        System.out.println("@PostConstruct");
        for (int i = 0; i < 10; i++) {
            KieServerImpl kieServer = KieServerLocator.getInstance();
            KieServerExtension jbpmExtension = kieServer.getServerRegistry().getServerExtension(JbpmKieServerExtension.EXTENSION_NAME);
            if (jbpmExtension == null || !jbpmExtension.isActive()) {
                System.out.println("jbpmExtension = " + jbpmExtension);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
            } else {
                break;
            }
        }

        EntityManagerFactory emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.domain");
        setEntityManagerFactory(emf);
    }
}
