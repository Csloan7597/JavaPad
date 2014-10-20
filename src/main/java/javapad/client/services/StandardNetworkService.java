package javapad.client.services;

import javapad.shared.utils.JavaPadMessage;

import java.util.function.Function;

/**
 * Created by conor on 20/10/2014.
 */
public class StandardNetworkService implements JavaPadNetworkService {

    @Override
    public void setMessageCallback(Function<JavaPadMessage, Void> callback) {

    }

    @Override
    public void connect(String IP, char[] pass, int port) {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void sendMessage(JavaPadMessage message) {

    }
}
