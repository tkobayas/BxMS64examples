package com.sample;

import org.jbpm.process.instance.event.listeners.TriggerRulesEventListener;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.ConsequenceException;
import org.kie.api.runtime.rule.Match;

public class RHSExceptionTest {

	@Test
	public void testRule() {
		try {
			KieServices ks = KieServices.Factory.get();
			KieContainer kContainer = ks.getKieClasspathContainer();
			KieSession ksession = kContainer.newKieSession();

			ksession.addEventListener(new TriggerRulesEventListener(ksession));

			Message message = new Message();
			message.setMessage("Hello Rule Flow");
			message.setStatus(Message.HELLO);
			ksession.insert(message);

			System.out.println("----");

			ksession.startProcess("com.sample.bpmn.hello.flow");

			System.out.println("----");

			ksession.dispose();
		} catch (Exception e) {
			System.out.println("Thrown Exception = " + e.getClass());
			System.out.println("       getCause() = " + e.getCause().getClass());
			System.out.println();
			
			ConsequenceException ce = (ConsequenceException)e.getCause();
			Match match = ce.getMatch();
			System.out.println(match);
			System.out.println(ce.getRule());
			ce.printFacts();
			
			System.out.println("-----------------");

			e.printStackTrace();
		}
	}
}
