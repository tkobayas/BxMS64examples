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
        	KieSession kSession = kContainer.newKieSession("ksession-rules");

            // go !
            kSession.insert(new Message(1));
            kSession.insert(new Message(2));
            kSession.insert(new Message(3));
            kSession.insert(new Message(4));

            kSession.fireAllRules();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static class Message {

        private int index;

        
        
        public Message(int index) {
            super();
            this.index = index;
        }


        public int getIndex() {
            return index;
        }

        
        public void setIndex(int index) {
            this.index = index;
        }


        @Override
        public String toString() {
            return "Message [index=" + index + "]";
        }
        
        

    }

}
