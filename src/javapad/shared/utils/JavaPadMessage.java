package javapad.shared.utils;

import java.io.Serializable;

public class JavaPadMessage implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 440191911870830684L;

    // These ENUMS represent the types of message that can be sent.
    // Client can send all but RESPONSE, server sends DATA, DISCONNECT, RESPONSE
    public enum MessageType
    {
        CONNECT, CONNECT_DENIED, DISCONNECT, CONTROL_REQUEST, CONTROL_RELEASE, CONTROL_GRANTED, CONTROL_DENIED, SERVER_RESPONSE_ERROR, SEND_DATA;
    }

    private final MessageType type;
    private final String messageBody;

    // public constructor
    public JavaPadMessage(MessageType type, String messageBody)
    {
        this.type = type;
        this.messageBody = messageBody;
    }

    public JavaPadMessage(MessageType type)
    {
        this.type = type;
        messageBody = "";
    }

    public MessageType getMessageType()
    {
        return type;
    }

    public String getMessageBody()
    {
        return messageBody;
    }

}
