package javapad.client.services;

import javapad.client.models.ConnectionState;
import javapad.shared.utils.JavaPadMessage;

import java.util.function.Function;

/**
 * Created by conor on 20/10/2014.
 */
public interface JavaPadNetworkService {

    public void setMessageCallback(Function<JavaPadMessage, Void> callback);

    public void connect(final String IP, final char[] pass, final int port);

    public void disconnect();

    public void sendMessage(JavaPadMessage message);
}
