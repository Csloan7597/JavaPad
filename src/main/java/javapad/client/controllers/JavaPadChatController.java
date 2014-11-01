package javapad.client.controllers;

import javapad.client.services.ChatNetworkService;
import javapad.client.services.UDPChatNetworkService;
import javapad.client.views.JavaPadChatView;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by conor on 25/10/2014.
 */
public class JavaPadChatController {

    private static final String TEXT_SUBMIT = "text-submit";
    private static final String INSERT_BREAK = "insert-break";

    // Views
    private JavaPadChatView view;

    // Services
    private ChatNetworkService networkService = new UDPChatNetworkService();

    // State / models
    private String name = "anon";

    public JavaPadChatController(JavaPadChatView view) {
        this.view = view;
        configureActions();
        networkService.setMessageCallback(this::handleMessage);
    }


    public void openChat() {
        if ("anon".equals(name)) {
            this.name = JOptionPane.showInputDialog(view, "What is your name?");
        }

        view.showChatView();
    }

    public void connect() {
        networkService.connect();
    }

    public void disconnect() {
        networkService.disconnect();
    }

    public void configureActions() {
        view.getSendChat().addActionListener(e -> {
            try
            {
                networkService.sendMessage(name + ": " + view.getChatToSend().getText());
                view.getChatToSend().setText("");
            } catch (final Exception ex)
            {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });// end sendChat

        final InputMap input = view.getChatToSend().getInputMap();
        final KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
        final KeyStroke shiftEnter = KeyStroke.getKeyStroke("shift ENTER");
        input.put(shiftEnter, INSERT_BREAK);
        input.put(enter, TEXT_SUBMIT);

        final ActionMap actions = view.getChatToSend().getActionMap();
        actions.put(TEXT_SUBMIT, new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                networkService.sendMessage(name + ": " + view.getChatToSend().getText());
                view.getChatToSend().setText("");
            }
        });
    }

    private Void handleMessage(String message) {
        final int indexOfColon = message.indexOf(':');
        final String messageName = message.substring(0, indexOfColon);
        final String rest = message.substring(indexOfColon);
        view.appendChat(messageName, rest, name.equals(messageName));
        return null;
    }

    public boolean isConnected() {
        return networkService.isConnected();
    }

}
