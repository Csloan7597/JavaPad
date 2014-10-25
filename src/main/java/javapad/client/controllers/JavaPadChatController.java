package javapad.client.controllers;

import javapad.client.views.JavaPadChatView;

import javax.swing.*;

/**
 * Created by conor on 25/10/2014.
 */
public class JavaPadChatController {

    // Views
    private JavaPadChatView view;

    // State / models
    private String name = "anon";

    public JavaPadChatController(JavaPadChatView view) {
        this.view = view;
    }

    public void openChat() {
        if ("anon".equals(name)) {
            this.name = JOptionPane.showInputDialog(view, "What is your name?");
        }

        view.showChatView();
    }




}
