package com.sample;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class DroolsTest {

    public static final void main(String[] args) {
        try {
            // load up the knowledge base
            KieServices ks = KieServices.Factory.get();
            KieContainer kContainer = ks.getKieClasspathContainer();
            KieSession kSession = kContainer.newKieSession();

            // go !
            Person p1 = new Person("John", 22);
            Person p2 = new Person("Paul", 34);

            kSession.insert(p1);
            kSession.insert(p2);

            
            kSession.fireAllRules();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
