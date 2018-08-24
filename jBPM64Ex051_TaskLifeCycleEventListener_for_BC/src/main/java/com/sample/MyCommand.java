package com.sample;

import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyCommand implements Command{
    
    private static final Logger logger = LoggerFactory.getLogger(MyCommand.class);

    public ExecutionResults execute(CommandContext ctx) {
        logger.info("Command executed on executor with data {}", ctx.getData());
        
        logger.info("  ### for example, call an external service via HTTP");
        
        ExecutionResults executionResults = new ExecutionResults();
        return executionResults;
    }
    
}
