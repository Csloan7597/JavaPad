package javapad.server.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javapad.server.GUINotifier;
import javapad.server.Server;
import javapad.server.interfaces.INotifier;
import javapad.server.interfaces.IServer;
import javapad.shared.utils.TextUtils;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;


public class JavaPadServerGUI extends JFrame implements Observer
{

    /**
     * 
     */
    private static final long serialVersionUID = 220317380051918723L;
    private static final Logger LOGGER = Logger
            .getLogger(JavaPadServerGUI.class.getSimpleName());

    private enum ServerRunState
    {
        RUNNING, STOPPED;
    }

    private ServerRunState serverRunState;
    private IServer server;
    private Thread serverThread;
    private GUINotifier notifier;

    private final JLabel passLabel = new JLabel();
    private final JLabel limitLabel = new JLabel();
    private final JLabel portLabel = new JLabel();
    private final JLabel welcomeMessage = new JLabel();
    private final JPanel topPanel = new JPanel();
    private final JPanel consolePanel = new JPanel();
    private final JPasswordField passwordField = new JPasswordField();
    private final JTextField limitInput = new JTextField();
    private JComboBox<String> portInput;
    private final JLabel status = new JLabel();
    private final JButton run = new JButton();
    // private final notifier notify = new notifier();

    private int connections = 0;
    private Thread t;
    private final JTextPane console = new JTextPane();
    private final JScrollPane consolePane = new JScrollPane(console,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    private final HTMLEditorKit kit = new HTMLEditorKit();
    private final HTMLDocument doc = new HTMLDocument();

    private void constructGUI()
    {
        console.setEditorKit(kit);
        console.setDocument(doc);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final Container Pane = this.getContentPane();
        Pane.setLayout(new BorderLayout());
        setSize(new Dimension(600, 500));
        setLocationByPlatform(true);

        welcomeMessage
                .setText("<html>Welcome to the server side of JavaPad. <br> "
                        + "Please enter a port & connection limit, then click run.<br><br></html> ");
        welcomeMessage.setFont(new Font("Dialog", Font.PLAIN, 16));

        limitLabel.setText("Please enter a limit for connections here:");
        limitLabel.setFont(new Font("Dialog", Font.PLAIN, 13));
        passLabel.setText(" Please enter a password for clients here:");
        passLabel.setFont(new Font("Dialog", Font.PLAIN, 13));
        portLabel.setText("Port:");
        portLabel.setFont(new Font("Dialog", Font.PLAIN, 13));

        passwordField.setPreferredSize(new Dimension(100, 20));
        limitInput.setPreferredSize(new Dimension(95, 20));
        run.setPreferredSize(new Dimension(120, 20));
        run.setText("Run Server!");
        portInput = new JComboBox<String>(TextUtils.range(12111, 12411));

        final JPanel zero = new JPanel();
        zero.setPreferredSize(new Dimension(10, 20));

        topPanel.setPreferredSize(new Dimension(600, 150));
        topPanel.setLayout(new FlowLayout());
        topPanel.add(welcomeMessage);
        topPanel.add(limitLabel);
        topPanel.add(limitInput);
        topPanel.add(zero);
        topPanel.add(portLabel);
        topPanel.add(portInput);
        topPanel.add(passLabel);
        topPanel.add(passwordField);
        topPanel.add(status);
        topPanel.add(run);

        final JPanel one = new JPanel();
        final JPanel two = new JPanel();
        final JPanel three = new JPanel();
        one.setPreferredSize(new Dimension(50, 400));
        two.setPreferredSize(new Dimension(50, 400));
        three.setPreferredSize(new Dimension(600, 50));

        status.setFont(new Font("Dialog", Font.PLAIN, 12));
        console.setEditable(false);

        consolePanel.setLayout(new BorderLayout());
        consolePanel.add(status, BorderLayout.NORTH);
        consolePanel.add(consolePane, BorderLayout.CENTER);

        Pane.add(two, BorderLayout.EAST);
        Pane.add(one, BorderLayout.WEST);
        Pane.add(three, BorderLayout.SOUTH);
        Pane.add(consolePanel, BorderLayout.CENTER);
        Pane.add(topPanel, BorderLayout.NORTH);

        setServerRunState(ServerRunState.STOPPED);
        configureGUIActions();
        setVisible(true);
    }

    private void configureGUIActions()
    {
        this.run.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                switch (serverRunState)
                {
                case RUNNING:
                    killServer();
                    setServerRunState(ServerRunState.STOPPED);
                    break;

                case STOPPED:
                    runServer();
                    setServerRunState(ServerRunState.RUNNING);
                }
            }
        });
    }

    @Override
    public void update(Observable o, Object arg)
    {
        LOGGER.info("Interpreting message from notifier");
        final HashMap<String, Object> argument = (HashMap<String, Object>) arg;
        final INotifier.Type messageType = (INotifier.Type) argument.keySet()
                .toArray()[0];
        final String message;

        switch (messageType)
        {
        case CONSOLE_MESSAGE:
            message = (String) argument.get(messageType);
            interpretConsoleMessage(message);
            break;

        case ERROR_MESSAGE:
            message = (String) argument.get(messageType);
            appendConsole("<font color=red> " + message + "</font><br>");
            break;

        case ERROR_EXCEPTION:
            final Exception e = (Exception) argument.get(messageType);
            appendConsole("<font color=red> " + e + "</font><br>");
            break;

        case STACK_TRACE:
            final StackTraceElement[] st = (StackTraceElement[]) argument
                    .get(messageType);
            appendConsole("<font color=purple>");
            for (final StackTraceElement line : st)
            {
                appendConsole(line.toString() + "<br>");
            }
            appendConsole("</font><br>");
            break;
        }
    }

    public void interpretConsoleMessage(String message)
    {
        final String lcMessage = message.toLowerCase();
        if (lcMessage.contains("successful connection made"))
        {
            message = "<font color=green>" + message + "</font>";
            connections++;
            status.setText("Server Running!  No. Connected: " + connections);
        } else if (lcMessage.contains("client has disconnected"))
        {
            message = "<font color=maroon>" + message + "</font>";
            connections--;
            status.setText("Server Running!  No. Connected: " + connections);
        } else if (lcMessage.contains("control has been"))
        {
            message = "<font color=blue>" + message + "</font>";
        } else if (lcMessage.contains("refused"))
        {
            message = "<font color=purple>" + message + "</font>";
        } else if (lcMessage.contains("data published by"))
        {
            message = "<font color=#FF8C00>" + message + "</font>";
        } else
        {
            // stay black
        }
        appendConsole(message);
    }

    public void appendConsole(String message)
    {
        try
        {
            kit.insertHTML(doc, doc.getLength(), message, 0, 0, null);
        } catch (final BadLocationException e)
        {
            System.out.println(e + " " + e.getMessage());
        } catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    private void runServer()
    {
        try
        {
            final char[] password = passwordField.getPassword();
            final int limit = Integer.parseInt(limitInput.getText());
            if (limit > 10 || limit < 1)
            {
                throw new NumberFormatException();
            }
            final int port = Integer.parseInt((String) portInput
                    .getSelectedItem());

            notifier = new GUINotifier();
            notifier.addObserver(this);

            // server = new Server(notifier, limit, password);
            server = new Server(notifier, limit, password, port);
            serverThread = new Thread((Runnable) server);
            serverThread.start();
            setServerRunState(ServerRunState.RUNNING);

        } catch (final NumberFormatException e)
        {
            JOptionPane
                    .showMessageDialog(this,
                            "Error: Limit of connections must be an integer between 1 and 10");
        }
    }

    private void setServerRunState(ServerRunState runState)
    {
        switch (runState)
        {
        case RUNNING:
            run.setText("Kill Server");
            setTitle("JavaPad Server - Running");
            serverRunState = runState;
            passwordField.setEnabled(false);
            limitInput.setEnabled(false);
            portInput.setEnabled(false);
            break;

        case STOPPED:
            run.setText("Run Server");
            setTitle("JavaPad Server");
            status.setText("Not Currently Running");
            serverRunState = runState;
            passwordField.setEnabled(true);
            limitInput.setEnabled(true);
            portInput.setEnabled(true);
            break;
        }
    }

    private void killServer()
    {
        server.kill();
        // serverThread.interrupt();
    }

    public JavaPadServerGUI()
    {
        constructGUI();
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        new JavaPadServerGUI();
    }

}
