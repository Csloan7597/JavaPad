package javapad.client.views;

import javapad.shared.utils.TextUtils;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Set;

/**
 * Created by conor on 20/10/2014.
 */
public class ChangeSyntaxModeDialog {

    // Syntax Changer Vars
    private final Map<String, String> syntaxConstants = TextUtils.getSyntaxModes();;
    private String[] syntaxOptions;
    private final JDialog syntaxChanger;
    private final JLabel syntaxMessage = new JLabel(
            "Please choose a new syntax highlighter");
    private JComboBox<String> syntaxSelector;
    private final JButton syntaxOkButton = new JButton("Ok");

    public ChangeSyntaxModeDialog(JavaPadView parent) {
        this.syntaxChanger = new JDialog(parent);

        final Set<String> x = syntaxConstants.keySet();
        syntaxOptions = x.toArray(new String[29]);

        syntaxSelector = new JComboBox<String>(syntaxOptions);
        syntaxChanger.setTitle("Choose a new Syntax Style");
        syntaxChanger.setLayout(new FlowLayout());
        syntaxChanger.setSize(300, 100);
        syntaxChanger.add(syntaxMessage);
        syntaxChanger.add(syntaxSelector);
        syntaxChanger.add(syntaxOkButton);

        syntaxChanger.setLocationRelativeTo(parent);
        syntaxChanger.setVisible(false);
        syntaxChanger.getRootPane().setDefaultButton(syntaxOkButton);
    }

    public void show() {
        this.syntaxChanger.setVisible(true);
    }

    public void hide() {
        this.syntaxChanger.setVisible(false);
    }

    public JComboBox<String> getSyntaxSelector() {
        return syntaxSelector;
    }

    public JButton getSyntaxOkButton() {
        return syntaxOkButton;
    }

    public String getCurrentSyntaxConstant() {
        return syntaxConstants.get(syntaxSelector.getSelectedItem());
    }
}
