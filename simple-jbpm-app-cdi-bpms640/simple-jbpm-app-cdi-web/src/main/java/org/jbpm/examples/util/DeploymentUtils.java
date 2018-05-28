package org.jbpm.examples.util;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.cdi.Kjar;

@ApplicationScoped
public class DeploymentUtils {

    public static final String DEPLOYMENT_ID = "com.sample:simple-jbpm-kjar:1.0.0-SNAPSHOT";

    @Inject
    @Kjar
    DeploymentService deploymentService;
    
    private static boolean initialized = false;

    public void init() {
        System.out.println("DeploymentUtils.init()");
        //        System.setProperty("org.jbpm.ht.callback", "custom");
        //        System.setProperty("org.jbpm.ht.custom.callback", "org.jbpm.examples.util.RewardsUserGroupCallback");
        
        if (!initialized ) {
            String[] gav = DEPLOYMENT_ID.split(":");
            DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(gav[0], gav[1], gav[2]);
            deploymentService.deploy(deploymentUnit);
        }
        initialized = true;
    }

    @Produces
    public DeploymentService getDeploymentService() {
        return deploymentService;
    }
}
