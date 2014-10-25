package javapad.client.services;

import javapad.client.old.interfaces.IResponseListener;
import javapad.shared.utils.JavaPadMessage;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by conor on 20/10/2014.
 */
public class SSLNetworkService implements JavaPadNetworkService {

    /** Logger for this class */
    private static Logger LOGGER = Logger.getLogger(SSLNetworkService.class
            .getSimpleName());

    // SSL socket objects
    private SSLSocketFactory mySSLSocketFactory;
    private SSLSocket mySSLSocket;
    final String[] enabledCipherSuites =
            { "SSL_DH_anon_WITH_RC4_128_MD5" };

    // Reader and writer objects
    ObjectInputStream in;
    ObjectOutputStream out;
    Thread listenerThread;
    private Function<JavaPadMessage, Void> messageCallback;


    @Override
    public void setMessageCallback(Function<JavaPadMessage, Void> callback) {
        this.messageCallback = callback;
    }

    @Override
    public void connect(String IP, char[] pass, int port) {
        try {
            LOGGER.info("Connecting to the Server");
            mySSLSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            mySSLSocket = (SSLSocket) mySSLSocketFactory.createSocket(IP, port);
            mySSLSocket.setEnabledCipherSuites(enabledCipherSuites);

            out = new ObjectOutputStream(mySSLSocket.getOutputStream());
            in = new ObjectInputStream(mySSLSocket.getInputStream());

            final JavaPadMessage jpm = new JavaPadMessage(JavaPadMessage.MessageType.CONNECT,
                    Arrays.toString(pass));

            sendMessage(jpm);

            // Now open the listener to get a response
            listenerThread = new Thread(new ResponseListener());
            listenerThread.start();
        } catch (Exception e) {
            LOGGER.severe("Connection issue");
        }
    }

    @Override
    public void disconnect() {
        try
        {
            if (mySSLSocket != null)
            {
                mySSLSocket.close();
            }
        } catch (final IOException e)
        {
            LOGGER.severe("Error closing connection" + e.getMessage());
        }
    }

    @Override
    public void sendMessage(JavaPadMessage message) {
        LOGGER.info("Sending a JavaPad message from client: "
                + message.getMessageType());
        try {
            out.writeObject(message);
        } catch (IOException e) {
            LOGGER.severe("Error sending message");
        }
    }

    private class ResponseListener implements Runnable, IResponseListener
    {

        @Override
        public void handleMessage(JavaPadMessage jpm)
        {
            messageCallback.apply(jpm);
            if (jpm.getMessageType() == JavaPadMessage.MessageType.DISCONNECT)
            {
                disconnect();
            }
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
            } finally
            {
                disconnect();
            }

        }
    }
}
