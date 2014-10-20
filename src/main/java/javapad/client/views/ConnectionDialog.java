package javapad.client.views;

import javapad.shared.utils.TextUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by conor on 20/10/2014.
 */
public class ConnectionDialog {

    private final JDialog connectionDialog;
    private final JLabel connectionMessage1 = new JLabel("IP address:");
    private final JTextField ipEntryField = new JTextField(15);
    private final JLabel connectionMessage2 = new JLabel("Port: \t");
    private JComboBox<String> portEntryField;
    private final JLabel connectionMessage3 = new JLabel(
            "\nPassword (Tick if needed): ");
    private final JPasswordField connectPasswordField = new JPasswordField(15);
    private final JCheckBox passwdCheck = new JCheckBox();
    private final JButton connectOkButton = new JButton("Connect");

    public ConnectionDialog(JavaPadView parent) {
        this.connectionDialog = new JDialog(parent);

        portEntryField = new JComboBox<String>(TextUtils.range(12111, 12141));

        // connectionDialog.setLocationByPlatform(true);
        connectionDialog.setSize(215, 200);
        connectionDialog.setResizable(false);
        connectionDialog.getRootPane().setDefaultButton(connectOkButton);
        connectPasswordField.setEnabled(false);
        connectionDialog.setLayout(new FlowLayout(FlowLayout.CENTER));
        connectionDialog.setLocationRelativeTo(parent);
        connectionDialog.setTitle("Connect");
        connectionDialog.add(connectionMessage1);
        connectionDialog.add(ipEntryField);
        connectionDialog.add(connectionMessage2);
        connectionDialog.add(portEntryField);
        connectionDialog.add(connectionMessage3);
        connectionDialog.add(connectPasswordField);
        connectionDialog.add(passwdCheck);
        connectionDialog.add(connectOkButton);
        passwdCheck.addActionListener(e -> connectPasswordField.setEnabled(passwdCheck.isSelected()));
    }

    public void show() {
        this.connectionDialog.setVisible(true);
    }

    public void hide() {
        this.connectionDialog.setVisible(false);
    }

    public JButton getConnectOkButton() {
        return connectOkButton;
    }

    public JPasswordField getConnectPasswordField() {
        return connectPasswordField;
    }

    public JComboBox<String> getPortEntryField() {
        return portEntryField;
    }

    public JTextField getIpEntryField() {
        return ipEntryField;
    }
}
