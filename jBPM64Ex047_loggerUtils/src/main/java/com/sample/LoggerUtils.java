package com.sample;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtils {

    private static Map<String, Logger> loggerMap = new HashMap<String, Logger>();

    public static Logger getLogger(String name) {
        if (loggerMap.containsKey(name)) {
            return loggerMap.get(name);
        } else {
            Logger logger = LoggerFactory.getLogger(name);
            loggerMap.put(name, logger);
            return logger;
        }
    }
}
