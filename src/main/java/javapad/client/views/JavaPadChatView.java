package javapad.client.views;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.IOException;

/**
 * Created by conor on 25/10/2014.
 */
public class JavaPadChatView extends JFrame{

    private final JButton sendChat = new JButton();
    private JScrollPane chatScroll;
    private final JTextArea chatToSend = new JTextArea();
    private JScrollPane chatSendScroll;
    private final JPanel bottomPanel = new JPanel();
    private final JTextPane chatBox = new JTextPane();
    private final HTMLEditorKit kit = new HTMLEditorKit();
    private final HTMLDocument doc = new HTMLDocument();

    public JavaPadChatView() {
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

        final DefaultCaret caret = (DefaultCaret) chatToSend.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    public JButton getSendChat() {
        return sendChat;
    }

    public JTextArea getChatToSend() {
        return chatToSend;
    }

    public JTextPane getChatBox() {
        return chatBox;
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

    public void showChatView() {
        this.setVisible(true);
    }

    public void hideChatView() {
        this.setVisible(false);
    }
}
