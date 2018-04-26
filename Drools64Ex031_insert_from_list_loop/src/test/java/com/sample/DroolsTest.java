package com.sample;

import java.util.ArrayList;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class DroolsTest {

    @Test
    public void testRule() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        KieSession ksession = kContainer.newKieSession();
        
        //KieRuntimeLogger logger = KieServices.Factory.get().getLoggers().newFileLogger(ksession, "mylog");

        ksession.setGlobal("results", new ArrayList<Person>());
        
        Person p1 = new Person("John", 25, 2000);
        Person p2 = new Person("Paul", 23, 2000);
        Person p3 = new Person("George", 21, 2000);
        
        ArrayList<Person> list = new ArrayList<Person>();
        list.add(p1);
        list.add(p2);
        list.add(p3);

        ksession.insert(list);
        //ksession.fireAllRules(); // No need to call fireAllRules() because rules are fired by Rule task in ruleflow
        
        System.out.println("----");

        ksession.startProcess("com.sample.bpmn.hello.flow");

        System.out.println("----");
        
        System.out.println("results = " + ksession.getGlobal("results"));

        //logger.close();
        
        ksession.dispose();
    }
}
