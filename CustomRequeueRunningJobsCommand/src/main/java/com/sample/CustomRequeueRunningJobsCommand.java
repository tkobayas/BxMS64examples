package com.sample;

import java.util.Date;

import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.executor.RequeueAware;
import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.executor.ExecutorService;
import org.kie.api.executor.Reoccurring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom RequeueRunningJobsCommand which runs repeatedly
 * So can be used for automatic fail-over of async jobs.
 * 
 */
public class CustomRequeueRunningJobsCommand implements Command, Reoccurring {

    private static final Logger logger = LoggerFactory.getLogger(CustomRequeueRunningJobsCommand.class);

    private static final long DEFAULT_MAX_RUNNING_TIME = 600000L; // default 600sec
    private static final long DEFAULT_RUN_PERIOD = 60000L; // default 60sec

    private long maxRunningTime = DEFAULT_MAX_RUNNING_TIME;
    private long runPeriod = DEFAULT_RUN_PERIOD;

    public ExecutionResults execute(CommandContext ctx) {

        Long maxRunningTimeConfigured = (Long) ctx.getData("MaxRunningTime");
        if (maxRunningTimeConfigured == null) {
            logger.warn("MaxRunningTime is not configured. Default is " + DEFAULT_MAX_RUNNING_TIME + "ms");
        } else {
            maxRunningTime = maxRunningTimeConfigured;
        }
        Long runPeriodConfigured = (Long) ctx.getData("RunPeriod");
        if (runPeriodConfigured == null) {
            logger.warn("RunPeriod is not configured. Default is " + DEFAULT_RUN_PERIOD + "ms");
        } else {
            runPeriod = runPeriodConfigured;
        }

        try {
            ExecutorService executorService = ExecutorServiceFactory.newExecutorService(null);
            if (executorService instanceof RequeueAware) {
                logger.info("Requeue jobs older than {}", maxRunningTime);
                ((RequeueAware) executorService).requeue(maxRunningTime);
            } else {
                logger.info("Executor Service is not capable of jobs requeue");
            }

        } catch (Exception e) {
            logger.error("Error while creating CDI bean from jbpm executor", e);
        }

        logger.info("Command executed on executor with data {}", ctx.getData());
        ExecutionResults executionResults = new ExecutionResults();
        return executionResults;
    }

    public Date getScheduleTime() {
        long current = System.currentTimeMillis();
        Date nextSchedule = new Date(current + runPeriod);
        logger.debug("Next schedule for job {} is set to {}", this.getClass().getSimpleName(), nextSchedule);
        return nextSchedule;
    }

}
