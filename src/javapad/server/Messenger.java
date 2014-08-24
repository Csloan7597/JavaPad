package javapad.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javapad.server.interfaces.IMessenger;
import javapad.server.interfaces.INotifier;


public class Messenger implements IMessenger
{

    private final INotifier notifier;
    private MulticastSocket chatServerSocket;
    private InetAddress group;

    public Messenger(INotifier notifier)
    {
        this.notifier = notifier;
        connect();
    }

    private void connect()
    {
        try
        {
            // not sure this ever has to actually read anything???
            chatServerSocket = new MulticastSocket();
            group = InetAddress.getByName("228.5.6.7");
            chatServerSocket.joinGroup(group);

        } catch (final IOException e)
        {
            notifier.sendError(e);
        }
    }

    @Override
    public synchronized void sendSystemChatMessage(String message)
    {
        byte[] sendData = new byte[1024];
        sendData = message.getBytes();

        final DatagramPacket sendPacket = new DatagramPacket(sendData,
                sendData.length, group, 9877);
        try
        {
            chatServerSocket.send(sendPacket);
        } catch (final IOException e)
        {
            e.printStackTrace();
        }
    }
}
