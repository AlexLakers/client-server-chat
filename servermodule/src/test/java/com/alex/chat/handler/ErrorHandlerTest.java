package com.alex.chat.handler;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {
    private TaskServer taskServer;
    private CountDownLatch latch;
    @BeforeEach
    void setUp(){
        latch=new CountDownLatch(1);
        taskServer=new TaskServer(latch,5006);
    }
    class TaskServer implements Runnable{
        private CountDownLatch latch;
        private int port;
        public TaskServer(CountDownLatch latch,int port){
            this.latch=latch;
            this.port=port;
        }
        @Override
        public void run(){
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                latch.countDown();
                try (Socket socket = serverSocket.accept()) {
                    new MainHandler(socket).run();
                }
            } catch (IOException e) {
                new RuntimeException(e);
            }
        }
    }

    @Test
    void run_shouldCancelClientTask_whenClientTaskIncreaseThenLimit() {
       /* ExecutorFactory.
        service.submit(taskServer);

        service.submit(new ClientSimulatorTask(latch,true))*/
    }

}