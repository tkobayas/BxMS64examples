package com.sample;

import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * This is a sample class to launch a rule.
 */
public class DroolsTest {

    public static final void main( String[] args ) {

        String groupId = "org.kie.example";
        String artifactId = "project1";
        String version = "1.0.0-SNAPSHOT";

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId( groupId, artifactId, version );
        KieContainer kcontainer = ks.newKieContainer( releaseId );

        KieSession ksession = kcontainer.newKieSession();
        ksession.insert( new Integer( 2 ) );
        int fired = ksession.fireAllRules();

        System.out.println( "fired = " + fired );
        
        ksession.dispose();

    }

}
