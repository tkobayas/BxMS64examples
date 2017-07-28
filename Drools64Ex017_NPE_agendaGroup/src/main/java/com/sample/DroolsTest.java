package com.sample;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * This is a sample class to launch a rule.
 */
public class DroolsTest {

    public static final void main(String[] args) {
        try {
            // load up the knowledge base
            KieServices ks = KieServices.Factory.get();
            KieContainer kContainer = ks.getKieClasspathContainer();
            KieSession kSession = kContainer.newKieSession();

            // go !
                        Person john = new Person("john", 20);
//            Person john = new Person("john",new Address("mystreet"), 20);
            kSession.insert(john);
            kSession.fireAllRules();
            
            
            kSession.getAgenda().getAgendaGroup("group1").setFocus();
            kSession.fireAllRules();
            
            kSession.getAgenda().getAgendaGroup("group2").setFocus();
            kSession.fireAllRules();
            
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
