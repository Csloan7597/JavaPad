package javapad.shared.utils;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.Map;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

public final class TextUtils
{

    /**
     * Given a Graphics environment, returns the list of fonts available on this
     * system as an array of strings, to be used in a GUI application
     * 
     * @param graphicsEnv
     *            The Graphics Environment to use
     * @return A list of font names
     */
    public static String[] getFonts(GraphicsEnvironment graphicsEnv)
    {
        // Get all fonts for this environment
        final Font[] fonts = graphicsEnv.getAllFonts();

        // Add them to an array of Strings
        final String[] fontNames = new String[fonts.length];
        for (int x = 0; x < fonts.length; x++)
        {
            fontNames[x] = fonts[x].getFontName();
        }
        return fontNames;
    }

    /**
     * Simply returns a list of Strings representing font sizes from 1-30, for
     * use in a GUI application
     * 
     * @return A list of sizes as strings
     */
    public static String[] getFontSizes()
    {
        final String[] sizes = new String[30];

        for (int i = 1; i < 31; i++)
        {
            sizes[i - 1] = Integer.toString(i);
        }

        return sizes;
    }

    /**
     * Returns a Mapping of Language name to Syntax constant for use with the
     * RSyntaxTextArea
     * 
     * @return Mapping of Lang name to syntax constant
     */
    public static Map<String, String> getSyntaxModes()
    {
        final Map<String, String> syntax_constants = new HashMap<String, String>();

        syntax_constants.put("ActionScript",
                SyntaxConstants.SYNTAX_STYLE_ACTIONSCRIPT);
        syntax_constants.put("Assembler",
                SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_X86);
        syntax_constants.put("BBCode", SyntaxConstants.SYNTAX_STYLE_BBCODE);
        syntax_constants.put("C", SyntaxConstants.SYNTAX_STYLE_C);
        syntax_constants.put("Clojure", SyntaxConstants.SYNTAX_STYLE_CLOJURE);
        syntax_constants.put("C++", SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
        syntax_constants.put("CSS", SyntaxConstants.SYNTAX_STYLE_CSS);
        syntax_constants.put("Delphi", SyntaxConstants.SYNTAX_STYLE_DELPHI);
        syntax_constants.put("Fortran", SyntaxConstants.SYNTAX_STYLE_FORTRAN);
        syntax_constants.put("Groovy", SyntaxConstants.SYNTAX_STYLE_GROOVY);
        syntax_constants.put("HTML", SyntaxConstants.SYNTAX_STYLE_HTML);
        syntax_constants.put("Java", SyntaxConstants.SYNTAX_STYLE_JAVA);
        syntax_constants.put("JavaScript",
                SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        syntax_constants.put("JSP", SyntaxConstants.SYNTAX_STYLE_JSP);
        syntax_constants.put("Lisp", SyntaxConstants.SYNTAX_STYLE_LISP);
        syntax_constants.put("LUA", SyntaxConstants.SYNTAX_STYLE_LUA);
        syntax_constants.put("Perl", SyntaxConstants.SYNTAX_STYLE_PERL);
        syntax_constants.put("PHP", SyntaxConstants.SYNTAX_STYLE_PHP);
        syntax_constants.put("Properties File",
                SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE);
        syntax_constants.put("Python", SyntaxConstants.SYNTAX_STYLE_PYTHON);
        syntax_constants.put("Ruby", SyntaxConstants.SYNTAX_STYLE_RUBY);
        syntax_constants.put("SAS", SyntaxConstants.SYNTAX_STYLE_SAS);
        syntax_constants.put("Scala", SyntaxConstants.SYNTAX_STYLE_SCALA);
        syntax_constants.put("SQL", SyntaxConstants.SYNTAX_STYLE_SQL);
        syntax_constants.put("TCL", SyntaxConstants.SYNTAX_STYLE_TCL);
        syntax_constants.put("Unix Shell",
                SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL);
        syntax_constants.put("Windows Batch File",
                SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH);
        syntax_constants.put("XML", SyntaxConstants.SYNTAX_STYLE_XML);
        syntax_constants.put("None", SyntaxConstants.SYNTAX_STYLE_NONE);

        return syntax_constants;
    }

    /**
     * Given an int start and finish, returns a String array of string
     * representations of the range between the integers given.
     * 
     * @param start
     *            The lower number to start at
     * @param stop
     *            The higher number to finish at
     * @return The String array of numbers
     */
    public static String[] range(int start, int stop)
    {
        final String[] result = new String[stop - start];

        for (int i = 0; i < stop - start; i++)
            result[i] = Integer.toString(start + i);

        return result;
    }

}
