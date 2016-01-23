/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import configurations.Configurations;
import configurations.TimeStamps;
import gui.Gui;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tomáš
 */
public class Client {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("CLIENT");
        Configurations.init("client");
        
        /* TimeStamps variables are used to store informations about files in the watched folder
         * and about files on the server.
         */
        Map<String, Long> timeStamps = new TimeStamps<String, Long>(Configurations.getTimeStampsFileName());
        Map<String, Long> remoteTimeStamps = new TimeStamps<String, Long>(Configurations.getRemoteTimeStampsFileName());

        /* Put reference to timeStamps in configurations class,
         * that makes the timeStamps accesible from anywhere.
         */
        Configurations.setTimeStamps(timeStamps);
        Configurations.setRemoteTimeStamps(remoteTimeStamps);
        System.out.println(Configurations.getWorkingDirectoryName());

        /*In these lists I'm gonna put files to upload and to download
         * I use them almost like Queue but I need each file to put in the list only once,
         * that's why I use Sets instead of Queues.
         */
        Set<String> newLocalFiles = new HashSet<String>();
        Set<String> deletedLocalFiles = new HashSet<String>();
        Set<String> newRemoteFiles = new HashSet<String>();
        Set<String> deletedRemoteFiles = new HashSet<String>();

        /* Load data from files, saved in last session*/
        TimeStamps.getNamesFromFile(newLocalFiles, Configurations.getNewLocalFileName());
        TimeStamps.getNamesFromFile(deletedLocalFiles, Configurations.getDeletedLocalFileName());
        TimeStamps.getNamesFromFile(newRemoteFiles, Configurations.getNewRemoteFileName());
        TimeStamps.getNamesFromFile(deletedRemoteFiles, Configurations.getDeletedRemoteFileName());

        //Start gui
        Gui gui = new Gui();

        TimeStampsHandler timeStampsHandler = new TimeStampsHandler(timeStamps, newLocalFiles, deletedLocalFiles);
        FileTransferHandler fileTransferHandler = new FileTransferHandler(gui, timeStamps, remoteTimeStamps, newLocalFiles, deletedLocalFiles, newRemoteFiles, deletedRemoteFiles);

        Thread timeStampsThread = new Thread(timeStampsHandler);
        Thread fileTransferThread = new Thread(fileTransferHandler);

        /*If it's first Start, wait for user to do the Settings,
         * after closing Settings the firstStart flag will be changed to false
          */
        while(Configurations.isFirstStart()){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        /* There are two threads in parallel, one is for watching the folder and updating timeStamps of changed files
         * and the other one's job is to communicate with server.
         */
        timeStampsThread.start();
        fileTransferThread.start();
    }
}
