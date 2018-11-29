package org.redhat.gss;

import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.client.JobServicesClient;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.kie.server.client.RuleServicesClient;
import org.kie.server.client.UserTaskServicesClient;
import org.kie.server.client.admin.ProcessAdminServicesClient;

public abstract class AbstractKieServerConnector {

	private KieServicesClient client;
	protected String container;

	public AbstractKieServerConnector() {

		String url = System.getProperty("kie-server-url", "http://localhost:8280/kie-server/services/rest/server");
		String username = System.getProperty("kie-server-username", "anton");
		String password = System.getProperty("kie-server-password", "password1!");
		
		KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(url, username, password);
		config.setMarshallingFormat(MarshallingFormat.JSON);
		config.setTimeout(10000L);

		this.client = KieServicesFactory.newKieServicesClient(config);
	}

	public JobServicesClient getJobClient() {

		return this.client.getServicesClient(JobServicesClient.class);
	}

	public ProcessAdminServicesClient getAdminClient() {

		return this.client.getServicesClient(ProcessAdminServicesClient.class);
	}

	public ProcessServicesClient getProcessClient() {

		return this.client.getServicesClient(ProcessServicesClient.class);
	}

	public UserTaskServicesClient getTaskClient() {

		return this.client.getServicesClient(UserTaskServicesClient.class);
	}

	public QueryServicesClient getQueryClient() {

		return this.client.getServicesClient(QueryServicesClient.class);
	}

	public RuleServicesClient getRuleClient() {

		return this.client.getServicesClient(RuleServicesClient.class);
	}

	public KieServicesClient getClient() {
		return client;
	}

}
