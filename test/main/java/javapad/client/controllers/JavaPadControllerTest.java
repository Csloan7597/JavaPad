package javapad.client.controllers;

import javapad.client.services.FileService;
import javapad.client.services.JavaPadNetworkService;
import javapad.client.views.ChangeFontDialog;
import javapad.client.views.ChangeSyntaxModeDialog;
import javapad.client.views.ConnectionDialog;
import javapad.client.views.JavaPadView;
import javapad.shared.utils.JavaPadMessage;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.swing.*;
import java.util.function.Function;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by conor on 25/10/2014.
 */
public class JavaPadControllerTest {

    JavaPadController controller;
    JavaPadView view = Mockito.mock(JavaPadView.class);
    FileService fileService = Mockito.mock(FileService.class);
    JavaPadNetworkService networkService = Mockito.mock(JavaPadNetworkService.class);
    ChangeFontDialog fontDialog = Mockito.mock(ChangeFontDialog.class);
    ChangeSyntaxModeDialog syntaxModeDialog = Mockito.mock(ChangeSyntaxModeDialog.class);
    ConnectionDialog connectionDialog = Mockito.mock(ConnectionDialog.class);


    // Main View component mocks
    JMenu chat = Mockito.mock(JMenu.class);
    JMenu file = Mockito.mock(JMenu.class);
    JMenu network = Mockito.mock(JMenu.class);
    JMenuItem changeFont = Mockito.mock(JMenuItem.class);
    JMenuItem close = Mockito.mock(JMenuItem.class);
    JMenuItem connect = Mockito.mock(JMenuItem.class);
    JMenuItem disconnect = Mockito.mock(JMenuItem.class);
    JMenuItem mode = Mockito.mock(JMenuItem.class);
    JMenuItem open = Mockito.mock(JMenuItem.class);
    JMenuItem openChat = Mockito.mock(JMenuItem.class);
    JMenuItem save = Mockito.mock(JMenuItem.class);
    JMenuItem sendData = Mockito.mock(JMenuItem.class);
    JMenuItem toggleControl = Mockito.mock(JMenuItem.class);
    RSyntaxTextArea syntaxTextArea = Mockito.mock(RSyntaxTextArea.class);

    // Dialog view mocks
    JButton fontOkButton = Mockito.mock(JButton.class);
    JButton syntaxOkButton = Mockito.mock(JButton.class);
    JButton connectionOkButton = Mockito.mock(JButton.class);

    public void resetMocks() {
        Mockito.reset(view, fileService, networkService, fontDialog, syntaxModeDialog, connectionDialog, chat,
                file, network, changeFont, close, connect, disconnect, mode, open, openChat, save, sendData,
                toggleControl, syntaxTextArea
        );
    }

    public void setUpViewMockRules() {
        // Mocking rules
        when(view.getChangeFont()).thenReturn(changeFont);
        when(view.getClose()).thenReturn(close);
        when(view.getConnect()).thenReturn(connect);
        when(view.getDisconnect()).thenReturn(disconnect);
        when(view.getMode()).thenReturn(mode);
        when(view.getOpen()).thenReturn(open);
        when(view.getChat()).thenReturn(chat);
        when(view.getOpenChat()).thenReturn(openChat);
        when(view.getSave()).thenReturn(save);
        when(view.getSendData()).thenReturn(sendData);
        when(view.getSyntaxTextArea()).thenReturn(syntaxTextArea);
        when(view.getToggleControl()).thenReturn(toggleControl);
        when(fontDialog.getFontOkButton()).thenReturn(fontOkButton);
        when(syntaxModeDialog.getSyntaxOkButton()).thenReturn(syntaxOkButton);
        when(connectionDialog.getConnectOkButton()).thenReturn(connectionOkButton);
    }

    @Before
    public void setUp() {
        resetMocks();
        setUpViewMockRules();
    }

    @Test
    public void startupTest() {
        // Mock the view
        controller = new JavaPadController(view, fileService, networkService, fontDialog, syntaxModeDialog, connectionDialog);

        // Verify controller has configured responses to all 'actions' on the GUI
        verify(close).addActionListener(any());
        verify(changeFont).addActionListener(any());
        verify(mode).addActionListener(any());
        verify(open).addActionListener(any());
        verify(openChat).addActionListener(any());
        verify(save).addActionListener(any());
        verify(sendData).addActionListener(any());
        verify(disconnect).addActionListener(any());
        verify(toggleControl).addActionListener(any());
        verify(connect).addActionListener(any());
        verify(fontOkButton).addActionListener(any());
        verify(syntaxOkButton).addActionListener(any());
        verify(connectionOkButton).addActionListener(any());

        // Verify correct GUI modifications were made to reflect a non-connected state
        assertDisconnectedState();

        // Verify that the networking service has a callback set for message passing
        verify(networkService).setMessageCallback(any(Function.class));
    }

