package com.alex.chat.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * This class describes behaviour that's used during task rejecting process in the executor.
 */
public class ServRejectExecutorHandler implements RejectedExecutionHandler {
    private final static Logger logger = LoggerFactory.getLogger(ServRejectExecutorHandler.class);
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        ((FutureTask)r).cancel(true);
        logger.debug("The task of error link is cancelled");
    }
}
