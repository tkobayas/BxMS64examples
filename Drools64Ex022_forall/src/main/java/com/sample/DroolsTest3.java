package com.sample;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * This is a sample class to launch a rule.
 */
public class DroolsTest3 {

    public static final void main(String[] args) {
        try {
            // load up the knowledge base
	        KieServices ks = KieServices.Factory.get();
    	    KieContainer kContainer = ks.getKieClasspathContainer();
        	KieSession kSession = kContainer.newKieSession();

            Person john = new Person("john", 20, 2000);
            Person paul = new Person("paul", 18, 1800);
            Person george = new Person("george", 17, 1600);
            Person ringo = new Person("ringo", 20, 2200);

            kSession.insert(john);
            kSession.insert(paul);
            kSession.insert(george);
            kSession.insert(ringo);

            kSession.fireAllRules();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


}
