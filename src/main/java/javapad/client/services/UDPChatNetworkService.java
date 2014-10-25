package javapad.client.services;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by conor on 25/10/2014.
 */
public class UDPChatNetworkService implements ChatNetworkService {

    private static final Logger LOGGER = Logger.getLogger(UDPChatNetworkService.class
            .getSimpleName());

    private MulticastSocket myChatSocket;
    private InetAddress group;
    private IntermittentChatReaderThread myChatReaderThread;
    private Function<String, Void> messageCallback;

    private boolean disconnected = true;

    @Override
    public void connect() {
        try
        {
            myChatSocket = new MulticastSocket(9877);
            group = InetAddress.getByName("228.5.6.7");
            myChatSocket.joinGroup(group);
            LOGGER.info("I have joined the group!");


            disconnected = false;
            myChatReaderThread = new IntermittentChatReaderThread();
            myChatReaderThread.execute();


        } catch (final IOException e)
        {
            LOGGER.severe("Error connecting to multicast: " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        disconnected = true;
        myChatSocket.close();
    }

    @Override
    public void setMessageCallback(Function<String, Void> callback) {
        this.messageCallback = callback;
    }

    @Override
    public void sendMessage(String message) {
        byte[] sendData = new byte[1024];
        sendData = message.getBytes();
        final DatagramPacket sendPacket = new DatagramPacket(sendData,
                sendData.length, group, 9877);
        try
        {
            myChatSocket.send(sendPacket);
            System.out.println("Sent chat!");
        } catch (final IOException e)
        {
            LOGGER.severe("Error sending chat: " + e.getMessage());
        }
    }

    class IntermittentChatReaderThread extends SwingWorker<Void, String>
    {
        @SuppressWarnings("unchecked")
        @Override
        protected Void doInBackground() throws Exception
        {
            while (true)
            {
                // to fill with data
                LOGGER.info("The messenger reader is running");
                final byte[] receiveData = new byte[1024];
                final DatagramPacket receivePacket = new DatagramPacket(
                        receiveData, receiveData.length);
                myChatSocket.receive(receivePacket);
                final String sentence = new String(receivePacket.getData());
                publish(sentence);

                if (disconnected) {
                    break;
                }

            }
            return null;
        }

        @Override
        protected void process(List<String> chunks)
        {
            // here we check what type it is and add stuff.
            messageCallback.apply(chunks.get(0));
        }
    }

    public boolean isConnected() {
        return !disconnected;
    }

}
