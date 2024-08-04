package com.alex.chat.testdata;

import com.alex.chat.DataPacket;
import com.alex.chat.Headers;
import com.alex.chat.Link;
import com.alex.chat.utill.Utill;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * This additional class is a chat-server simulator for the testing process.
 */
public class ServerSimulatorTask implements Callable<String>{
    private DataPacket dataPacketByClient;
    private volatile boolean isImmitationError;
    private CountDownLatch latch;
    private int port;
    public ServerSimulatorTask(CountDownLatch latch,boolean isImmitError,int port){
        this.isImmitationError=isImmitError;
        this.latch=latch;
        this.port=port;
    }
    void startCommunication() throws IOException, ClassNotFoundException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            latch.countDown();
            try (Link link = new Link(serverSocket.accept())) {
                if(isImmitationError){
                    link.writeData(new DataPacket(Headers.I_AM_HERE));
                }
                else {
                    link.writeData(new DataPacket(Headers.WHO_IS));
                }
                while(true){
                    dataPacketByClient = link.readData();
                    switch (dataPacketByClient.getHeader()) {
                        case I_AM_HERE -> {
                            link.writeData(new DataPacket(Headers.I_SEE_YOU, dataPacketByClient.getBody()));
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public String call() {
        latch.countDown();
        try {
            startCommunication();
            return dataPacketByClient.getBody();
        } catch (IOException | ClassNotFoundException e) {
            Utill.writeString(e.getMessage());
        }
        return "error";
    }
}
