package com.alex.chat.executor;

import com.alex.chat.ParamsServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * This class is a factory for creating two executors by params which are defined in the {@link ParamsServer paramServer}.
 * It contains the methods that allows to create the error thread pool executor and the main thread pool executor.
 */
public final class ExecutorFactory {
    private final static Logger logger = LoggerFactory.getLogger(ExecutorFactory.class);
    private ExecutorFactory(){}

    public static ExecutorService createMainExecutor(int countMainThread,int capQueue){
        logger.debug("The process of creating main executor with countMainThread[{}],capQueue[{}]",countMainThread,capQueue);
        return new ThreadPoolExecutor(countMainThread,
                countMainThread,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(capQueue),
                new OwnThreadFactory());
    }

    public static ExecutorService createErrorExecutor(int countErrorThread){
        logger.debug("The process of creating error executor with countErrorThread[{}]",countErrorThread);
        return Executors.newFixedThreadPool(countErrorThread);
    }
}
