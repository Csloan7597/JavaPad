package javapad.client.old.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.logging.Logger;

import javapad.client.old.connection.JPConnectionManager;
import javapad.client.old.interfaces.IConnectionManager;
import javapad.shared.utils.FileUtils;
import javapad.shared.utils.JavaPadMessage;
import javapad.shared.utils.JavaPadMessage.MessageType;
import javapad.shared.utils.TextUtils;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 * The GUI of JavaPad v2. This GUI uses an IConnectionManager to communicate
 * with a Server.
 * 
 * @author conor
 * 
 */
public class JavaPadGUI extends JFrame implements Observer
{
    private static final long serialVersionUID = -1740244988900922227L;

    /** Logger for this class */
    private static Logger LOGGER = Logger.getLogger(JavaPadGUI.class
            .getSimpleName());

    public enum ConnectionState
    {
        CONNECTED_NO_CONTROL, CONNECTED_IN_CONTROL, DISCONNECTED;
    }

    /** GUI Items */
    private final JMenuBar menuBar = new JMenuBar();
    private final JMenu file = new JMenu("File");
    private final JMenuItem open = new JMenuItem("Open");
    private final JMenuItem save = new JMenuItem("Save");
    private final JMenuItem close = new JMenuItem("Close");
    private final JMenuItem mode = new JMenuItem("Change Syntax Mode");
    private final JMenuItem font = new JMenuItem("Change Font");
    private final JMenu network = new JMenu("Network");
    private final JMenuItem sendData = new JMenuItem("Send Data");
    private final JMenuItem toggleControl = new JMenuItem("Request Control");
    private final JMenuItem connect = new JMenuItem("Connect to Network");
    private final JMenuItem disconnect = new JMenuItem("Disconnect");
    private final JMenu chat = new JMenu("Chat");
    private final JMenuItem openChat = new JMenuItem("Open Chat Window");

    private final RSyntaxTextArea syntaxTextArea = new RSyntaxTextArea();
    private final RTextScrollPane sp = new RTextScrollPane(syntaxTextArea);

    // Syntax Changer Vars
    private final Map<String, String> syntaxConstants = TextUtils
            .getSyntaxModes();;
    private String[] syntaxOptions;
    private final JDialog syntaxChanger = new JDialog(this);
    private final JLabel syntaxMessage = new JLabel(
            "Please choose a new syntax highlighter");
    private JComboBox<String> syntaxSelector;
    private final JButton syntaxOkButton = new JButton("Ok");

    // Font Changer Vars
    private final JDialog fontChanger = new JDialog(this);
    private final JLabel fontMessage = new JLabel(
            "Please choose a font style and size");
    private JComboBox<String> fontSelector;
    private JComboBox<String> fontSizeSelector; // these are instantiated later
    private final JButton fontOkButton = new JButton("Ok");

    // Connection Form
    private final JDialog connectionDialog = new JDialog(this);
    private final JLabel connectionMessage1 = new JLabel("IP address:");
    private final JTextField ipEntryField = new JTextField(15);
    private final JLabel connectionMessage2 = new JLabel("Port: \t");
    private JComboBox<String> portEntryField;
    private final JLabel connectionMessage3 = new JLabel(
            "\nPassword (Tick if needed): ");
    private final JPasswordField connectPasswordField = new JPasswordField(15);
    private final JCheckBox passwdCheck = new JCheckBox();
    private final JButton connectOkButton = new JButton("Connect");

    private JavaPadChatGUI chatWindow;
    private IConnectionManager connectionManager;
    private ConnectionState GUIState;

    /** GUI Setup Logic */

