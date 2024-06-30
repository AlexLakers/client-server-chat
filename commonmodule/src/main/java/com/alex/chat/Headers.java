package com.alex.chat;

/**
 * This enum contains the constants of types the {@link DataPacket dataPacket} which is transmitted between the chat server and
 * the chat client.
 * The hello stage:
 * <strong>'WHO_IS'</strong> chat server send it when some new chat client link has been detected.
 * <strong>'I_AM_HERE'</strong> it is the chat client answer to the 'WHO_IS' question.
 * <strong>'I_SEE_YOU'</strong> it is the chat server answer to the 'I_AM_HERE' statement.
 * The main stage:
 * <strong>'EXCHANGE'</strong> after  the hello stage occurs exchange data packets between the chat server and chat client.
 * <strong>'EXIT'</strong> it allow some chat client to leave the chat.
 * Errors and warnings:
 * <strong>'WARNING','ERRORS'</strong> warning and error.
 */
public enum Headers {
    WHO_IS,
    I_AM_HERE,
    I_SEE_YOU,

    EXCHANGE,
    ERROR,
    WARNING,
    EXIT
}


