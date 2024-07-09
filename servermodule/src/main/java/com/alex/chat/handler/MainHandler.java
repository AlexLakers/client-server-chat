package com.alex.chat.handler;

import com.alex.chat.Headers;
import com.alex.chat.Link;
import com.alex.chat.DataPacket;
import com.alex.chat.utill.Utill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class contains the main functional for the handling process of the data packets from the chat client.
 * It implements {@link Runnable Runnable} for use and execution in the {@link java.util.concurrent.ThreadPoolExecutor executor}.
 * By the way this handler executes in one thread of the main thread pool.
 */
public class MainHandler implements Runnable {
    private Socket socket;
    private static final Logger logger = LoggerFactory.getLogger(MainHandler.class);
    private static ConcurrentHashMap<String, Link> linksMap = new ConcurrentHashMap<>();

    /**
     * Creates a new instance of MainHandler by {@link Socket socket} between the chat server and the chat client.
     * @param socket socket between the server and client.
     */
    public MainHandler(Socket socket) {
        this.socket = socket;
    }

    /**
     * Performs creating a new {@link Link link} by socket between the chat server and the chat client.
     * After it occurs the handling process of hello data packets(authentication).
     * The Hello algorithm is described in the {@link MainHandler#handleHelloDataPacket(Link) handleHelloDataPacket}.
     * The result of previous step is login.It and socket are used for the handling main data packets that's described
     * in the {@link MainHandler#handleMainDataPacket(Link, String) handleMainDataPacket}.
     * When the client left the chat then occurs the removing process of the current client from {@link MainHandler#linksMap linksMap}.
     * The IOException or ClassNotFoundException can be thrown during the reading or the writing data packets(Serialization,Deserialization),
     * or other IO errors have been detected.
     */

    @Override
    public void run() {
        String login="";
        try (Link link = new Link(socket)){
            login = handleHelloDataPacket(link);
            String infoMessage=String.format("The client[%1$s] with login[%2$s] is authorised success.",socket.getRemoteSocketAddress(),login);
            logger.info(infoMessage);
            Utill.writeString(infoMessage);
            handleMainDataPacket(link,login);

        } catch (IOException|ClassNotFoundException e) {
            String errMessage=String.format("An error of handling data packets from[%1$s]:[%2$s]",socket.getRemoteSocketAddress(),e.getMessage());
            logger.error(errMessage);
            Utill.writeString(errMessage);
        }
        removeClientLink(login);
        String exitMessage=String.format("The client[%1$s] left this chat",login);
        logger.debug("The client '{}' is removed from the linksMap",login);
        logger.info(exitMessage);
        Utill.writeString(exitMessage);

    }
    private void notifyAllClientsWithoutSender(DataPacket mainDataPacket, String senderName) throws IOException{
        logger.debug("The notification all the clients process with DataPacket[{}],sender[{}]",mainDataPacket,senderName);
        for (var entry : linksMap.entrySet()) {
            String currName = entry.getKey();
            Link currLink = entry.getValue();//РўРѕС‚ РјРѕР¶РЅРѕ С‡С‚Рѕ С‚Рѕ С‚РёРїР° СЃС‚СЂРёРјР°
            if (!currName.equals(senderName)) {
                currLink.writeData(mainDataPacket);
                logger.debug("The client[{}] sent the data packet{} to client[{}]",senderName,mainDataPacket,currName);
            }
        }
    }

    private void handleMainDataPacket(Link link, String senderName) throws IOException, ClassNotFoundException {
        logger.info("The main communication process  for the client[{}] with address[{}] is started",senderName,link.getSocket().getRemoteSocketAddress());
        while (true) {
            DataPacket mainDataPacket = link.readData();
            logger.debug("The data packet[{}] from{}",mainDataPacket,senderName);
            switch (mainDataPacket.getHeader()){
                case EXCHANGE -> {
                    notifyAllClientsWithoutSender(mainDataPacket,senderName);
                }
                case EXIT -> {
                    String exitMessage=senderName.concat(" left this chat");
                    logger.info(exitMessage);
                    notifyAllClientsWithoutSender(new DataPacket(Headers.WARNING,exitMessage),senderName);
                    return;
                }
                default -> {
                    String errorMessage= String.format("An incorrect header is detected in the data packet from[%1$s]",senderName);
                    logger.error(errorMessage);
                    Utill.writeString(errorMessage);
                    return;
                }
            }
        }
    }

    private void removeClientLink(String login) {
        logger.debug("The removing client[{}] process",login);
        linksMap.remove(login);
    }

    /**
     * Returns the chat client login by {@link Link link} between the chat server and the current user.
     * The chat client login is a result of some algorithm that's presented bellow.
     * The chat server sends the 'WHO_IS' data packet to the connected the chat client.
     * After it occurs waiting of getting received data packet from the chat client.
     * If it is contains a header 'I_AM_HERE' then it should contain body as a login.
     * Then if the login is not valid occurs slip one iteration of the polling cycle and begins the sending process a new 'WARNING' data packet from the chat server to the chat client.
     * Else the chat server send to chat client a new data packet with header 'I_SEE_YOU' and body as a login.
     * @param link the link between the server and the current user.
     * @return the login of the authenticated client.
     * @throws IOException if occurs some problems during the reading or the writing data packets into IO stream(Serialization,Deserialization),
     *       or other IO errors have been detected.
     * @throws ClassNotFoundException if during deserialization process class {@link DataPacket DataPackets} is not found in the compilation level.
     */
    private String handleHelloDataPacket(Link link) throws IOException, ClassNotFoundException {
        logger.info("The hello communication process for client[{}]",link.getSocket().getRemoteSocketAddress());
        while (true) {
            link.writeData(new DataPacket(Headers.WHO_IS));
            logger.debug("Sent a new hello data packet");
            DataPacket dataPacket = link.readData();
            logger.debug("Received the answered data packet[{}]",dataPacket);
            //answer
            if (dataPacket.getHeader().equals(Headers.I_AM_HERE)) {
                String login = dataPacket.getBody();
                if (login.isEmpty() || linksMap.containsKey(login)) {
                    String warnMessage = String.format("The login[%1$s] is empty or already exists",login);
                    //log
                    logger.warn(warnMessage);
                    link.writeData(new DataPacket(Headers.WARNING, warnMessage));
                    logger.debug("A new data packet with header[{}] has been sent to sender[{}]",Headers.WARNING,link.getSocket().getRemoteSocketAddress());
                    continue;
                }
                linksMap.put(login, link);
                logger.debug("The client[{}] has been added to the linksMap",login);
                link.writeData(new DataPacket(Headers.I_SEE_YOU, login));
                logger.debug("A new data packet with header[{}] has been sent to sender[{}]",Headers.I_SEE_YOU,login);
                logger.debug("The result of hello  communication process is [{}]",login);
                return login;
            }
            else{
                String warnMessage ="The data packet from a chat-client contains incorrect header";
                link.writeData(new DataPacket(Headers.WARNING,warnMessage));
                logger.warn(warnMessage);
            }
        }
    }

}

