package javapad.client.services;

import java.util.function.Function;

/**
 * Created by conor on 25/10/2014.
 */
public interface ChatNetworkService {

    public void connect();

    public void disconnect();

    public void setMessageCallback(Function<String, Void> callback);

    public void sendMessage(String message);

    public boolean isConnected();
}
