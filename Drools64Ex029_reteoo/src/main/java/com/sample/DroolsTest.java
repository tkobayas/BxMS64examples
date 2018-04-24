package com.sample;

import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.conf.RuleEngineOption;

/**
 * This is a sample class to launch a rule.
 */
public class DroolsTest {

    public static final void main(String[] args) {
        try {

            // load up the knowledge base
            KieServices ks = KieServices.Factory.get();
            KieContainer kContainer = ks.getKieClasspathContainer();

            KieBaseConfiguration kbConf = ks.newKieBaseConfiguration();
            kbConf.setOption(RuleEngineOption.RETEOO);
            KieBase kieBase = kContainer.newKieBase(kbConf);

            KieSession kSession = kieBase.newKieSession();

            // go !
            Person john = new Person("john", 20);
            kSession.insert(john);
            kSession.fireAllRules();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