    private void constructGUI()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception e)
        {
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

        // file menu design
        menuBar.add(file);
        menuBar.add(network);
        menuBar.add(chat);

        configureGUIActions();
        setAccelerators();
        setUpFonts();
        setUpSyntax();
        setUpConnectionDialog();

        getContentPane().add(sp);

        setGUIState(ConnectionState.DISCONNECTED);
        setVisible(true);
    }

    private void setUpSyntax()
    {
        final Set<String> x = syntaxConstants.keySet();
        syntaxOptions = x.toArray(new String[29]);

        syntaxSelector = new JComboBox<String>(syntaxOptions);
        syntaxChanger.setTitle("Choose a new Syntax Style");
        syntaxChanger.setLayout(new FlowLayout());
        syntaxChanger.setSize(300, 100);
        syntaxChanger.add(syntaxMessage);
        syntaxChanger.add(syntaxSelector);
        syntaxChanger.add(syntaxOkButton);

        syntaxChanger.setLocationRelativeTo(this);
        syntaxChanger.setVisible(false);
        syntaxChanger.getRootPane().setDefaultButton(syntaxOkButton);

        // default
        syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        syntaxTextArea.setSyntaxEditingStyle(syntaxConstants.get("Java"));
        syntaxSelector.setSelectedItem("Java");
    }

    private void setUpFonts()
    {
        final Font defaultFont = new Font("Times New Roman", Font.PLAIN, 16);
        syntaxTextArea.setFont(defaultFont);

        final GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        final String[] fonts = TextUtils.getFonts(ge);

        fontSelector = new JComboBox<String>(fonts);
        fontSizeSelector = new JComboBox<String>(TextUtils.getFontSizes());
        fontSelector.setSelectedItem("Times New Roman");
        fontSizeSelector.setSelectedItem("16");

        fontChanger.setSize(400, 115);
        fontChanger.setTitle("Choose a new Font");
        fontChanger.setLayout(new FlowLayout());
        fontChanger.setLocationRelativeTo(this);
        fontChanger.add(fontMessage);
        fontChanger.add(fontSelector);
        fontChanger.add(fontSizeSelector);
        fontChanger.add(fontOkButton);

        fontChanger.getRootPane().setDefaultButton(fontOkButton);
        fontChanger.setVisible(false);
    }

    private void setUpConnectionDialog()
    {
        portEntryField = new JComboBox<String>(TextUtils.range(12111, 12141));

        // connectionDialog.setLocationByPlatform(true);
        connectionDialog.setSize(215, 200);
        connectionDialog.setResizable(false);
        connectionDialog.getRootPane().setDefaultButton(connectOkButton);
        connectPasswordField.setEnabled(false);
        connectionDialog.setLayout(new FlowLayout(FlowLayout.CENTER));
        connectionDialog.setLocationRelativeTo(this);
        connectionDialog.setTitle("Connect");
        connectionDialog.add(connectionMessage1);
        connectionDialog.add(ipEntryField);
        connectionDialog.add(connectionMessage2);
        connectionDialog.add(portEntryField);
        connectionDialog.add(connectionMessage3);
        connectionDialog.add(connectPasswordField);
        connectionDialog.add(passwdCheck);
        connectionDialog.add(connectOkButton);
        connectionDialog.setVisible(false);
    }

    /**
     * Sets up accelerators for the GUI of this application
     */
    private void setAccelerators()
    {
        open.setAccelerator(KeyStroke.getKeyStroke('O',
                InputEvent.CTRL_DOWN_MASK));
        file.add(open);
        save.setAccelerator(KeyStroke.getKeyStroke('S',
                InputEvent.CTRL_DOWN_MASK));
        file.add(save);
        mode.setAccelerator(KeyStroke.getKeyStroke('M',
                InputEvent.CTRL_DOWN_MASK));
        file.add(mode);
        font.setAccelerator(KeyStroke.getKeyStroke('F',
                InputEvent.CTRL_DOWN_MASK));
        file.add(font);
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

    private void configureGUIActions()
    {
        close.addActionListener(new ActionListener()
        {
            
            public void actionPerformed(ActionEvent e)
            {
                // Disconnect from the thing TODO
                System.exit(0);
            }
        });// end of closer
        open.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                open();
            }
        });// end of file opener
        save.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                save();
            }
        });// end of save file
        mode.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                syntaxChanger.setVisible(true);
            }
        });// end of mode listener
        syntaxOkButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                changeSyntaxStyle();
                syntaxChanger.setVisible(false);
            }
        });
        font.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                fontChanger.setVisible(true);
            }
        });// end of changeFont
        fontOkButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                changeFont();
                fontChanger.setVisible(false);
            }
        });// end of changeFont
        sendData.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                sendData();
            }
        });// end of sending data
        connect.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                connectionDialog.setVisible(true);
            }
        });// end of connector
        connectOkButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                connect();
                connectionDialog.setVisible(false);
            }
        });// end of connector
        passwdCheck.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                connectPasswordField.setEnabled(passwdCheck.isSelected());
            }
        });// end of connector
        disconnect.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                disconnect();
            }
        });// end of disconnector
        toggleControl.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                toggleControl();
            }
        });
        openChat.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
            {
                if (chatWindow != null)
                {
                    chatWindow.setVisible(true);
                }
            }
        });
    }

    /** Functional Code of GUI */
    public void setGUIState(ConnectionState state)
    {
        switch (state)
        {
        case DISCONNECTED:
            setTitle("JavaPad");
            syntaxTextArea.setEditable(true);
            chat.setEnabled(false);
            chat.setVisible(false);
            toggleControl.setEnabled(false);
            sendData.setEnabled(false);
            connect.setEnabled(true);
            disconnect.setEnabled(false);
            open.setEnabled(true);
            save.setEnabled(true);
            break;

        case CONNECTED_NO_CONTROL:
            setTitle("JavaPad - Connected");
            syntaxTextArea.setEditable(false);
            chat.setEnabled(true);
            chat.setVisible(true);
            toggleControl.setEnabled(true);
            toggleControl.setText("Request Control");
            sendData.setEnabled(false);
            connect.setEnabled(false);
            disconnect.setEnabled(true);
            open.setEnabled(false);
            save.setEnabled(true);
            break;

        case CONNECTED_IN_CONTROL:
            setTitle("JavaPad - Connected");
            syntaxTextArea.setEditable(true);
            chat.setEnabled(true);
            chat.setVisible(true);
            toggleControl.setEnabled(true);
            toggleControl.setText("Relinquish Control");
            sendData.setEnabled(true);
            connect.setEnabled(false);
            disconnect.setEnabled(true);
            open.setEnabled(true);
            save.setEnabled(true);
            break;
        }
        GUIState = state;
    }

    private void open()
    {
        try
        {
            final JFileChooser opener = new JFileChooser(); // opens filechooser
            final int option = opener.showOpenDialog(getOwner());
            if (option == JFileChooser.APPROVE_OPTION)
            {
                final String path = opener.getSelectedFile().getPath();
                final String toSet = FileUtils.openTextFile(path);
                syntaxTextArea.setText(toSet);
            }
        } catch (final FileNotFoundException e)
        {
            LOGGER.warning("File not found exception in open: "
                    + e.getMessage());
            showGUIMessage("The File you selected was not found");
        }
    }

    private void save()
    {
        try
        {
            final JFileChooser saver = new JFileChooser();
            final int option = saver.showSaveDialog(getOwner());
            if (option == JFileChooser.APPROVE_OPTION)
            {
                final String path = saver.getSelectedFile().getPath();
                FileUtils.saveTextFile(path, syntaxTextArea.getText());
            }
        } catch (final IOException e)
        {
            LOGGER.warning("IO Exception when saving: " + e.getMessage());
            showGUIMessage("Could not save File: " + e.getMessage());
        }
    }

    private void changeSyntaxStyle()
    {
        final String newStyle = (String) syntaxSelector.getSelectedItem();
        final String constant = syntaxConstants.get(newStyle);
        syntaxTextArea.setSyntaxEditingStyle(constant);
    }

    private void changeFont()
    {
        final String newfont = (String) fontSelector.getSelectedItem();
        final int newSize = Integer.parseInt((String) fontSizeSelector
                .getSelectedItem());
        final Font newFont = new Font(newfont, Font.PLAIN, newSize);
        this.syntaxTextArea.setFont(newFont);
    }

    private void connect()
    {
        if (connectionManager == null)
        {
            connectionManager = new JPConnectionManager(this);
        }
        setTitle("JavaPad - Connecting...");

        new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    final String ip = ipEntryField.getText();
                    final int port = Integer.parseInt((String) portEntryField
                            .getSelectedItem());
                    final char[] pass = connectPasswordField.getPassword();

                    connectionManager.connect(ip, pass, port);
                } catch (final IOException e)
                {
                    setGUIState(ConnectionState.DISCONNECTED);
                    LOGGER.severe("Error whilst connecting to Server: "
                            + e.getMessage());
                    showGUIMessage("Error whilst connecting to the Server: "
                            + e.getMessage());
                }
            }
        }.start();
    }

    private void disconnect()
    {
        try
        {
            connectionManager.disconnect();
            // setGUIState(ConnectionState.DISCONNECTED);
        } catch (final IOException e)
        {
            LOGGER.severe("Error whilst disconnecting from Server: "
                    + e.getMessage());
            showGUIMessage("Error whilst disconnecting from Server: "
                    + e.getMessage());
        }
    }

    private void toggleControl()
    {
        try
        {
            JavaPadMessage jpm;
            switch (GUIState)
            {
            case CONNECTED_NO_CONTROL:
                jpm = new JavaPadMessage(MessageType.CONTROL_REQUEST, "");
                connectionManager.sendJavaPadMessage(jpm);
                break;

            case CONNECTED_IN_CONTROL:
                jpm = new JavaPadMessage(MessageType.CONTROL_RELEASE, "");
                connectionManager.sendJavaPadMessage(jpm);
                break;

            default:
                break;
            }
        } catch (final IOException e)
        {
            LOGGER.severe("Error whilst toggling control: " + e.getMessage());
            showGUIMessage("Error whilst toggling control: " + e.getMessage());
        }
    }

    private void sendData()
    {
        try
        {
            connectionManager.sendJavaPadMessage(new JavaPadMessage(
                    MessageType.SEND_DATA, syntaxTextArea.getText()));
        } catch (final IOException e)
        {
            LOGGER.severe("Error when sending data: " + e.getMessage());
            showGUIMessage("Error when sending data: " + e.getMessage());
        }
    }

    public void showGUIMessage(String message)
    {
        JOptionPane.showMessageDialog(this, message);
    }

    public void update(Observable o, Object arg)
    {
        // Response to any kind of message that comes in:
        final JavaPadMessage jpm = (JavaPadMessage) arg;
        switch (jpm.getMessageType())
        {
        case CONNECT:
            showGUIMessage("You have connected successfully!");
            setGUIState(ConnectionState.CONNECTED_NO_CONTROL);
            chatWindow = new JavaPadChatGUI();
            chatWindow.setVisible(true);
            break;
        case CONNECT_DENIED:
            showGUIMessage("Error: Could not connect, message: "
                    + jpm.getMessageBody());
            break;
        case DISCONNECT:
            if (this.GUIState != ConnectionState.DISCONNECTED)
            {
                showGUIMessage("Server has disconnected");
                setGUIState(ConnectionState.DISCONNECTED);
            }
            break;
        case CONTROL_GRANTED:
            showGUIMessage("You have gained control!");
            setGUIState(ConnectionState.CONNECTED_IN_CONTROL);
            break;
        case CONTROL_DENIED:
            showGUIMessage("You have been denied control, message: "
                    + jpm.getMessageBody());
            break;
        case CONTROL_RELEASE:
            showGUIMessage("Control released");
            setGUIState(ConnectionState.CONNECTED_NO_CONTROL);
            break;
        case SEND_DATA:
            syntaxTextArea.setText(jpm.getMessageBody());
            showGUIMessage("New Data Received!");
            break;
        case SERVER_RESPONSE_ERROR:
            showGUIMessage("The server returned an error; Message: "
                    + jpm.getMessageBody());
            break;

        default:
            break;
        }
    }

    /** Runner */

    public static void main(String[] args)
    {
        new JavaPadGUI();
    }

    public JavaPadGUI()
    {
        LOGGER.info("Starting JavaPad");
        constructGUI();
    }

}
