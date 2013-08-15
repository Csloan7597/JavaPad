package javapad.client.interfaces;

import java.io.IOException;

import javapad.shared.utils.JavaPadMessage;


public interface IConnectionManager
{
    /**
     * Connects to the JavaPad server, based on IP, password, and port
     * 
     * @param IP
     *            the IP to connect to
     * @param pass
     *            The password for this server (optional)
     * @param port
     *            The port to connect via
     * @throws IOException
     */
    void connect(String IP, char[] pass, int port) throws IOException;

    /**
     * Disconnects from the JavaPad Server
     * 
     */
    void disconnect() throws IOException;

    /**
     * Sends a message to the Javapad server using the wrapper object
     * JavaPadMessage
     * 
     * @param message
     * @throws IOException
     */
    void sendJavaPadMessage(JavaPadMessage message) throws IOException;

}
