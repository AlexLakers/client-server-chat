package com.alex.chat;



import com.alex.chat.utill.Utill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class describes client part of chat, it allows some user to connect to the server and use communicate functions to communication with other users using this class.
 * @see "servermodule"
 * @see "commonmodule"
 */

public class Client {
    private Link link;
    private Lock lock;
    private Condition condition;
    private final static Logger logger= LoggerFactory.getLogger(Client.class);

    /**
     * Creates new instance of Client with {@link InetAddress inetAddres} and port.
     * These network parameters need to connect to the server.
     * @param address server ip address
     * @param port server port
     * @throws IOException if connection parameters is not valid or other IO errors have been detected.
     */
    public Client(InetAddress address, int port)throws IOException{
        logger.debug("The creation process a new client with address[{}],port[{}]",address,port);
        Socket socket = new Socket(address,port);
        logger.debug("The socket with [{}] is has been opened",socket.getRemoteSocketAddress());
        this.link=new Link(socket);
        this.lock=new ReentrantLock();
        this.condition =lock.newCondition();
    }


    public static void main(String[] args) {
        try {
            InetAddress inetAddress=Utill.readIpAddress();
            int port =Utill.readInt();
            new Client(inetAddress,port).start();

        } catch (IOException | IllegalArgumentException e) {
            logger.error("An error has been detected:{}",e.getMessage());
        }
    }

    /**
     * Performs creating a new {@link ReceiveHandler receiveHandle} for the handling receive data packets and starts it.
   */
    public void start() {
        ReceiveHandler receiveHandler =new ReceiveHandler(link,lock, condition);

    }

}
