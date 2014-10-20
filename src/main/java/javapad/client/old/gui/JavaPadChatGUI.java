package javapad.client.old.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class JavaPadChatGUI extends JFrame
{

    /**
     * 
     */
    private static final long serialVersionUID = -1301218930530630554L;
    private static final String TEXT_SUBMIT = "text-submit";
    private static final String INSERT_BREAK = "insert-break";

    private static final Logger LOGGER = Logger.getLogger(JavaPadChatGUI.class
            .getSimpleName());

    public String myName;
    private MulticastSocket myChatSocket;
    private InetAddress group;
    private IntermittentChatReaderThread myChatReaderThread;

    private final JButton sendChat = new JButton();
    private JScrollPane chatScroll;
    private final JTextArea chatToSend = new JTextArea();
    private JScrollPane chatSendScroll;
    private final JPanel bottomPanel = new JPanel();
    private final JTextPane chatBox = new JTextPane();
    private final HTMLEditorKit kit = new HTMLEditorKit();
    private final HTMLDocument doc = new HTMLDocument();

    public JavaPadChatGUI()
    {
        myName = JOptionPane.showInputDialog("What is your name?");
        constructGUI();
        createUDPSocket();
    }

    public JavaPadChatGUI(InetAddress server)
    {
        myName = JOptionPane.showInputDialog("What is your name?");
        constructGUI();
        createUDPSocket();
    }

    private void createUDPSocket()
    {
        try
        {
            myChatSocket = new MulticastSocket(9877);
            group = InetAddress.getByName("228.5.6.7");
            myChatSocket.joinGroup(group);
            LOGGER.info("I have joined the group!");

            myChatReaderThread = new IntermittentChatReaderThread();
            myChatReaderThread.execute();

        } catch (final IOException e)
        {
            LOGGER.severe("Error connecting to multicast: " + e.getMessage());
        }
    }

    public void constructGUI()
    {
        chatBox.setEditorKit(kit);
        chatBox.setDocument(doc);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        setSize(500, 600);
        setResizable(false);
        setLocationByPlatform(true);

        chatBox.setSize(new Dimension(500, 500));
        chatBox.setEditable(false);
        chatToSend.setSize(100, 100);
        sendChat.setText("<html><b><i>Send</i></b></html>");

        chatToSend.setLineWrap(true);
        // chatBox.setLineWrap(true);

        chatSendScroll = new JScrollPane(chatToSend,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        chatScroll = new JScrollPane(chatBox,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        bottomPanel.setPreferredSize(new Dimension(500, 100));
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(chatSendScroll, BorderLayout.CENTER);
        bottomPanel.add(sendChat, BorderLayout.EAST);

        getContentPane().add(chatScroll, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        setVisible(false);
        setTitle("JavaPad - Chat");
        configureGUIActions();

        final DefaultCaret caret = (DefaultCaret) chatToSend.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    public void configureGUIActions()
    {
        sendChat.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    sendChat();
                    // /dispose(); // close GUI
                } catch (final Exception ex)
                {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });// end sendChat

        final InputMap input = chatToSend.getInputMap();
        final KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
        final KeyStroke shiftEnter = KeyStroke.getKeyStroke("shift ENTER");
        input.put(shiftEnter, INSERT_BREAK);
        input.put(enter, TEXT_SUBMIT);

        final ActionMap actions = chatToSend.getActionMap();
        actions.put(TEXT_SUBMIT, new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                sendChat();
            }
        });
    }

    public void destroy()
    {
        if (myChatSocket != null)
        {
            myChatSocket.close();
        }
        this.dispose();
    }

    public void sendChat()
    {
        final String message = myName + ": " + chatToSend.getText();
        byte[] sendData = new byte[1024];
        sendData = message.getBytes();
        final DatagramPacket sendPacket = new DatagramPacket(sendData,
                sendData.length, group, 9877);
        try
        {
            myChatSocket.send(sendPacket);
            System.out.println("Sent chat!");
            chatToSend.setText("");
        } catch (final IOException e)
        {
            LOGGER.severe("Error sending chat: " + e.getMessage());
        }
    }

    public void appendChat(String message)
    {
        try
        {
            kit.insertHTML(doc, doc.getLength(), message, 0, 0, null);
        } catch (final BadLocationException e)
        {
            e.printStackTrace();
        } catch (final IOException e)
        {
            e.printStackTrace();
        }

    }

    public void interpretChatMessage(String message)
    {
        if (message.contains("SYSTEM: "))
        {
            appendChat("<font color = red><b>" + message + "</b></font>\n");
        } else
        {
            final int indexOfColon = message.indexOf(':');

            final String name = message.substring(0, indexOfColon);
            final String rest = message.substring(indexOfColon);
            String colour;

            if (name.equals(myName))
            {
                colour = "purple";
            } else
            {
                colour = "blue";
            }
            appendChat("<html><font color='" + colour + "'<b>" + name
                    + "</b></font> " + rest + "</html>");
        }
    }

    class IntermittentChatReaderThread extends SwingWorker<Void, String>
    {
        @SuppressWarnings("unchecked")
        @Override
        protected Void doInBackground() throws Exception
        {
            while (true)
            {
                // to fill with data
                LOGGER.info("The messenger reader is running");
                final byte[] receiveData = new byte[1024];
                final DatagramPacket receivePacket = new DatagramPacket(
                        receiveData, receiveData.length);
                myChatSocket.receive(receivePacket);
                final String sentence = new String(receivePacket.getData());
                publish(sentence);

                if (chatBox == null)
                {
                    break;
                }
            }
            return null;
        }

        @Override
        protected void process(List<String> chunks)
        {
            // here we check what type it is and add stuff.
            interpretChatMessage(chunks.get(0));
        }
    }

    // TEST
    public static void main(String[] args)
    {
        final JFrame chatGUI = new JavaPadChatGUI();
        chatGUI.setVisible(true);
    }

}
