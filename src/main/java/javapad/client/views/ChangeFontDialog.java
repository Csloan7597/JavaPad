package javapad.client.views;

import javapad.shared.utils.TextUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Created by conor on 20/10/2014.
 */
public class ChangeFontDialog {

    private final JDialog fontChanger;
    private final JLabel fontMessage = new JLabel(
            "Please choose a font style and size");
    private JComboBox<String> fontSelector;
    private JComboBox<String> fontSizeSelector; // these are instantiated later
    private final JButton fontOkButton = new JButton("Ok");

    public ChangeFontDialog(JavaPadView parent) {

        // Plug in to parent
        fontChanger = new JDialog(parent);

        fontSelector = new JComboBox<String>(getFontsFromEnvironment());
        fontSizeSelector = new JComboBox<String>(TextUtils.getFontSizes());
        fontSelector.setSelectedItem("Times New Roman");
        fontSizeSelector.setSelectedItem("16");

        fontChanger.setSize(400, 115);
        fontChanger.setTitle("Choose a new Font");
        fontChanger.setLayout(new FlowLayout());
        fontChanger.setLocationRelativeTo(parent);
        fontChanger.add(fontMessage);
        fontChanger.add(fontSelector);
        fontChanger.add(fontSizeSelector);
        fontChanger.add(fontOkButton);

        fontChanger.getRootPane().setDefaultButton(fontOkButton);
    }

    String[] getFontsFromEnvironment() {
        final GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        final String[] fonts = TextUtils.getFonts(ge);
        return fonts;
    }

    public void setFont(Font font, JTextArea textArea) {
        textArea.setFont(font);
    }

    public void show() {
        this.fontChanger.setVisible(true);
    }

    public void hide() {
        this.fontChanger.setVisible(false);
    }

    public JButton getFontOkButton() {
        return this.fontOkButton;
    }

    public String getCurrentFontName() {
        return (String) fontSelector.getSelectedItem();
    }

    public int getCurrentFontSize() {
        return Integer.parseInt((String) fontSizeSelector
                .getSelectedItem());
    }

    public Font getCurrentFont() {
        return new Font(getCurrentFontName(), Font.PLAIN, getCurrentFontSize());
    }
}
