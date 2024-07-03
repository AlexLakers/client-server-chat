package com.alex.chat;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * This class describes some link between the chat server and the chat client.
 * Also it contains the functions for serialization(writing) and deserialization(reading) the {@link DataPacket data packet}.
 */
public class Link implements AutoCloseable{
    //Serialiese
    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream objectOutputStream;

    private Lock inLock;
    private Lock outLock;

    private Socket socket;

    public Socket getSocket() {
        return socket;
    }

    /**
     * Creates a new Link between the chat server and the chat client by socket.
     * @param socket a socket between a client and a server
     * @throws IOException if some IO error has been detected during creating new streams.
     */
    public Link(Socket socket)throws IOException{
        this.socket=socket;
        this.inLock =new ReentrantLock();
        this.outLock =new ReentrantLock();
        this.objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
        this.objectInputStream=new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Writes a {@link DataPacket dataPacket} to the corresponding socket stream.
     * @param dataPacket a data packet for writing process.
     * @throws IOException if some IO error has been detected during writing into the corresponding socket stream.
     */
    public void writeData(DataPacket dataPacket)throws IOException {
        outLock.lock();
        try {
            objectOutputStream.writeObject(dataPacket);
        }
        finally {
            outLock.unlock();
        }
    }

    /**
     * Reads a {@link DataPacket dataPacket} from the corresponding socket stream.
     * @return a data packet from the socket stream
     * @throws IOException if some IO error has been detected during reading from the corresponding socket stream.
     * @throws ClassNotFoundException if during deserialization process class {@link DataPacket DataPackets} is not found in the compilation level.
     */

    public DataPacket readData()throws IOException,ClassNotFoundException {
        inLock.lock();
        try {
            return (DataPacket) objectInputStream.readObject();
        }
        finally {
            inLock.unlock();
        }
    }

    @Override
    public void close()throws IOException{
        objectInputStream.close();
        objectOutputStream.close();
        socket.close();
    }

}

