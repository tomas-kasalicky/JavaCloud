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

/**
 *
 * @author Tomáš
 */
//Class configuration works as singleton
public class ServerConfigurations1 {

    protected static final ServerConfigurations1 instance = new ServerConfigurations1();
    protected String workingDirectoryName;
    protected String workingDirectoryAbsolutePath;
    protected String configFileName;
    protected String configFilesDirectory;
    protected String timeStampsFileName;
    protected final static int socket = 1111;

    
    
    public static String getWorkingDirectoryName() {
        return getInstance().workingDirectoryName;
    }

    protected ServerConfigurations1() {
        this.configFilesDirectory = System.getProperty("java.class.path") + System.getProperty("file.separator");
        this.configFileName = configFilesDirectory + "config.txt";
        this.timeStampsFileName = configFilesDirectory + "timestamps.txt";
        getWorkingDirectoryFromConfigFile(System.getProperty("user.home") + System.getProperty("file.separator") + "cloudServer");
        this.workingDirectoryAbsolutePath = (new File(this.workingDirectoryName)).getAbsolutePath();
    }

    public static ServerConfigurations1 getInstance() {
        return instance;
    }

    static public String getWorkingDirectoryAbsolutePath() {
        return getInstance().workingDirectoryAbsolutePath;
    }

    static public void setWorkingDirectory(String workingDirectory) {
        getInstance().workingDirectoryName = workingDirectory;
        getInstance().setWorkingDirectoryInConfigFile();
    }

    static public String getTimeStampsFileName() {
        return getInstance().timeStampsFileName;
    }

    static public void setTimeStampsFileName(String timeStampsFileName) {
        getInstance().timeStampsFileName = timeStampsFileName;
    }

    protected void getWorkingDirectoryFromConfigFile(String defaultWorkingDirectory) {
        try {
            FileReader readChar = new FileReader(configFileName);
            BufferedReader readLine = new BufferedReader(readChar);
            workingDirectoryName = readLine.readLine();
        } catch (IOException ex) {
            this.workingDirectoryName = defaultWorkingDirectory;//ToDo ask user for defining our working directory!
            setWorkingDirectoryInConfigFile();
        }
    }

    protected void setWorkingDirectoryInConfigFile() {
        try {
            PrintWriter configWriter = new PrintWriter(new FileWriter(configFileName));
            configWriter.println(workingDirectoryName);
            configWriter.close();
        } catch (IOException ex1) {
            System.out.println("Unable to write! Check your file acces permissions.");
        }
    }

    static public int getSocket() {
        return socket;
    }
}
