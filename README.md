# client-server-chat
This is a multi-module and multi-threaded client-server chat with a queue of connections.
## Description
This is a client-server chat which allows some different people to communicate with each other from anywhere in the world,by the way you router should has static white IP address.
As you see in a simple description the server part use a queue to limit a count of the client connections.

## How does it works?

First of all,I want to say that all the communication process between the chat client and the chat server occurs using a conditional data packets(cdp) which contains the header and the body.

So...you need to set all the neccessary properties before the chat server running.It located by the '~\client-server-chat\servermodule\src\main\resources\config.properties' and includes the fallow properties:

```properties
#The listening port.
server.port=5002
#Count of one-time main client connections.
pool.main.thread.count=10
#Count of the waiting client connections(capacity of links queue).
connections.queue.capacity=20
#Count of one-time error clients connections.
pool.error.thread.count=2
```

Of caurse,you can experiment with these props to find you own decision,but I want to give you some example using default values.

So,you entered default values and what will happen then? Then the chat server opens for listening the port number is 5002 and will be ready to provide the communication process

between 10 chat clients.The communication process occurs in two stages using sockets-technology and a conditional data packet which contains the header and body.

### The hello stage(authentication):

Every chat client should finish this stage before the main communication stage will be started.When the socket-connection between the chat server and the chat client is established then the server sends 'WHO_IS' data packet and waits an answered 'I_AM_HERE' data packet wich contains the client login.After it the chat server send the 'I_SEE_YOU' data packet to the chat client, accepting and finalizing the hello stage.

By the way, the chat client enters the fallowing paraps in the 'clientmodule' console:the server ip address, server port and the client login.

### The main stage(exchanging):

On this stage occurs the main communication process.The chat client enters some message in the clientmodule console and sends the 'EXCHANGE' data packet with entered message.

The chat server receives this data packet and sends all the chat clients the same data packet.

## About the project stucture and buildnig:

As a building system I used 'Maven'.Also I used multi-module project structure to separate 'chat' into 'clientmodule' and 'servermodule', in addition I addded 'commonmodule' to storage common packages.Thus every module has his own 'POM.xml' and one a root POM.xml.

## A several words about the logging system:

I used 'slf4j' and his implementation 'log4j2' to organize the logging process in the app.

The 'clientmodule' and 'servermodule' have different and independent logging systems.The appenders and the loggers are deffined in the ~/resources/log4j2.xml.

Bellow you can see the ref to this lib.

> [!IMPORTANT]
>The additional libs for tests and logging:
> - "Apache Log4j SLF4J 2.0 Binding" https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-slf4j2-impl
> - "Mockito JUnit Jupiter" https://mvnrepository.com/artifact/org.mockito/mockito-junit-jupiter
> - "JUnit Jupiter Engine" https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-engine
> - "JUnit Jupiter Params" https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-params
> - "Mockito Core" https://mvnrepository.com/artifact/org.mockito/mockito-core

