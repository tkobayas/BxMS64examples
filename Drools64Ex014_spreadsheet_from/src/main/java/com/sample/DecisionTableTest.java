package com.sample;

import java.util.ArrayList;
import java.util.List;

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
	        
	        // useful for debug
	        SpreadsheetCompiler compiler = new SpreadsheetCompiler();
	        String drl = compiler.compile(ks.getResources().newClassPathResource("dtables/Sample.xls")
	            .getInputStream(), InputType.XLS);
	        System.out.println(drl);
	        
    	    KieContainer kContainer = ks.getKieClasspathContainer();
        	KieSession kSession = kContainer.newKieSession("ksession-dtables");

        	List<Employee> list1 = new ArrayList<Employee>();
            Employee john = new Employee( "john" );
            Employee george = new Employee( "george" );
            list1.add( john );
            list1.add( george );
            Department dept1 = new Department( "dept1", list1 );
            
            List<Employee> list2 = new ArrayList<Employee>();
            Employee paul = new Employee( "paul" );
            list2.add( paul );
            Department dept2 = new Department( "dept2", list2 );

            kSession.insert( dept1 );
            kSession.insert( dept2 );
            
            kSession.fireAllRules();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
