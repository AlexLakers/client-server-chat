package com.alex.chat.handler;

import com.alex.chat.DataPacket;
import com.alex.chat.Headers;
import com.alex.chat.Link;
import com.alex.chat.utill.Utill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

/**
 *This class describes the handler of the error link from chat client.
 *It implements {@link Runnable Runnable} for use and execution in the {@link java.util.concurrent.ThreadPoolExecutor executor}.
 *By the way this handler is executed in one thread of the error thread pool.
 */
public class ErrorHandler implements Runnable{
    private Socket socket;
    private String message;
    private final static Logger logger = LoggerFactory.getLogger(ErrorHandler.class);
    /**
     * Creates a new instance of ErrorHandler using {@link Socket socket} between the server and the client and error message.
     * @param socket socket between the server and client.
     * @param message message for informing process.
     */
    public ErrorHandler(Socket socket,String message){
        this.socket=socket;
        this.message=message;
    }

    /**
     * Performs creating a new {@link Link link} by socket between the server and the client.
     * After it occurs the sending the {@link DataPacket data packet} with header='ERROR',body=message about error.
     * The IOException can be thrown during the writing data packets(Serialization) or other IO errors have been detected.
     */
    @Override
    public void run(){
        try(Link link = new Link(socket)){
            link.writeData(new DataPacket(Headers.ERROR,message));
            logger.debug("A new data packet with header[{}],body[{}] has been sent to client[{}]",Headers.ERROR,message,socket.getRemoteSocketAddress());
        }
        catch (IOException e){
            String errorMessage = String.format("An error of handling error link[%1$s]",e.getMessage());
            logger.error(errorMessage);
            Utill.writeString(errorMessage);
        }

    }

}
