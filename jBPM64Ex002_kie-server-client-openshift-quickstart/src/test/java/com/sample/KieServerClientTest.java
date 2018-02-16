package com.sample;

import static com.sample.Constants.BASE_URL;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import org.junit.Test;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;
import org.openshift.quickstarts.processserver.library.types.Book;
import org.openshift.quickstarts.processserver.library.types.Loan;
import org.openshift.quickstarts.processserver.library.types.LoanRequest;
import org.openshift.quickstarts.processserver.library.types.LoanResponse;
import org.openshift.quickstarts.processserver.library.types.ReturnRequest;
import org.openshift.quickstarts.processserver.library.types.ReturnResponse;
import org.openshift.quickstarts.processserver.library.types.Suggestion;
import org.openshift.quickstarts.processserver.library.types.SuggestionRequest;
import org.openshift.quickstarts.processserver.library.types.SuggestionResponse;

public class KieServerClientTest extends TestCase {

    private static final String USERNAME = "kieserver";
    private static final String PASSWORD = "kieserver1!";

    //    private static final MarshallingFormat FORMAT = MarshallingFormat.JSON;
    //    private static final MarshallingFormat FORMAT = MarshallingFormat.JAXB;

    @Test
    public void testProcess() {

        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(BASE_URL, USERNAME, PASSWORD);
        //        config.setMarshallingFormat(FORMAT);
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(Book.class);
        classes.add(Loan.class);
        classes.add(LoanRequest.class);
        classes.add(LoanResponse.class);
        classes.add(ReturnRequest.class);
        classes.add(ReturnResponse.class);
        classes.add(Suggestion.class);
        classes.add(SuggestionRequest.class);
        classes.add(SuggestionResponse.class);
        config.addJaxbClasses(classes);
        KieServicesClient client = KieServicesFactory.newKieServicesClient(config);

        ProcessServicesClient processClient = client.getServicesClient(ProcessServicesClient.class);

        Map<String, Object> parameters = new HashMap<String, Object>();
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setIsbn("AAAA1111");
        parameters.put("loanRequest", loanRequest);
        LoanResponse loanResponse;
        
        Long pid = processClient.startProcess("processserver-library", "LibraryProcess", parameters);
        
        System.out.println("pid = " + pid);
        
        
        loanResponse = (LoanResponse)processClient.getProcessInstanceVariable("processserver-library", pid, "loanResponse");
        
        System.out.println("loanResponse = " + loanResponse);
    }


}