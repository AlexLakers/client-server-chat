package com.alex.chat;


import java.io.Serializable;

/**
 * This class describes the data packet.
 * It's used during communication process between the chat server and the chat client.
 * It has header and body.Header field can be different and describes type of data packet.
 * What about body, it contains some data.
 */
public class DataPacket implements Serializable {
    private Headers header;
    private String body;
    public DataPacket(Headers header, String body)
    {
        this(header);
        this.body =body;
    }
    public DataPacket(Headers header){
        this.header=header;
    }

    public String getBody() {
        return body;
    }

    public void setHeader(Headers header) {
        this.header = header;
    }

    public Headers getHeader() {
        return header;
    }

    public void setBody(String body) {
        this.body = body;
    }
    @Override
    public String toString(){
        return String.format("[header:%1$s,body:%2$s]",header,body);
    }
}

