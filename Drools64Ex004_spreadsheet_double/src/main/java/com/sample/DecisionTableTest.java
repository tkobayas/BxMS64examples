package com.sample;

import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * This is a sample class to launch a rule.
 */
public class DecisionTableTest {

    public static final void main(String[] args) {
        try {
            // load up the knowledge base
	        KieServices ks = KieServices.Factory.get();
	        
            SpreadsheetCompiler compiler = new SpreadsheetCompiler();
            String drl = compiler.compile(ks.getResources().newClassPathResource("dtables/Sample.xls")
                    .getInputStream(), InputType.XLS);
            System.out.println(drl);

            
    	    KieContainer kContainer = ks.getKieClasspathContainer();
        	KieSession kSession = kContainer.newKieSession("ksession-dtables");

            // go !
        	Person person = new Person();
        	person.setSalary(5000);
            kSession.insert(person);
            kSession.fireAllRules();
            
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


}
