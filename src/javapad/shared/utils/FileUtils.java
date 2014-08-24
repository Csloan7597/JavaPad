package javapad.shared.utils;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public final class FileUtils
{

    /**
     * Saves a text file to a given path
     * 
     * @param path
     *            The path to save this item
     * @param toWrite
     *            A String representation of the text file to write
     * @throws IOException
     * @throws Exception
     */
    public static void saveTextFile(String path, String toWrite)
            throws IOException
    {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(path)))
        {
            out.write(toWrite);
        }
    }

    /**
     * Opens a text file at a specific path, returning it as a String
     * 
     * @param path
     *            Path to open
     * @return The text file (as string)
     * @throws FileNotFoundException
     * @throws Exception
     */
    public static String openTextFile(String path) throws FileNotFoundException
    {
        try (Scanner scan = new Scanner(new FileReader(path)))
        {
            String file = "";
            while (scan.hasNext())
            {
                file = file + (scan.nextLine() + "\n");
            }
            scan.close();
            return file;
        }
    }

}
