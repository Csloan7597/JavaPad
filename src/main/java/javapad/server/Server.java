package javapad.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.logging.Logger;

import javapad.server.interfaces.IClientConnection;
import javapad.server.interfaces.IClientList;
import javapad.server.interfaces.IDataPublisher;
import javapad.server.interfaces.ILock;
import javapad.server.interfaces.IMessenger;
import javapad.server.interfaces.INotifier;
import javapad.server.interfaces.IServer;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;


public class Server implements Runnable, IServer
{
    private static Logger LOGGER = Logger.getLogger(Server.class
            .getSimpleName());

    // Constructor Param values
    private final INotifier notifier;
    private final int connectionLimit;
    private final char[] password;

    // Resources to be passed down
    IMessenger messenger;
    ILock lock;
    IClientList clientList;
    IDataPublisher publisher;
    IClientConnection conn;

    // Network Resources
    final String[] enabledCipherSuites =
    { "SSL_DH_anon_WITH_RC4_128_MD5" };
    SSLServerSocketFactory socketFactory;
    SSLServerSocket serverSocket;
    SSLSocket socket;
    ObjectInputStream in;
    ObjectOutputStream out;

    // Checks for the input message
    boolean passwordRequired;
    boolean passwordCorrect;
    boolean isConnectRequest;

    private final int port;

    public Server(INotifier notifier, int connectionLimit, char[] password,
            int port)
    {
        this.notifier = notifier;
        this.connectionLimit = connectionLimit;
        this.password = password;
        this.port = port;
    }

    @Override
    public void kill()
    {
        try
        {
            if (serverSocket != null)
            {
                serverSocket.close();
            }
            if (socket != null)
            {
                socket.close();
            }
            // Possibly kill every connection in the clientlist?
            if (clientList == null)
            {
                return;
            }

            for (final IClientConnection conn : clientList.getAll())
            {
                conn.disconnect();
            }
        } catch (final IOException e)
        {
            // Swallow this output
            LOGGER.info("Swallowing output for socket.close");
        }
    }

    @Override
    public void run()
    {
        // Create the Objects needed for this session
        // TODO Clean up object relations or what?
        lock = new Lock();
        messenger = new Messenger(notifier);
        clientList = new ClientList(notifier, messenger, connectionLimit);
        publisher = new Publisher(lock, clientList);

        try
        {
            notifier.sendToConsole("Server Started");
            LOGGER.info("Server Started");

            // Set up networking
            socketFactory = (SSLServerSocketFactory) SSLServerSocketFactory
                    .getDefault();
            serverSocket = (SSLServerSocket) socketFactory
                    .createServerSocket(port);
            serverSocket.setEnabledCipherSuites(enabledCipherSuites);

            int idCounter = 1;

            // Now repeatedly listen for connections
            while (true)
            {
                // Blocks whilst waiting for an incoming connection
                socket = (SSLSocket) serverSocket.accept();

                conn = new ClientConnection(socket, idCounter, notifier, lock,
                        publisher, clientList, password);
                new Thread((Runnable) conn).start();

                LOGGER.info("Someone connected: " + idCounter);
                idCounter++;
            }

        } catch (final SocketException e)
        {
            LOGGER.severe("Socket Exception: Server closed?");
            notifier.sendToConsole("Server Stopped");
        } catch (final IOException e)
        {
            LOGGER.severe(e.getMessage());
            notifier.sendError(e);
        } finally
        {
            kill();
        }
    }
}
