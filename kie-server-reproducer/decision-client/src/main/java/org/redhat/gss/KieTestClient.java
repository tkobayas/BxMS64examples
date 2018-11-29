package org.redhat.gss;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.jms.IllegalStateException;

import org.jbpm.kie.services.impl.query.mapper.ProcessInstanceQueryMapper;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.api.model.definition.QueryFilterSpec;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.api.util.QueryFilterSpecBuilder;
import org.kie.server.client.QueryServicesClient;

public class KieTestClient extends AbstractKieServerConnector {

	private static final String G = "org.redhat.gss";
	private static final String A = "async-mi-test";
	private static final String V = "1.0";
	private static final String CONTAINER_ID = G + ":" + A + ":" + V;
	private static final String FINAL_DRAFT_PROCESS_ID = "MultiAttempts.AllInOne";
	private static final KieTestClient client = new KieTestClient();

	public static void main(String[] args) throws InterruptedException, IllegalStateException {

		ReleaseId rid = new ReleaseId();
		rid.setGroupId(G);
		rid.setArtifactId(A);
		rid.setVersion(V);

		KieContainerResource kcr = new KieContainerResource(rid);

		client.getClient().disposeContainer(CONTAINER_ID);
		client.getClient().createContainer(CONTAINER_ID, kcr);

		System.out.println("=== starting async MI sub-process test ===");

		final long pid2 = startProcess(FINAL_DRAFT_PROCESS_ID);
		
		// wait for the async execution to finish
		try {
		    Thread.sleep(1000);
		} catch(Exception ex) {
		
		}
		
		List<String> signals = client.getProcessClient().getAvailableSignals(CONTAINER_ID, pid2);
		System.out.println("Here is a list of signals for this pid:" + pid2);
		signals.forEach(s -> {
			System.out.println(s);
			client.getProcessClient().signalProcessInstance(CONTAINER_ID, pid2, s, "we don't care");
		});
		ProcessInstance p2 = client.getProcessClient().getProcessInstance(CONTAINER_ID, pid2);
		if (p2.getState().equals(2)) {
			System.out.println("Process successfully ended");
		} else {

			throw new IllegalStateException("Process must be completed at this point");
		}
	}

	public static long startProcess(String processId) {
		return client.getProcessClient().startProcess(CONTAINER_ID, processId);
	}

	public static void abort(Long pid) {
		client.getProcessClient().abortProcessInstance(CONTAINER_ID, pid);
	}



}
