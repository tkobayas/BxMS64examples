package com.sample;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class DroolsTest {

    @Test
    public void testContainer() {
        try {
            KieServices kServices = KieServices.Factory.get();
            ReleaseId releaseId = kServices.newReleaseId("com.sample", "Drools64Ex001_basic_kjar", "1.0.0-SNAPSHOT");
            KieContainer kContainer = kServices.newKieContainer(releaseId);
            KieScanner scanner = kServices.newKieScanner(kContainer);
            scanner.start(5000);

            for (int i = 0; i < 100; i++) {
                KieSession kSession = kContainer.newKieSession();
                Person john = new Person("john", 20);
                kSession.insert(john);
                kSession.fireAllRules();
                kSession.dispose();
                
                Thread.sleep(1000);
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
