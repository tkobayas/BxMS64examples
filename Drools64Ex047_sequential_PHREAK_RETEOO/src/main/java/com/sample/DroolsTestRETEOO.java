package com.sample;

import java.util.Arrays;

import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.conf.SequentialOption;

/**
 * This is a sample class to launch a rule.
 */
public class DroolsTestRETEOO {

    public static final void main(String[] args) {
        try {

            // If you set sequential mode to true, you should not use re-evaluation by update/modify.
            // If you have update/modify in rules, the result may differ depending on rules ordering in a DRL file
            System.setProperty("drools.sequential", "true");
            
            // IMPORTANT: This requires drools-reteoo dependency. See pom.xml
            // <dependency>
            //   <groupId>org.drools</groupId>
            //   <artifactId>drools-reteoo</artifactId>
            //   <version>${drools.version}</version>
            // </dependency>
            System.setProperty("drools.ruleEngine", "reteoo");

            // load up the knowledge base
            KieServices ks = KieServices.Factory.get();
            KieContainer kContainer = ks.getKieClasspathContainer();
            StatelessKieSession kSession = kContainer.newStatelessKieSession(); // can use only stateless

            // go !
            MyFact fact = new MyFact();
            fact.setField1(false);

            kSession.execute(CommandFactory.newInsertElements(Arrays.asList(fact)));

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
