package com.alex.chat;

import com.alex.chat.testdata.ServerSimulatorTask;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;


@ExtendWith(MockitoExtension.class)
class ReceiveHandlerTest {
    private static final int port=5006;
    private  final static String LOGIN="TEST_LOGIN";
    private final static InputStream ConsoleInputStream=System.in;
    private ByteArrayInputStream byteArrayInputStream;
    private CountDownLatch latch;

    @BeforeEach
    void setLatchAndInputStream(){
        latch=new CountDownLatch(1);
        byteArrayInputStream = new ByteArrayInputStream(LOGIN.concat("\n").getBytes(StandardCharsets.UTF_8));
        System.setIn(byteArrayInputStream);
    }
    @AfterEach
    void setDefaultInputStream(){
        System.setIn(ConsoleInputStream);
    }
    class TaskClient implements Runnable{
        private CountDownLatch latch;
        private int port;
        public TaskClient(CountDownLatch latch,int port){
            this.latch=latch;
            this.port=port;
        }
        @Override
        public void run(){
            try (Link link = new Link(new Socket("127.0.0.1",port))) {
                latch.await();
                var lock=new ReentrantLock();
                new ReceiveHandler(link,lock,lock.newCondition()).run();
            } catch (InterruptedException|IOException e) {
                new RuntimeException(e);
            }
        }
    }
    @Test
    void run_shouldHandleReceivedDataPacketFromServer_whenTypeOfReceivedDataPacketIsValid()throws TimeoutException,InterruptedException,ExecutionException {
        ExecutorService executor= Executors.newFixedThreadPool(2);
        Future<String> future=executor.submit(new ServerSimulatorTask(latch,false,port));
        executor.submit(new TaskClient(latch,port));

        String actual=future.get(5,TimeUnit.SECONDS);


        Assertions.assertEquals(LOGIN,actual);

    }
    @Test
    void run_shouldFinishHandlingProcess_whenTypeOfDataPacketIsInvalid()throws ExecutionException,InterruptedException,TimeoutException{
        ExecutorService executor= Executors.newFixedThreadPool(2);
        Future<String> future=executor.submit(new ServerSimulatorTask(latch,true,port));
        executor.submit(new TaskClient(latch,port));
        String expected="error";

        String actual=future.get(5,TimeUnit.SECONDS);

        Assertions.assertEquals(expected,actual);
    }
}