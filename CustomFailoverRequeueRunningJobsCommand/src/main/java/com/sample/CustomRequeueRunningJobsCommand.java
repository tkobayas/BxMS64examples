package com.sample;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
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
    private static final int DEFAULT_TIMEOUT = 10000; // default 10sec

    private long maxRunningTime = DEFAULT_MAX_RUNNING_TIME;
    private long runPeriod = DEFAULT_RUN_PERIOD;

    private String theOtherKieServerUrl = System.getProperty("org.kie.server.location.other");
    private String heartbeatTimeoutStr = System.getProperty("CustomRequeueRunningJobsCommand.heartbeatTimeout");
    private int heartbeatTimeout = DEFAULT_TIMEOUT;

    private String kieServerUser = System.getProperty("org.kie.server.user");
    private String kieServerPassword = System.getProperty("org.kie.server.pwd");

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
            logger.info("runPeriod = " + runPeriod);
        }

        if (theOtherKieServerUrl == null || theOtherKieServerUrl.isEmpty()) {
            logger.warn("System property 'org.kie.server.location.other' is not configured. Heartbeat check is skipped."); // go ahead
        } else {
            boolean available = pingUrl(theOtherKieServerUrl);
            if (available) {
                logger.info("No need to requeue");
                logger.info("Command executed on executor with data {}", ctx.getData());
                ExecutionResults executionResults = new ExecutionResults();
                return executionResults;
            } else {
                logger.info("Check requeue");
            }
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

    private boolean pingUrl(String url) {
        boolean available = false;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

            if (heartbeatTimeoutStr == null || heartbeatTimeoutStr.isEmpty()) {
                logger.debug("System property 'CustomRequeueRunningJobsCommand.heartbeatTimeout' is not configured. Default is " + DEFAULT_TIMEOUT + "ms");
                heartbeatTimeout = DEFAULT_TIMEOUT;
            } else {
                int value;
                try {
                    value = Integer.parseInt(heartbeatTimeoutStr);
                    heartbeatTimeout = value;
                    logger.info("heartbeatTimeout is " + heartbeatTimeout + "ms");
                } catch (NumberFormatException nfe) {
                    logger.warn("Failed to parse " + heartbeatTimeoutStr + ". Default is " + DEFAULT_TIMEOUT + "ms", nfe);
                    heartbeatTimeout = DEFAULT_TIMEOUT;
                }
            }

            connection.setConnectTimeout(heartbeatTimeout);
            connection.setReadTimeout(heartbeatTimeout);

            ((HttpURLConnection) connection).setRequestMethod("HEAD");

            String userpassword = kieServerUser + ":" + kieServerPassword;

            String authorization = "Basic " + Base64.getEncoder().encodeToString(userpassword.getBytes());
            connection.setRequestProperty("Authorization", authorization);

            connection.connect();
            logger.info(url + " is available");
            available = true;
        } catch (final MalformedURLException e) {
            throw new IllegalStateException("Bad URL: " + url, e);
        } catch (final IOException e) {
            logger.info(url + " is unavailable");
            available = false;
        }
        return available;
    }

    public Date getScheduleTime() {
        long current = System.currentTimeMillis();
        Date nextSchedule = new Date(current + runPeriod);
        logger.info("Next schedule for job {} is set to {}", this.getClass().getSimpleName(), nextSchedule);
        return nextSchedule;
    }

}
