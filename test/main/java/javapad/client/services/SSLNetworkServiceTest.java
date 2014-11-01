package javapad.client.services;

import javapad.shared.utils.JavaPadMessage;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import java.io.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by conor on 25/10/2014.
 */
public class SSLNetworkServiceTest {

    SSLNetworkService service;
    SSLSocketFactory socketFactory = Mockito.mock(SSLSocketFactory.class, RETURNS_DEEP_STUBS);
    SSLSocket socket = Mockito.mock(SSLSocket.class);

    SSLNetworkResponseListener rl = Mockito.mock(SSLNetworkResponseListener.class);

    @Before
    public void setUp() throws IOException {
        Mockito.reset(socketFactory);
        when(socketFactory.createSocket(any(String.class), any(Integer.class))).thenReturn(socket);
    }

    @Test
    public void connectionTest() throws IOException {

        String pathToFile = "test/main/java/javapad/client/services/";

        //when(socket.getInputStream()).thenReturn(new FileInputStream(pathToFile + "testFileIn.txt"));
        //when(socket.getOutputStream()).thenReturn(new FileOutputStream(pathToFile + "testFile.txt"));

        when(socket.getInputStream()).thenReturn(new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        });
        when(socket.getOutputStream()).thenReturn(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                // Do nothing
            }
        });
        when(socket.isClosed()).thenReturn(false);

        service = new SSLNetworkService(socketFactory, rl);
        service.connect("10.2.2.2", new char[] {},  3);

        verify(socket).getInputStream();
        verify(socket).getOutputStream();
    }
}
