package com.sample;

import example.scorecard_example.Customer;
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
        	Customer john = new Customer();
        	john.setName("John");
        	john.setAge(25.0d);
            kSession.insert(john);
            int fired = kSession.fireAllRules();
            System.out.println("fired = " + fired);
            
            System.out.println("john.getScore() = " + john.getScore());
            
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


}
