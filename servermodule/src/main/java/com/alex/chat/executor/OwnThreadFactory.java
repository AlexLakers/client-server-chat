package com.alex.chat.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is a thread factory that contains defined rules for the every new thread in the thread pool executor.
 */
public class OwnThreadFactory implements ThreadFactory {
    private final static Logger logger = LoggerFactory.getLogger(OwnThreadFactory.class);
    private final AtomicInteger countThreads=new AtomicInteger(1);
    private final String NAME_THREAD_PATTERN = "Pool-%1$s-thread-%2$d";
    @Override
    public Thread newThread(Runnable r){
        Thread thread =new Thread(r);
        thread.setName(String.format(NAME_THREAD_PATTERN,"clients",countThreads.getAndIncrement()));
        thread.setDaemon(true);
        logger.debug("A new thread with name[{}] and his daemon status[{}]",thread.getName(),thread.isDaemon());
        return thread;
    }
}
