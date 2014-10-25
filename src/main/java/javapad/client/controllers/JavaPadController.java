package javapad.client.controllers;

import javapad.client.models.ConnectionState;
import javapad.client.services.FileService;
import javapad.client.services.JavaPadNetworkService;
import javapad.client.views.*;
import javapad.shared.utils.JavaPadMessage;

/**
 * Created by conor on 20/10/2014.
 *
 * Main controller of the application, representing the main text pad window.
 */
public class JavaPadController {

    // Services
    private final FileService fileService;
    private final JavaPadNetworkService jpNetworkService;

    // Views
    private final JavaPadView view;
    private final ChangeFontDialog changeFontDialog;
    private final ChangeSyntaxModeDialog changeSyntaxDialog;
    private final ConnectionDialog connectionDialog;

    // Model / State
    private ConnectionState.ConnectionStatus connectionStatus;

    // Sub-controllers
    private JavaPadChatView chatView = new JavaPadChatView();
    private JavaPadChatController chatController = new JavaPadChatController(chatView);


    public JavaPadController(JavaPadView view, FileService fileService, JavaPadNetworkService jpNetworkService) {
        this.view = view;
        this.fileService = fileService;
        this.jpNetworkService = jpNetworkService;
        this.changeFontDialog = new ChangeFontDialog(view);
        this.changeSyntaxDialog = new ChangeSyntaxModeDialog(view);
        this.connectionDialog = new ConnectionDialog(view);
        configureActionListeners();

        // Set default connection status
        this.updateConnectionStateInView(ConnectionState.ConnectionStatus.DISCONNECTED);

        // This will be updated when some connection happens
        jpNetworkService.setMessageCallback(this::messageCallback);
    }

    private void configureActionListeners() {

        view.getClose().addActionListener(e -> System.exit(0));

        view.getOpen().addActionListener(e -> fileService.open(view) );

        view.getSave().addActionListener(e -> fileService.save(view) );

        view.getChangeFont().addActionListener(e -> changeFontDialog.show() );

        view.getMode().addActionListener(e -> changeSyntaxDialog.show() );

        view.getSendData().addActionListener(e ->
            jpNetworkService.sendMessage(new JavaPadMessage(
                    JavaPadMessage.MessageType.SEND_DATA, view.getSyntaxTextArea().getText()))
        );

        view.getConnect().addActionListener(e -> connectionDialog.show() );

        view.getDisconnect().addActionListener(e -> jpNetworkService.disconnect() );

        view.getToggleControl().addActionListener(e -> {
            JavaPadMessage jpm;
            switch (connectionStatus)
            {
                case CONNECTED_NO_CONTROL:
                    jpm = new JavaPadMessage(JavaPadMessage.MessageType.CONTROL_REQUEST, "");
                    jpNetworkService.sendMessage(jpm);
                    break;

                case CONNECTED_IN_CONTROL:
                    jpm = new JavaPadMessage(JavaPadMessage.MessageType.CONTROL_RELEASE, "");
                    jpNetworkService.sendMessage(jpm);
                    break;

                default:
                    break;
            }
        });

        view.getOpenChat().addActionListener(e -> {
            chatController.openChat();
        });

        changeFontDialog.getFontOkButton().addActionListener(e -> {
            view.getSyntaxTextArea().setFont(changeFontDialog.getCurrentFont());
            changeFontDialog.hide();
        });

        changeSyntaxDialog.getSyntaxOkButton().addActionListener(e -> {
            view.getSyntaxTextArea().setSyntaxEditingStyle(changeSyntaxDialog.getCurrentSyntaxConstant());
            changeSyntaxDialog.hide();
        });

        connectionDialog.getConnectOkButton().addActionListener(e -> {
            jpNetworkService.connect(connectionDialog.getIpEntryField().getText(),
                    connectionDialog.getConnectPasswordField().getPassword(),
                    Integer.parseInt((String)connectionDialog.getPortEntryField().getSelectedItem()));
            connectionDialog.hide();
        });
    }

    private Void messageCallback(JavaPadMessage message) {
        // Do stuff here based on message;
        switch (message.getMessageType())
        {
            case CONNECT:
                view.showMessage("You have connected successfully!");
                updateConnectionStateInView(ConnectionState.ConnectionStatus.CONNECTED_NO_CONTROL);
                break;
            case CONNECT_DENIED:
                view.showMessage("Error: Could not connect, message: "
                        + message.getMessageBody());
                break;
            case DISCONNECT:
                if (this.connectionStatus != ConnectionState.ConnectionStatus.DISCONNECTED)
                {
                    view.showMessage("Server has disconnected");
                    updateConnectionStateInView(ConnectionState.ConnectionStatus.DISCONNECTED);
                }
                break;
            case CONTROL_GRANTED:
                view.showMessage("You have gained control!");
                updateConnectionStateInView(ConnectionState.ConnectionStatus.CONNECTED_IN_CONTROL);
                break;
            case CONTROL_DENIED:
                view.showMessage("You have been denied control, message: "
                        + message.getMessageBody());
                break;
            case CONTROL_RELEASE:
                view.showMessage("Control released");
                updateConnectionStateInView(ConnectionState.ConnectionStatus.CONNECTED_NO_CONTROL);
                break;
            case SEND_DATA:
                view.getSyntaxTextArea().setText(message.getMessageBody());
                view.showMessage("New Data Received!");
                break;
            case SERVER_RESPONSE_ERROR:
                view.showMessage("The server returned an error; Message: "
                        + message.getMessageBody());
                break;

            default:
                break;
        }
        return null;
    }

    private void updateConnectionStateInView(ConnectionState.ConnectionStatus state) {

        this.connectionStatus = state;

        switch (state)
        {
            case DISCONNECTED:
                view.setPageTitle("JavaPad");
                view.getSyntaxTextArea().setEditable(true);
                view.getChat().setEnabled(false);
                view.getChat().setVisible(false);
                view.getToggleControl().setEnabled(false);
                view.getSendData().setEnabled(false);
                view.getConnect().setEnabled(true);
                view.getDisconnect().setEnabled(false);
                view.getOpen().setEnabled(true);
                view.getSave().setEnabled(true);
                break;

            case CONNECTED_NO_CONTROL:
                view.setPageTitle("JavaPad - Connected");
                view.getSyntaxTextArea().setEditable(false);
                view.getChat().setEnabled(true);
                view.getChat().setVisible(true);
                view.getToggleControl().setEnabled(true);
                view.getToggleControl().setText("Request Control");
                view.getSendData().setEnabled(false);
                view.getConnect().setEnabled(false);
                view.getDisconnect().setEnabled(true);
                view.getOpen().setEnabled(false);
                view.getSave().setEnabled(true);
                break;

            case CONNECTED_IN_CONTROL:
                view.setPageTitle("JavaPad - Connected");
                view.getSyntaxTextArea().setEditable(true);
                view.getChat().setEnabled(true);
                view.getChat().setVisible(true);
                view.getToggleControl().setEnabled(true);
                view.getToggleControl().setText("Relinquish Control");
                view.getSendData().setEnabled(true);
                view.getConnect().setEnabled(false);
                view.getDisconnect().setEnabled(true);
                view.getOpen().setEnabled(true);
                view.getSave().setEnabled(true);
                break;
        }
    }
}
