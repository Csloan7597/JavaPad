package javapad.server.interfaces;

import javapad.shared.utils.JavaPadMessage;

public interface IClientConnection
{
    void sendMessage(JavaPadMessage jpm);

    void disconnect();

    int getId();
}
