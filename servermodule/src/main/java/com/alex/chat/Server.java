package com.alex.chat;


import com.alex.chat.executor.ExecutorFactory;
import com.alex.chat.handler.MainHandler;
import com.alex.chat.utill.Utill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * This class describes server part of chat, it allows to apply the connection from clients of chat.
 * Also it needs to handle different data packets from them and form error if it is necessary.
 *
 */
public class Server {
    private final static Logger logger= LoggerFactory.getLogger(Server.class);
    private static final Properties initProps;

    /**
     * This static block allows to get 'config.properties' that contains main properties for the correct running process of the server.
     * If something went wrongly,for example the file is not found then occurs IOException in try-catch block.
     * @see IOException IOException
     * @see Properties Properties
     * @see ParamsServer ParamsServer
     */
    static{
        initProps=new Properties();
        try(InputStream is = Server.class.getResourceAsStream("/config.properties")){
            initProps.load(is);
            logger.info("The file 'config.properties' is found and loaded");
        }
        catch(IOException e){
            logger.error("An error in the file 'config.properties':[{}]",e.getMessage());
        }
    }
    private ParamServer paramServer;
    private ExecutorService mainExecutorService;


    /**
     * Creates a new instance of Server with {@link ParamServer paramsServer} which contains all the necessary params for the server.
     * @param paramServer contains the main params for the server.
     */
    public Server(ParamServer paramServer){
        this.paramServer=paramServer;
        this.mainExecutorService= ExecutorFactory.createMainExecutor(paramServer.countMainThread(),paramServer.capQueue());
    }


    public static void main( String[] args ) {
        ParamServer props=ParamServerParser.tryParseProperties(initProps);
        logger.info("The property file is parsed successful");
        new Server(props).start();
        String startMessage="Server is started";
        logger.info(startMessage);
        Utill.writeString(startMessage);
    }

    /**
     * Performs creating a new {@link ServerSocket serverSocket} and waits for the connection some client.
    */
    public void start(){
        try(ServerSocket serverSocket=new ServerSocket(paramServer.port())) {
            logger.info("The server port {} is opened",paramServer.port());
            while (true) {
                Socket socket = serverSocket.accept();
                logger.info("An unauthorized client[{}] is connected",socket.getRemoteSocketAddress());
                Future mainTask = mainExecutorService.submit(new MainHandler(socket));

            }
        } catch (IOException e) {
            logger.error("A server error has been detected:[{}]",e.getMessage());
        }
    }
}

