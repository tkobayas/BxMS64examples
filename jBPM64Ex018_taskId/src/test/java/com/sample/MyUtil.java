package com.sample;

import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessContext;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Task;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

public class MyUtil {

    public static void work(ProcessContext kcontext, long processVariable2) {
//        System.out.println("taskId1 = " + processVariable1 );
        System.out.println("taskId2 = " + processVariable2 );
        long processInstanceId = kcontext.getProcessInstance().getId();
        TaskService taskService = ((RuntimeManager)kcontext.getKieRuntime().getEnvironment().get("RuntimeManager")).getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId)).getTaskService();
        Task task = taskService.getTaskById(processVariable2);
        System.out.println(task.getTaskData().getStatus());
        System.out.println(task.getTaskData().getActualOwner().getId());

    }
}
