package com.alex.chat;

import com.alex.chat.utill.Utill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * This class contains the main functional for the handling process of received data packets(messages) from server.
 * It extends {@link Thread Thread} and allows some client to handle messages in a one thread.
 * @see Client Client
 * @see "commonmodule"
 */
public class ReceiveHandler extends Thread {
    private Link link;
    private String login;
    private Lock lock;
    private Condition condition;

    public String getLogin() {
        return login;
    }

    public void setSuccessConnection(boolean successConnection) {
        isSuccessConnection = successConnection;
    }

    public boolean isSuccessConnection() {
        return isSuccessConnection;
    }
    private final static Logger logger = LoggerFactory.getLogger(ReceiveHandler.class);

    private volatile boolean isSuccessConnection;

    /**
     * Creates a new ReceiveHandler with link between a client and a server,
     * also for creating process needs {@link Lock lock} and {@link Condition condition}.
     * @param link link between a server and a client.
     * @param lock lock for the synchronizing process
     * @param condition to control the synchronising process between several thread.
     */
    public ReceiveHandler(Link link, Lock lock, Condition condition) {
        this.link = link;
        this.isSuccessConnection = false;
        this.condition = condition;
        this.lock=lock;
    }

    @Override
    public void run() {
        try {
            handleReceiveDataPacket();

        } catch (IOException | ClassNotFoundException e) {
            isSuccessConnection = false;
            logger.error(e.getMessage());
        }
        logger.debug("The method 'run' is finished");
    }

    /**
     * Performs handling process of received{@link DataPacket data packets} from server.
     * The data packets can be different types.
     * If the type of data packet corresponds 'WHO_IS' then some user should enter the login , after it
     * occurs the sending process new data packet 'I_AM_HERE' with entered login.
     * If the type of data packet corresponds 'I_SEE_YOU' then the server apply the login  and user is ready to chat.
     * If the type of data packet corresponds 'EXCHANGE','WARNING' and 'EXIT' then occurs showing process of the body data packet
     * into console.
     * Also server can form 'ERROR' message when something went wrongly(user entered incorrect or exist login and so on).
     * And finally, if the type of data packets is unsupported when cycle of polling will finish.
     * @throws IOException if occurs some problems during the reading or the writing data packets into IO stream(Serialization,Deserialization),
     * or other IO errors have been detected.
     * @throws ClassNotFoundException if during deserialization process class {@link DataPacket DataPackets} is not found in the compilation level.
     *
     */
    private void handleReceiveDataPacket() throws IOException, ClassNotFoundException {
        logger.info("The handling of the received data packets is started");
        while (true) {
            DataPacket dataPacket = link.readData();
            logger.debug("The data packet[{}] from server[{}]",dataPacket,link.getSocket().getRemoteSocketAddress());
            switch (dataPacket.getHeader()) {
                case WHO_IS -> {
                    login = Utill.readLineWithMessage("Enter your login:");
                    logger.info("{} entered login[{}]",link.getSocket().getLocalSocketAddress(),login);
                    link.writeData(new DataPacket(Headers.I_AM_HERE, login));
                    logger.debug("A new hello data packet with header[{}],body[{}] has been sent to server[{}]",Headers.I_AM_HERE,login,link.getSocket().getRemoteSocketAddress());
                }
                case I_SEE_YOU -> {
                    isSuccessConnection = true;
                    lock.lock();
                    condition.signalAll();
                    logger.debug("Wake up of the waiting main client-thread");
                    logger.info("The authentication is successful-login[{}]",login);
                    lock.unlock();

                }
                case WARNING, EXCHANGE, EXIT -> {
                    Utill.writeString(dataPacket.getBody());
                }

                default -> {
                    logger.error("Unsupported header[{}] in the packet",dataPacket.getHeader());
                    return;
                }
            }

        }


    }
}
