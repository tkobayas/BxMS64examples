package com.sample;

import org.jbpm.process.instance.event.listeners.TriggerRulesEventListener;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class DroolsTest {
    
    public static long count = 0;

    public static final void main(String[] args) {
        
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        KieSession ksession = kContainer.newKieSession();

        ksession.addEventListener(new TriggerRulesEventListener(ksession));

        Message message = new Message();
        message.setMessage("Hello Rule Flow");
        message.setStatus(Message.HELLO);
        ksession.insert(message);
        
        System.out.println("----");

        ksession.startProcess("com.sample.parent");

        System.out.println("----");

        ksession.dispose();
    }
}
