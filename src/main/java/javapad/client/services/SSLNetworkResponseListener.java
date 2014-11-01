package javapad.client.services;

import javapad.shared.utils.JavaPadMessage;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by conor on 26/10/2014.
 */
public class SSLNetworkResponseListener implements Runnable {

    /** Logger for this class */
    private static Logger LOGGER = Logger.getLogger(SSLNetworkResponseListener.class
            .getSimpleName());

    private ObjectInputStream in;
    private Function<JavaPadMessage, Void> callback;
    private SSLSocket mySSLSocket;

    public void setSocket(SSLSocket socket) {
        this.mySSLSocket = socket;
    }

    public void setInputStream(ObjectInputStream in) {
        this.in = in;
    }

    public void setCallback(Function<JavaPadMessage, Void> callback) {
        this.callback = callback;
    }

    public void handleMessage(JavaPadMessage jpm)
    {
        callback.apply(jpm);
    }

    @Override
    public void run()
    {
        try
        {
            while (!mySSLSocket.isClosed())
            {
                // Listen for incoming object messages
                final JavaPadMessage jpm = (JavaPadMessage) in.readObject();
                handleMessage(jpm);
            }
        } catch (IOException | ClassNotFoundException e)
        {
            LOGGER.severe("Error in listener thread: " + e.getMessage());
            LOGGER.info("Connection Closed");
            handleMessage(new JavaPadMessage(JavaPadMessage.MessageType.DISCONNECT));
        }
    }

}
