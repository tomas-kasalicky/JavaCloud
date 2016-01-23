/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package configurations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 *
 * @author Tomáš
 */
public class Configurations {
    /* The Configurations class works as singleton, 
     * because I need it to be accesible as good as the static class is,
     * but I need constructor to initialize settings from files */

    protected static final String programName = "CzechCloud";
    protected static String remoteTimeStampsFileName, newLocalFileName, deletedLocalFileName, newRemoteFileName, deletedRemoteFileName;
    static protected String host;
    protected static String password;
    protected static boolean firstStart;
    protected static String workingDirectoryName, defaultWorkingDirectoryName;
    protected static String configFilesDirectory;
    protected static String timeStampsFileName;
    protected final static int socket = 1111;
    protected static Map<String, Long> timeStamps;
    protected static Map<String, Long> remoteTimeStamps;

    static public void init(String folderName) {
        if (OSDetector.isWindows()) {
            configFilesDirectory = System.getenv("APPDATA");
        } else {
            configFilesDirectory = System.getProperty("user.home");
        }
        configFilesDirectory += System.getProperty("file.separator") + "." + programName + folderName + System.getProperty("file.separator");
        File folder = new File(configFilesDirectory);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        firstStart = getFirstStart();
        timeStampsFileName = configFilesDirectory + "ctimestamps.cnf";
        defaultWorkingDirectoryName = System.getProperty("user.home") + System.getProperty("file.separator") + programName + folderName;
        workingDirectoryName = getItemFromConfigFile("folder.cnf", defaultWorkingDirectoryName);
        remoteTimeStampsFileName = configFilesDirectory + "cremote_timestamps.cnf";
        newLocalFileName = deletedLocalFileName = newRemoteFileName = deletedRemoteFileName = configFilesDirectory;
        newLocalFileName += "newlocal.cnf";
        deletedLocalFileName += "deletedlocal.cnf";
        newRemoteFileName += "newremote.cnf";
        deletedRemoteFileName += "deletedremote.cnf";

        password = getItemFromConfigFile("pass.cnf", "");
        host = getItemFromConfigFile("hostname.cnf", "localhost");
    }

    public static String getProgramName() {
        return programName;
    }

    static public int getSocket() {
        return socket;
    }

    public static String getDefaultWorkingDirectoryName() {
        return defaultWorkingDirectoryName;
    }

    static public String getRemoteTimeStampsFileName() {
        return remoteTimeStampsFileName;
    }

    static public String getHost() {
        return host;
    }

    static public String getNewLocalFileName() {
        return newLocalFileName;
    }

    static public String getDeletedLocalFileName() {
        return deletedLocalFileName;
    }

    static public String getNewRemoteFileName() {
        return newRemoteFileName;
    }

    static public String getDeletedRemoteFileName() {
        return deletedRemoteFileName;
    }

    static public boolean isFirstStart() {
        return firstStart;
    }

    public static String getWorkingDirectoryName() {
        return workingDirectoryName;
    }

    static public String getPassword() {
        return password;
    }

    static public void setWorkingDirectory(String workingDirectory) {
        workingDirectoryName = workingDirectory;
        setItemInConfigFile("folder.cnf", workingDirectory);
    }

    static public String getTimeStampsFileName() {
        return timeStampsFileName;
    }

    static public void setTimeStampsFileName(String newTimeStampsFileName) {
        timeStampsFileName = newTimeStampsFileName;
    }

    static protected String getItemFromConfigFile(String fileName, String defaultValue) {
        try {
            FileReader readChar = new FileReader(configFilesDirectory + fileName);
            BufferedReader readLine = new BufferedReader(readChar);
            return readLine.readLine();
        } catch (IOException ex) {
            setItemInConfigFile(fileName, defaultValue);
            return defaultValue;
        }
    }

    static protected void setItemInConfigFile(String fileName, String item) {
        try {
            File file = new File(configFilesDirectory + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter configWriter = new PrintWriter(new FileWriter(file));
            configWriter.println(item);
            configWriter.close();
        } catch (IOException ex1) {
            System.out.println("Unable to write! Check your file acces permissions: " + fileName + "\n" + ex1);
        }
    }

    static public void setTimeStamps(Map<String, Long> newTimeStamps) {
        timeStamps = newTimeStamps;
    }

    static public void setRemoteTimeStamps(Map<String, Long> timeStamps) {
        remoteTimeStamps = timeStamps;
    }

    static public void setHost(String newHost) {
        host = newHost;
        setItemInConfigFile("hostname.cnf", host);
    }

    static public void setPassword(String newPassword) {
        password = newPassword;
        setItemInConfigFile("pass.cnf", password);
    }

    static public void setFirstStart(boolean state) {
        firstStart = state;
        if (!state) {
            setItemInConfigFile("psc.cnf", "");
        } else {
            File file = new File(configFilesDirectory + "psc.cnf");
            file.delete();
        }
    }

    static public void clearTimeStamps() {
        timeStamps.clear();
        remoteTimeStamps.clear();
    }

    static private boolean getFirstStart() {
        File file = new File(configFilesDirectory + "psc.cnf");
        return !file.exists();
    }
}
