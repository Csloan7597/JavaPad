package javapad.client.views;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

/**
 * Created by conor on 20/10/2014.
 */
public class JavaPadView extends JFrame {

    private static Logger LOGGER = Logger.getLogger(JavaPadView.class
            .getSimpleName());

    private final JMenuBar menuBar = new JMenuBar();
    private final JMenu file = new JMenu("File");
    private final JMenuItem open = new JMenuItem("Open");
    private final JMenuItem save = new JMenuItem("Save");
    private final JMenuItem close = new JMenuItem("Close");
    private final JMenuItem mode = new JMenuItem("Change Syntax Mode");
    private final JMenuItem changeFont = new JMenuItem("Change Font");
    private final JMenu network = new JMenu("Network");
    private final JMenuItem sendData = new JMenuItem("Send Data");
    private final JMenuItem toggleControl = new JMenuItem("Request Control");
    private final JMenuItem connect = new JMenuItem("Connect to Network");
    private final JMenuItem disconnect = new JMenuItem("Disconnect");
    private final JMenu chat = new JMenu("Chat");
    private final JMenuItem openChat = new JMenuItem("Open Chat Window");

    private final RSyntaxTextArea syntaxTextArea = new RSyntaxTextArea();
    private final RTextScrollPane sp = new RTextScrollPane(syntaxTextArea);

    public JavaPadView() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception e) {
            LOGGER.severe("Error setting native LAF: " + e);
        }
        syntaxTextArea
                .setText("Welcome to JavaPad! Use locally or connect to a server!");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        setSize(500, 600);
        setTitle("JavaPad");
        setLocationByPlatform(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setJMenuBar(menuBar);

        // Configure menu & keyboard shortcuts
        configureMenus();
        menuBar.add(file);
        menuBar.add(network);
        menuBar.add(chat);

        getContentPane().add(sp);
        setVisible(true);
    }

    /**
     * Sets up accelerators for the GUI of this application
     */
    private void configureMenus() {
        open.setAccelerator(KeyStroke.getKeyStroke('O',
                InputEvent.CTRL_DOWN_MASK));
        file.add(open);
        save.setAccelerator(KeyStroke.getKeyStroke('S',
                InputEvent.CTRL_DOWN_MASK));
        file.add(save);
        mode.setAccelerator(KeyStroke.getKeyStroke('M',
                InputEvent.CTRL_DOWN_MASK));
        file.add(mode);
        changeFont.setAccelerator(KeyStroke.getKeyStroke('F',
                InputEvent.CTRL_DOWN_MASK));
        file.add(changeFont);
        close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,
                InputEvent.ALT_DOWN_MASK));
        file.add(close);
        toggleControl.setAccelerator(KeyStroke.getKeyStroke('R',
                InputEvent.CTRL_DOWN_MASK));
        network.add(toggleControl);
        sendData.setAccelerator(KeyStroke.getKeyStroke('Q',
                InputEvent.CTRL_DOWN_MASK));
        network.add(sendData);
        connect.setAccelerator(KeyStroke.getKeyStroke('B',
                InputEvent.CTRL_DOWN_MASK));
        network.add(connect);
        disconnect.setAccelerator(KeyStroke.getKeyStroke('K',
                InputEvent.CTRL_DOWN_MASK));
        network.add(disconnect);
        openChat.setAccelerator(KeyStroke.getKeyStroke('P',
                InputEvent.CTRL_DOWN_MASK));
        chat.add(openChat);
    }


    public JMenu getFile() {
        return file;
    }

    public JMenuItem getOpen() {
        return open;
    }

    public JMenuItem getSave() {
        return save;
    }

    public JMenuItem getClose() {
        return close;
    }

    public JMenuItem getMode() {
        return mode;
    }

    public JMenuItem getChangeFont() {
        return changeFont;
    }

    public JMenu getNetwork() {
        return network;
    }

    public JMenuItem getSendData() {
        return sendData;
    }

    public JMenuItem getToggleControl() {
        return toggleControl;
    }

    public JMenuItem getConnect() {
        return connect;
    }

    public JMenuItem getDisconnect() {
        return disconnect;
    }

    public JMenu getChat() {
        return chat;
    }

    public JMenuItem getOpenChat() {
        return openChat;
    }

    public RSyntaxTextArea getSyntaxTextArea() {
        return syntaxTextArea;
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void setPageTitle(String title) {
        this.setTitle(title);
    }
}
