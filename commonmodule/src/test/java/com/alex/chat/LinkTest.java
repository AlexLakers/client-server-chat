package com.alex.chat;

import org.junit.jupiter.api.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;


class LinkTest {
    private ExecutorService service;
    private CountDownLatch latch;

    @BeforeEach
    void setUp(){
        latch=new CountDownLatch(1);
        service = Executors.newFixedThreadPool(2);
    }
    @AfterEach
    void setDown(){
        service.shutdownNow();
    }


    @Test
    void readData() throws ExecutionException,InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(2);
        String expected = "some info";
        service.submit(() -> {

            try(ServerSocket serverSocket = new ServerSocket(5005)){

                latch.countDown();
                try(Socket socket = serverSocket.accept();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())){
                    objectOutputStream.writeObject(new DataPacket(Headers.EXCHANGE, expected));

                }
                serverSocket.close();
            }
            catch (IOException e){
                new RuntimeException(e);
            }


        });


        Future<String> future = service.submit(() -> {
            latch.await();
            try (Link link = new Link(new Socket("127.0.0.1", 5005))) {
                DataPacket data=link.readData();
                //link.close();
                return data.getBody();
            }
        });
        String actual =future.get();
        Assertions.assertEquals(expected,actual);

    }


    @Test
    void writeData()throws InterruptedException,ExecutionException {

        String expected = "some info";


        service.submit(() -> {

            try(ServerSocket serverSocket = new ServerSocket(5003)){
                synchronized (this) {
                    this.notifyAll();
                }
                try(Socket socket = serverSocket.accept();
                    Link link = new Link(socket)){
                    link.writeData(new DataPacket(Headers.EXCHANGE, expected));
                }
            }
            catch (IOException e){
                new RuntimeException(e);
            }

        });


        Future<String> future = service.submit(() -> {
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            try (Socket socket = new Socket("127.0.0.1", 5003);
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())){
                return ((DataPacket)objectInputStream.readObject()).getBody();
            }
        });
        String actual =future.get();

        Assertions.assertEquals(expected,actual);
    }


}
