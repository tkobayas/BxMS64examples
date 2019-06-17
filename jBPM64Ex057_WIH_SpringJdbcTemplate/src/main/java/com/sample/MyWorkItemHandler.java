package com.sample;

import com.sample.bean.Person;
import com.sample.configuration.ApplicationConfig;
import com.sample.services.PersonService;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class MyWorkItemHandler implements WorkItemHandler {

    private static Logger logger = LoggerFactory.getLogger(MyWorkItemHandler.class);

    public MyWorkItemHandler() {
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

        logger.info("enter executeWorkItem()");

        AbstractApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        PersonService personService = (PersonService) context.getBean("personService");
        
        Person john = new Person(1, "John", "Lennon", 32);
        personService.addPerson(john);
        context.close();

        manager.completeWorkItem(workItem.getId(), null);

        return;

    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        manager.abortWorkItem(workItem.getId());
    }

}
