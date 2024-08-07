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
        logger.info("Client is started");
        try {
            Utill.writeString("Enter server IP address");
            InetAddress inetAddress=Utill.readIpAddress();
            Utill.writeString("Enter server port");
            int port =Utill.readInt();
            logger.info("The client entered '{}:{}'",inetAddress.getHostAddress(),port);
            new Client(inetAddress,port).start();

        } catch (IOException | IllegalArgumentException e) {
            logger.error("An error has been detected:{}",e.getMessage());
        }
    }

    /**
     * Performs creating a new {@link ReceiveHandler receiveHandle} for the handling receive data packets and starts it.
     * Then occurs waiting main-thread for authentication process in receiveHandler-thread.
     * The waiting process occurs using {@link Lock lock} and {@link Condition condition}.
     * After it occurs the sending process some messages from client to server,
     * if {@link ReceiveHandler#isSuccessConnection() isSuccessConnection} return true.
     * If some error has been detected then it will handle in try-catch block.
     * @see IOException IOException
     * @see InterruptedException InterruptedException
     */
    public void start() {
        ReceiveHandler receiveHandler =new ReceiveHandler(link,lock, condition);
        receiveHandler.setDaemon(true);
        receiveHandler.start();
        try {
            lock.lock();
            condition.await();
            logger.info("{} waited for the authentication process",link.getSocket().getLocalSocketAddress());
            lock.unlock();

            logger.info("{} started the main communication process",receiveHandler.getLogin());

            while (receiveHandler.isSuccessConnection()) {

                String body = Utill.readLine();
                logger.debug("The client has been entered message[{}]",body);
                if(body.equalsIgnoreCase("exit")){
                    link.writeData(new DataPacket(Headers.EXIT));
                    receiveHandler.setSuccessConnection(false);
                    logger.debug("The field 'isSuccessConnection'={}", receiveHandler.isSuccessConnection());
                }
                else {
                    String bodyWithLogin= receiveHandler.getLogin() + ":" + body;
                    link.writeData(new DataPacket(Headers.EXCHANGE, bodyWithLogin));
                    logger.debug("A new data packet with header[{}],body[{}] has been sent to server[{}]",Headers.EXCHANGE,bodyWithLogin,link.getSocket().getRemoteSocketAddress());
                }

            }
        } catch (IOException| InterruptedException e) {
            receiveHandler.setSuccessConnection(false);
            logger.debug("The field 'isSuccessConnection'={}", receiveHandler.isSuccessConnection());
            logger.error("The main client thread was interrupted or IO error has been detected:{}",e.getMessage());
        }
        logger.info("{} left this chat", receiveHandler.getLogin());
    }

}



