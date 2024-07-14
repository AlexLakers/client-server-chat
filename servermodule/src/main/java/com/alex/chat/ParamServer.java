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
        return String.format("The property file:server.port=%1$d,pool.main.thread.count=%2$d,pool.error.thread.count=%3$d,connections.queue.capacity=%4$d", port, countMainThread, countErrorThread, capQueue);
    }

}
