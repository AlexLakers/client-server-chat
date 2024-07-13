package com.alex.chat.handler;

import com.alex.chat.executor.ExecutorFactory;
import com.alex.chat.testdata.ClientSimulatorTask;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
@ExtendWith(MockitoExtension.class)
class MainHandlerTest {

    private TaskServer taskServer;
    private static final int port=5006;
    private CountDownLatch latch;
    @BeforeEach
    void setUp(){
        latch=new CountDownLatch(1);
        taskServer=new TaskServer(latch,port);
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
        //  @Timeout(5)
    void run_shouldHandleDataPacketsAndAnswerClient_whenTypeOfDataPacketsIsValid()throws TimeoutException,InterruptedException, ExecutionException {
        ExecutorService service=Executors.newFixedThreadPool(2);
        service.submit(taskServer);
        Future<String> future=service.submit(new ClientSimulatorTask(latch,false,port));
        String expected = "I am a good client-immitation'";


        String actual=future.get(10,TimeUnit.SECONDS);


        Assertions.assertEquals(expected,actual);

    }
    @Test
        // @Timeout(5)
    void run_shouldSendWarningDataPacket_whenTypeOfDataPacketIsInvalid() throws ExecutionException,InterruptedException,TimeoutException{
        ExecutorService service=Executors.newFixedThreadPool(2);
        service.submit(taskServer);
        Future<String> future=service.submit(new ClientSimulatorTask(latch,true,port));
        String expected = "The data packet from a chat-client contains incorrect header";


        String actual=future.get(5,TimeUnit.SECONDS);


        Assertions.assertEquals(expected,actual);
    }
    @Test
    void run_shouldCancelClientTask_whenClientTaskIncreaseThenLimit(){
        ExecutorService executor=ExecutorFactory.createMainExecutor(2,1);
        executor.submit(taskServer);

        Future<String> futureClientOne=executor.submit(new ClientSimulatorTask(latch,false,port));
        Future<String> futureClientTwo=executor.submit(new ClientSimulatorTask(latch,false,port));
        Future<String> futureClientThree=executor.submit(new ClientSimulatorTask(latch,false,port));


        Assertions.assertFalse(futureClientOne.isDone());
        Assertions.assertFalse(futureClientTwo.isDone());
        Assertions.assertTrue(futureClientThree.isCancelled());

    }


}