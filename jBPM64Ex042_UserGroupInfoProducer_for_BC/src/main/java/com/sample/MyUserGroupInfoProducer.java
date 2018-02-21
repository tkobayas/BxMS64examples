package com.sample;


import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;

import org.jbpm.services.cdi.Selectable;
import org.jbpm.services.cdi.producer.UserGroupInfoProducer;
import org.jbpm.services.task.identity.DefaultUserInfo;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.task.api.UserInfo;

@ApplicationScoped
@Alternative
@Selectable
public class MyUserGroupInfoProducer implements UserGroupInfoProducer {

    // Edit business-central.war/WEB-INF/beans.xml
    //  <beans xmlns="http://java.sun.com/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    //      xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://docs.jboss.org/cdi/beans_1_0.xsd">
    //    <alternatives>
    //      <class>com.sample.MyUserGroupInfoProducer</class>
    //    </alternatives>
    //  </beans>

    private UserGroupCallback callback = new MyUserGroupCallbackImpl();
    private UserInfo userInfo = new DefaultUserInfo(true);
    
    @Override
    @Produces
    public UserGroupCallback produceCallback() {
        return callback;
    }

    @Override
    @Produces
    public UserInfo produceUserInfo() {
        return userInfo;
    }

}
