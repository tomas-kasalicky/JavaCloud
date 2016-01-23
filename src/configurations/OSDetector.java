/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package configurations;

/**
 *
 * @author Tomáš
 */
public class OSDetector {

    private static String OSName = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {

        return (OSName.indexOf("win") >= 0);

    }

    public static boolean isMac() {

        return (OSName.indexOf("mac") >= 0);

    }

    public static boolean isUnix() {

        return (OSName.indexOf("nix") >= 0 || OSName.indexOf("nux") >= 0 || OSName.indexOf("aix") > 0);

    }

    public static boolean isSolaris() {

        return (OSName.indexOf("sunos") >= 0);

    }
}
