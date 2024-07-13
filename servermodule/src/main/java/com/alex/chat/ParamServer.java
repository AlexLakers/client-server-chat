package com.alex.chat;

/**
 * This record describes(encapsulates) the params by 'config.properties' file.
 * @param port port  a listening server port.
 * @param countMainThread a count of main core-thread.
 * @param countErrorThread a count of error core-thread.
 * @param capQueue a capacity of the chat clients(tasks) queue.
 */
public record ParamServer(int port, int countMainThread, int countErrorThread, int capQueue) {
    @Override
    public String toString() {
        return String.format("The property file:port=%1$d,mainThread=%2$d,errorThread=%3$d,capQueue=%4$d", port, countMainThread, countErrorThread, capQueue);
    }
}
