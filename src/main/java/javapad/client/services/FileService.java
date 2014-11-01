package javapad.client.services;

import javapad.client.views.JavaPadView;
import javapad.shared.utils.FileUtils;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by conor on 20/10/2014.
 */
public class FileService {

    /** Logger for this class */
    private static Logger LOGGER = Logger.getLogger(FileService.class
            .getSimpleName());

    public void save(JavaPadView view) {
        try
        {
            final JFileChooser saver = new JFileChooser();
            final int option = saver.showSaveDialog(view);
            if (option == JFileChooser.APPROVE_OPTION)
            {
                final String path = saver.getSelectedFile().getPath();
                FileUtils.saveTextFile(path, view.getSyntaxTextArea().getText());
            }
        } catch (final IOException e)
        {
            LOGGER.warning("IO Exception when saving: " + e.getMessage());
            view.showMessage("Could not save File: " + e.getMessage());
        }
    }

    public String open(JavaPadView view) {
        try
        {
            final JFileChooser opener = new JFileChooser(); // opens filechooser
            final int option = opener.showOpenDialog(view);
            if (option == JFileChooser.APPROVE_OPTION)
            {
                final String path = opener.getSelectedFile().getPath();
                final String toSet = FileUtils.openTextFile(path);
                return toSet;
            }
        } catch (final FileNotFoundException e)
        {
            LOGGER.warning("File not found exception in open: "
                    + e.getMessage());
            view.showMessage("The File you selected was not found");
        }
        return null;
    }
}
