package com.sample;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;

/**
 * This is a sample class to launch a rule.
 */
public class DroolsTest {

    public static final void main(String[] args) {
        try {
            // load up the knowledge base
	        KieServices ks = KieServices.Factory.get();
    	    KieContainer kContainer = ks.getKieClasspathContainer();
        	KieSession ksession = kContainer.newKieSession("ksession-rules");

            EntryPoint ep = ksession.getEntryPoint("parking");

            TestEventRunner runner = new TestEventRunner();
            runner.setEp(ep);
            runner.setKsession(ksession);
            Thread th = new Thread(runner);
            th.start();

            System.out.println("ksession.fireUntilHalt()");
            ksession.fireUntilHalt();

            System.out.println("ksession.halt();"); // halted by TestEventRunner

            System.exit(0);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static class Message {

        public static final int HELLO = 0;
        public static final int GOODBYE = 1;

        private String message;

        private int status;

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getStatus() {
            return this.status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

    }

}
