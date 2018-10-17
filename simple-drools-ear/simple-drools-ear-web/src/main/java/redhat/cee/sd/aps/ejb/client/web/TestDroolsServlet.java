package redhat.cee.sd.aps.ejb.client.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import redhat.cee.sd.aps.model.Account;

public class TestDroolsServlet extends HttpServlet {

    private static final long serialVersionUID = -6189108351718996259L;

    private static Logger log = Logger.getLogger(TestDroolsServlet.class.getName());

    

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        log.info("========== Testing Remote HELLO EJB START ==========");
        
        // load up the knowledge base
        KieServices ks = KieServices.Factory.get();
	    KieContainer kContainer = ks.getKieClasspathContainer();
    	KieSession kSession = kContainer.newKieSession("ksession-rules");

    	Account account = new Account(200);
        account.withdraw(150);
        kSession.insert(account);
        kSession.fireAllRules();
        
        
        try {
        	

            PrintWriter out = response.getWriter();

            out.println("<html>");
            out.println(" <head>");
            out.println("  <title>Test - Servlet Example</title>");
            out.println(" </head>");
            out.println(" <body>");
            out.println("  <h1>Hello, World!</h1>");
            out.println("  <h4>***** Drools called, please check log output *****</h4>");
            out.println(" </body>");
            out.println("</html>");
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        
    }


}
