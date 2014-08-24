package javapad.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.logging.Logger;

import javapad.server.interfaces.IClientConnection;
import javapad.server.interfaces.IClientList;
import javapad.server.interfaces.IDataPublisher;
import javapad.server.interfaces.ILock;
import javapad.server.interfaces.INotifier;
import javapad.shared.utils.JavaPadMessage;
import javapad.shared.utils.JavaPadMessage.MessageType;

import javax.net.ssl.SSLSocket;


public class ClientConnection implements IClientConnection, Runnable
{
    /** Logger for this class */
    private static Logger LOGGER = Logger.getLogger(ClientConnection.class
            .getSimpleName());

    private final SSLSocket socket;
    private final int id;
    private final INotifier notifier;
    private final ILock lock;
    private final IDataPublisher publisher;

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final IClientList clientList;

    private final char[] expectedPassword;

    public ClientConnection(SSLSocket socket, int id, INotifier notifier,
            ILock lock, IDataPublisher publisher, IClientList clientList,
            char[] password)
    {
        this.socket = socket;
        this.id = id;
        this.notifier = notifier;
        this.lock = lock;
        this.publisher = publisher;
        this.clientList = clientList;
        this.expectedPassword = password;
    }

    @Override
    public synchronized void sendMessage(JavaPadMessage jpm)
    {
        try
        {
            if (socket != null && out != null)
            {
                out.writeObject(jpm);
            }
        } catch (final IOException e)
        {
            // LOG
        }
    }

    private void handleMessage(JavaPadMessage jpm)
    {
        JavaPadMessage message;
        switch (jpm.getMessageType())
        {
        case CONTROL_REQUEST:
            if (lock.getLock(id))
            {
                message = new JavaPadMessage(MessageType.CONTROL_GRANTED);
                notifier.sendToConsole("Control has been given to id: " + id);
                LOGGER.info("Control given to id: " + id);
            } else
            {
                message = new JavaPadMessage(MessageType.CONTROL_DENIED);
                notifier.sendToConsole("Control has been denied, id: " + id);
                LOGGER.info("Control denied, id: " + id);
            }
            sendMessage(message);
            break;

        case CONTROL_RELEASE:
            if (lock.releaseLock(id))
            {
                message = new JavaPadMessage(MessageType.CONTROL_RELEASE);
                notifier.sendToConsole("Control has been released by id: " + id);
                LOGGER.info("Control released, id: " + id);
            } else
            {
                message = new JavaPadMessage(MessageType.SERVER_RESPONSE_ERROR,
                        "Error: You attempted to release control and you don't have it.");
            }
            sendMessage(message);
            break;

        case SEND_DATA:
            if (lock.getLockOwnerId() == id)
            {
                publisher.publishData(id, jpm);
                notifier.sendToConsole("Data published by id: " + id);
                LOGGER.info("Data published by id: " + id);
            } else
            {
                message = new JavaPadMessage(MessageType.SERVER_RESPONSE_ERROR,
                        "Error: You can't send data if you don't have control");
                sendMessage(message);
            }
            break;

        case DISCONNECT:
            disconnect();
            LOGGER.info("Client has Disconnected, id: " + id);
            break;

        case CONNECT:
            connectionHandshake(jpm);
            break;
        default:
            break;
        }
    }

    private void connectionHandshake(JavaPadMessage jpm)
    {
        try
        {
            final String pass = Arrays.toString(expectedPassword);
            final boolean passwordRequired = pass.equals("");
            final boolean passwordCorrect = pass.equals(jpm.getMessageBody());

            // Validate the message
            if (clientList.isFull())
            {
                // No room, send error
                out.writeObject(new JavaPadMessage(MessageType.CONNECT_DENIED,
                        "Server Response: Connection Refused, Room Full"));

                notifier.sendToConsole("Connection refused: Room Full");
                LOGGER.info("Connection refused: Room Full");

            } else if (passwordRequired && !passwordCorrect)
            {
                // Password was wrong, send error
                out.writeObject(new JavaPadMessage(MessageType.CONNECT_DENIED,
                        "Server Response: Connection Refused, Password incorrect."));

                notifier.sendToConsole("Connection refused: Incorrect Password");
                LOGGER.info("Connection refused: Incorrect Password");

            } else
            {
                // everything worked fine!
                out.writeObject(new JavaPadMessage(MessageType.CONNECT));
                clientList.addClient(id, this);

                notifier.sendToConsole("Successful connection made, id: " + id);
                LOGGER.info("Successful conncetion made, id: " + id);
            }
        } catch (final IOException e)
        {
            LOGGER.severe("Error in connection handshake: " + e.getMessage());
        }
    }

    @Override
    public void disconnect()
    {
        clientList.removeClient(this);
        notifier.sendToConsole("Client has Disconnected, id: " + id);
        try
        {
            if (socket != null)
            {
                sendMessage(new JavaPadMessage(MessageType.DISCONNECT));
                socket.close();
            }
        } catch (final IOException e)
        {
            // LOG ERROR CLOSING
        }
    }

    @Override
    public int getId()
    {
        return id;
    }

    @Override
    public void run()
    {
        try
        {
            LOGGER.info("Running the ClientConnection thread");

            // Create streams
            if (socket != null)
            {
                LOGGER.info("Creating the streams and starting thread");
                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());
                new Thread(new DataReader()).start();
            }

        } catch (final IOException e)
        {
            LOGGER.severe("Error when running clientconn" + e.getMessage());
        }
    }

    private class DataReader implements Runnable
    {
        @Override
        public void run()
        {
            LOGGER.info("Starting the DataReader Thread");

            try
            {
                while (true)
                {
                    if (in == null)
                    {
                        continue;
                    }

                    final JavaPadMessage jpm = (JavaPadMessage) in.readObject();
                    LOGGER.info("Client message received: "
                            + jpm.getMessageType());
                    handleMessage(jpm);

                    if (socket.isClosed())
                    {
                        break;
                    }
                }
                // Disconnect as its closed
                disconnect();
            } catch (final EOFException e)
            {
                // Client has disconnected
                disconnect();
            } catch (ClassNotFoundException | IOException e)
            {
                LOGGER.severe("Error: " + e.getMessage());
                disconnect();
            }
        }
    }
}