    @Test
    public void callbackTest() {
        controller = new JavaPadController(view, fileService, networkService, fontDialog, syntaxModeDialog, connectionDialog);

        // Capture the callback argument
        ArgumentCaptor<Function> argument = ArgumentCaptor.forClass(Function.class);
        verify(networkService).setMessageCallback(argument.capture());
        Function<JavaPadMessage, Void> callback = argument.getValue();
        assertNotNull(callback);

        /** Now perform some tests on the callback **/

        // Assert no errors from a null input (returns null)
        assertNull(callback.apply(null));

        /** For each type of message, check the type is checked in the callback, messages are given to view,
         * and state changed when applicable. Then reset the mocks. **/

        // CONNECT_DENIED - shows message
        callbackTest(Mockito.mock(JavaPadMessage.class), callback, JavaPadMessage.MessageType.CONNECT_DENIED, true);
        resetMocks(); setUpViewMockRules();

        // CONNECT - shows message & updates state to connected
        callbackTest(Mockito.mock(JavaPadMessage.class), callback, JavaPadMessage.MessageType.CONNECT, false);
        assertConnectedNoControlState();
        resetMocks(); setUpViewMockRules();

        // DISCONNECT - shows message & updates state to disconnected
        callbackTest(Mockito.mock(JavaPadMessage.class), callback, JavaPadMessage.MessageType.DISCONNECT, false);
        assertDisconnectedState();
        resetMocks(); setUpViewMockRules();

        // CONTROL_GRANTED - shows message & updates state to CONNECTED_IN_CONTROL
        callbackTest(Mockito.mock(JavaPadMessage.class), callback, JavaPadMessage.MessageType.CONTROL_GRANTED, false);
        assertConnectedInControlState();
        resetMocks(); setUpViewMockRules();

        // CONTROL_DENIED - shows message (reason)
        callbackTest(Mockito.mock(JavaPadMessage.class), callback, JavaPadMessage.MessageType.CONTROL_DENIED, true);
        resetMocks(); setUpViewMockRules();

        // CONTROL_RELEASED - updates state to CONNECTED_NO_CONTROL & shows message
        callbackTest(Mockito.mock(JavaPadMessage.class), callback, JavaPadMessage.MessageType.CONTROL_RELEASE, false);
        assertConnectedNoControlState();
        resetMocks(); setUpViewMockRules();

        // SEND_DATA - sets text area & shows message
        callbackTest(Mockito.mock(JavaPadMessage.class), callback, JavaPadMessage.MessageType.SEND_DATA, true);
        resetMocks(); setUpViewMockRules();

        // SERVER_RESPONSE_ERROR - shows message
        callbackTest(Mockito.mock(JavaPadMessage.class), callback, JavaPadMessage.MessageType.SERVER_RESPONSE_ERROR, true);
        resetMocks(); setUpViewMockRules();
    }

    public void callbackTest(JavaPadMessage mockMessage, Function<JavaPadMessage, Void> callback,
                             JavaPadMessage.MessageType type, boolean checkForMessageBody) {
        when(mockMessage.getMessageType()).thenReturn(type);
        callback.apply(mockMessage);
        verify(mockMessage).getMessageType();
        if (checkForMessageBody) { verify(mockMessage).getMessageBody(); }
        verify(view).showMessage(any(String.class));
        Mockito.reset(mockMessage);
    }

    public void assertConnectedInControlState() {
        verify(view).setPageTitle("JavaPad - Connected");
        verify(syntaxTextArea).setEditable(true);
        verify(chat).setEnabled(true);
        verify(chat).setVisible(true);
        verify(toggleControl).setEnabled(true);
        verify(toggleControl).setText("Relinquish Control");
        verify(sendData).setEnabled(true);
        verify(connect).setEnabled(false);
        verify(disconnect).setEnabled(true);
        verify(open).setEnabled(true);
        verify(save).setEnabled(true);
    }

    public void assertConnectedNoControlState() {
        verify(view).setPageTitle("JavaPad - Connected");
        verify(syntaxTextArea).setEditable(false);
        verify(chat).setEnabled(true);
        verify(chat).setVisible(true);
        verify(toggleControl).setEnabled(true);
        verify(toggleControl).setText("Request Control");
        verify(sendData).setEnabled(false);
        verify(connect).setEnabled(false);
        verify(disconnect).setEnabled(true);
        verify(open).setEnabled(false);
        verify(save).setEnabled(true);
    }

    public void assertDisconnectedState() {
        verify(view).setPageTitle("JavaPad");
        verify(syntaxTextArea).setEditable(true);
        verify(chat).setEnabled(false);
        verify(chat).setVisible(false);
        verify(toggleControl).setEnabled(false);
        verify(sendData).setEnabled(false);
        verify(connect).setEnabled(true);
        verify(disconnect).setEnabled(false);
        verify(open).setEnabled(true);
        verify(save).setEnabled(true);
    }

}
