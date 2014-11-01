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
    private final SSLNetworkResponseListener responseListener;

    // SSL socket objects
    private SSLSocketFactory mySSLSocketFactory;
    private SSLSocket mySSLSocket;
    private final String[] enabledCipherSuites =
            { "SSL_DH_anon_WITH_RC4_128_MD5" };

    // Reader and writer objects
    ObjectInputStream in;
    ObjectOutputStream out;
    Thread listenerThread;

    public SSLNetworkService(SSLSocketFactory socketFactory, SSLNetworkResponseListener responseListener) {
        this.mySSLSocketFactory = socketFactory;
        this.responseListener = responseListener;
    }

    @Override
    public void setMessageCallback(Function<JavaPadMessage, Void> callback) {
        this.responseListener.setCallback(callback);
    }

    @Override
    public void connect(String IP, char[] pass, int port) {
        try {
            LOGGER.info("Connecting to the Server");
            mySSLSocket = (SSLSocket) mySSLSocketFactory.createSocket(IP, port);
            mySSLSocket.setEnabledCipherSuites(enabledCipherSuites);

            out = new ObjectOutputStream(mySSLSocket.getOutputStream());
            in = new ObjectInputStream(mySSLSocket.getInputStream());

            final JavaPadMessage jpm = new JavaPadMessage(JavaPadMessage.MessageType.CONNECT,
                    Arrays.toString(pass));

            sendMessage(jpm);

            // Now open the listener to get a response
            responseListener.setSocket(mySSLSocket);
            responseListener.setInputStream(in);
            listenerThread = new Thread(responseListener);
            listenerThread.start();

        } catch (Exception e) {
            LOGGER.severe("Connection issue " + e.getMessage() );
            e.printStackTrace();
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


}
