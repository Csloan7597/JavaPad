package javapad.client.old.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javapad.client.old.interfaces.IConnectionManager;
import javapad.client.old.interfaces.IResponseListener;
import javapad.shared.utils.JavaPadMessage;
import javapad.shared.utils.JavaPadMessage.MessageType;


public class NonBlockingConnectionManager extends Observable implements
        IConnectionManager
{
    /** Logger for this class */
    private static Logger LOGGER = Logger.getLogger(JPConnectionManager.class
            .getSimpleName());

    SocketChannel socketChannel;
    ObjectInputStream in;
    ObjectOutputStream out;

    private Thread listenerThread;

    public NonBlockingConnectionManager(Observer e)
    {
        addObserver(e);
        LOGGER.info("Added the GUI as an observer in the constructor");
    }

    public void connect(String IP, char[] pass, int port) throws IOException
    {
        LOGGER.info("In the connect method");

        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(true);
        socketChannel.connect(new InetSocketAddress(IP, 12111));

        in = new ObjectInputStream(socketChannel.socket().getInputStream());
        out = new ObjectOutputStream(socketChannel.socket().getOutputStream());

        final JavaPadMessage jpm = new JavaPadMessage(MessageType.CONNECT,
                Arrays.toString(pass));
        sendJavaPadMessage(jpm);

        LOGGER.info("About to write object");
        out.writeObject(jpm);

        // Now open the listener to get a response
        listenerThread = new Thread(new ResponseListener());
        listenerThread.start();

    }

    public void disconnect() throws IOException
    {
        if (socketChannel != null)
        {
            socketChannel.close();
        } else
        {
            throw new IOException("You are not connected to this server");
        }

    }

    public void sendJavaPadMessage(JavaPadMessage message) throws IOException
    {
        if (out != null)
        {
            out.writeObject(message);
        } else
        {
            throw new IOException("You are not connected to this server");
        }

    }

    private class ResponseListener implements Runnable, IResponseListener
    {

        public void handleMessage(JavaPadMessage jpm)
        {
            LOGGER.info("Handling a response");
            setChanged();
            notifyObservers(jpm);
        }


        public void run()
        {
            try
            {
                LOGGER.info("Listening to object stream");
                // Listen for incoming object messages
                final JavaPadMessage jpm = (JavaPadMessage) in.readObject();
                handleMessage(jpm);

            } catch (IOException | ClassNotFoundException e)
            {
                LOGGER.severe("Error in listener thread: " + e.getMessage());
            }
        }
    }

}
