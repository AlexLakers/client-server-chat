package com.alex.chat.testdata;

import com.alex.chat.DataPacket;
import com.alex.chat.Headers;
import com.alex.chat.Link;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;


/**
 * This additional class is a chat-client simulator for the testing process.
 */
public class ClientSimulatorTask implements Callable<String> {
    private DataPacket dataPacketByServer;
    private volatile boolean isImmitationError;
    private CountDownLatch latch;
    private int port;
    public ClientSimulatorTask(CountDownLatch latch,boolean isImmitError,int port){
        this.isImmitationError=isImmitError;
        this.latch=latch;
        this.port=port;
    }
    void startCommunication(Socket socket) throws IOException, ClassNotFoundException {
        try (Link link = new Link(socket)) {
            while (true) {
                dataPacketByServer = link.readData();
                switch (dataPacketByServer.getHeader()) {
                    case WHO_IS -> {
                        if(isImmitationError) {
                            link.writeData(new DataPacket(Headers.EXCHANGE, "I am a bad client-immitation'"));
                        }
                        else{
                            link.writeData(new DataPacket(Headers.I_AM_HERE, "I am a good client-immitation'"));
                        }

                    }
                    case I_SEE_YOU,ERROR,WARNING -> {
                        link.writeData(new DataPacket(Headers.EXIT, "I want to exit"));
                        return;
                    }
                }
            }
        }
    }
    @Override
    public String call(){
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            startCommunication(new Socket("127.0.0.1", port));
            return dataPacketByServer.getBody();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

