package javapad.client.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javapad.client.interfaces.IConnectionManager;
import javapad.client.interfaces.IResponseListener;
import javapad.shared.utils.JavaPadMessage;
import javapad.shared.utils.JavaPadMessage.MessageType;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class JPConnectionManager extends Observable implements
        IConnectionManager
{
    /** Logger for this class */
    private static Logger LOGGER = Logger.getLogger(JPConnectionManager.class
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

    public JPConnectionManager(Observer e)
    {
        addObserver(e);
        LOGGER.info("Added the GUI as an observer in the constructor");
    }

    @Override
    public void connect(final String IP, final char[] pass, final int port)
            throws IOException
    {
        LOGGER.info("Connecting to the Server");
        mySSLSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        mySSLSocket = (SSLSocket) mySSLSocketFactory.createSocket(IP, port);
        mySSLSocket.setEnabledCipherSuites(enabledCipherSuites);

        out = new ObjectOutputStream(mySSLSocket.getOutputStream());
        in = new ObjectInputStream(mySSLSocket.getInputStream());

        final JavaPadMessage jpm = new JavaPadMessage(MessageType.CONNECT,
                Arrays.toString(pass));
        sendJavaPadMessage(jpm);

        // Now open the listener to get a response
        listenerThread = new Thread(new ResponseListener());
        listenerThread.start();
    }

    @Override
    public void disconnect() throws IOException
    {
        // If we're in a state where we can send a message
        if (mySSLSocket.isConnected() && !mySSLSocket.isClosed())
        {
            final JavaPadMessage jpm = new JavaPadMessage(
                    MessageType.DISCONNECT, "");
            sendJavaPadMessage(jpm);
        }
    }

    @Override
    public void sendJavaPadMessage(JavaPadMessage message) throws IOException
    {
        LOGGER.info("Sending a JavaPad message from client: "
                + message.getMessageType());
        out.writeObject(message);
    }

    public void closeConnection()
    {
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

    private class ResponseListener implements Runnable, IResponseListener
    {

        @Override
        public void handleMessage(JavaPadMessage jpm)
        {
            setChanged();
            notifyObservers(jpm);
            if (jpm.getMessageType() == MessageType.DISCONNECT)
            {
                closeConnection();
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
                handleMessage(new JavaPadMessage(MessageType.DISCONNECT));
            } finally
            {
                closeConnection();
            }

        }
    }

}
