/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import communication.FileReceiver;
import communication.FileSender;
import communication.MessageReceiver;
import communication.MessageSender;
import configurations.Configurations;
import configurations.TimeStamps;
import gui.Gui;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Tomáš
 */
public class FileTransferHandler implements Runnable {

    public Map<String, Long> timeStamps, remoteTimeStamps;
    public Set<String> newLocalFiles, deletedLocalFiles, newRemoteFiles, deletedRemoteFiles;
    protected MessageSender messageSender;
    protected MessageReceiver messageReceiver;
    protected FileSender fileSender;
    protected FileReceiver fileReceiver;
    protected Socket socket;
    protected int connectionFailuresCount;
    protected Gui gui;
    protected boolean verified;

    public FileTransferHandler(Gui gui, Map<String, Long> timeStamps, Map<String, Long> remoteTimeStamps, Set<String> newLocalFiles, Set<String> deletedLocalFiles, Set<String> newRemoteFiles, Set<String> deletedRemoteFiles) {
        this.timeStamps = timeStamps;
        this.remoteTimeStamps = remoteTimeStamps;
        this.newLocalFiles = newLocalFiles;
        this.deletedLocalFiles = deletedLocalFiles;
        this.newRemoteFiles = newRemoteFiles;
        this.deletedRemoteFiles = deletedRemoteFiles;
        this.connectionFailuresCount = 0;
        this.gui = gui;
        this.verified = true;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                uploadNewLocalFiles();
                deleteFilesOnServer();

                updateRemoteTimeStamps();

                downloadNewRemoteFiles();
                deleteFilesInLocalFolder();

            } catch (IOException ex) {
                disconnect();
            }
            try {
                setUpTrayIcon();
                Thread.sleep(2000L);
            } catch (InterruptedException ex) {
                System.out.println("inter exc");
                break;
            }
        }
    }

    private void updateRemoteTimeStamps() {
        if (connect()) {
            messageSender.sendMessage("requestingTimeStamps");
            messageReceiver.receiveTimeStamps(remoteTimeStamps, newRemoteFiles, deletedRemoteFiles);
            if (!TimeStamps.saveTimeStampsToFile(remoteTimeStamps, Configurations.getRemoteTimeStampsFileName())) {
                gui.setErrorTrayIconImage("File acces error! Check your permissions or try restarting the application.");
            }
            //Save results to files
            TimeStamps.saveNamesToFile(newRemoteFiles, Configurations.getNewRemoteFileName());
            TimeStamps.saveNamesToFile(deletedRemoteFiles, Configurations.getDeletedRemoteFileName());

            disconnect();
        }
    }

    private boolean sendFile(String fileName) throws IOException {
        File file = new File(Configurations.getWorkingDirectoryName() + fileName);
        if (!file.exists()) {
            return true;
        }
        if (file.isDirectory()) {
            messageSender.sendMessage("sendingDirectory");
            messageSender.sendMessage(fileName);
            messageSender.sendMessage(timeStamps.get(fileName));
        } else {
            messageSender.sendMessage("sendingFile");
            messageSender.sendMessage(fileName);
            messageSender.sendMessage(timeStamps.get(fileName));
            if (!fileSender.sendFile(Configurations.getWorkingDirectoryName() + fileName)) {
                return false;
            }
        }
        remoteTimeStamps.put(fileName, timeStamps.get(fileName));
        return true;

    }

    private boolean deleteFile(String fileName) throws IOException {
        messageSender.sendMessage("deletingFile");
        messageSender.sendMessage(fileName);
        String answer = messageReceiver.getMessage();
        if (answer.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean requestFile(String fileName) throws IOException {
        messageSender.sendMessage("requestingFile");
        messageSender.sendMessage(fileName);
        String answer = messageReceiver.getMessage();
        if ((answer != null) && (answer.equals("directory"))) {
            return fileReceiver.createDirectory(fileName, timeStamps);
        } else if ((answer != null) && (answer.equals("file"))) {
            return fileReceiver.getFile(Configurations.getWorkingDirectoryName(), fileName, timeStamps);
        }
        return false;

    }

    private boolean connect() {
        try {
            socket = new Socket(Configurations.getHost(), Configurations.getSocket());
            messageSender = new MessageSender(socket);
            messageReceiver = new MessageReceiver(socket);
            fileSender = new FileSender(socket);
            fileReceiver = new FileReceiver(socket);
            messageSender.sendMessage(Configurations.getPassword());
            String answer = messageReceiver.getMessage();
            if (!answer.equals("verified")) {
                this.verified = false;
                return false;
            } else {
                this.verified = true;
            }
            this.connectionFailuresCount = 0;
            return true;
        } catch (IOException ex) {
            disconnect();
            this.connectionFailuresCount++;
            return false;
        }
    }

    private void disconnect() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException exc) {
                this.connectionFailuresCount++;
            }
        }
    }

    private void uploadNewLocalFiles() throws IOException {
        Set<String> failed = new HashSet();
        if (!newLocalFiles.isEmpty()) {
            this.gui.setSyncingTrayIconImage("Uploading new files.");
        }
        while (!newLocalFiles.isEmpty()) {
            String fileName = newLocalFiles.iterator().next();
            if (connect()) {
                if (sendFile(fileName)) {
                    TimeStamps.saveNamesToFile(newLocalFiles, Configurations.getNewLocalFileName());
                } else {
                    failed.add(fileName);
                    timeStamps.remove(fileName);
                }
                newLocalFiles.remove(fileName);
                disconnect();
            } else {
                break;
            }
        }
        newLocalFiles.addAll(failed);
    }

    private void deleteFilesOnServer() throws IOException {
        Set<String> failed = new HashSet();
        if (!deletedLocalFiles.isEmpty()) {
            this.gui.setSyncingTrayIconImage("Removing files on server.");
        }
        while (!deletedLocalFiles.isEmpty()) {
            String fileName = deletedLocalFiles.iterator().next();
            if (connect()) {
                if (deleteFile(fileName)) {
                    TimeStamps.saveNamesToFile(deletedLocalFiles, Configurations.getDeletedLocalFileName());
                } else {
                    failed.add(fileName);
                }
                deletedLocalFiles.remove(fileName);
                disconnect();
            } else {
                break;
            }
        }
        deletedLocalFiles.addAll(failed);
    }

    private void downloadNewRemoteFiles() throws IOException {
        Set<String> failed = new HashSet();
        if (!newRemoteFiles.isEmpty()) {
            this.gui.setSyncingTrayIconImage("Downloading new files.");
        }
        while (!newRemoteFiles.isEmpty()) {
            String fileName = newRemoteFiles.iterator().next();
            if (connect()) {
                if (requestFile(fileName)) {
                    timeStamps.put(fileName, (new File(Configurations.getWorkingDirectoryName() + fileName)).lastModified());
                } else {
                    failed.add(fileName);
                }
                newRemoteFiles.remove(fileName);
                TimeStamps.saveNamesToFile(newRemoteFiles, Configurations.getNewRemoteFileName());
                //while downloading the file, timeStampsHandler probably found it as a new local file.
                newLocalFiles.remove(fileName);
                disconnect();
            } else {
                break;
            }
        }
        newRemoteFiles.addAll(failed);
    }

    private void deleteFilesInLocalFolder() {
        Set<String> failed = new HashSet();
        if (!deletedRemoteFiles.isEmpty()) {
            this.gui.setSyncingTrayIconImage("Deleting local files.");
        }
        while (!deletedRemoteFiles.isEmpty()) {
            String fileName = deletedRemoteFiles.iterator().next();
            if (fileReceiver.deleteFile(fileName, timeStamps)) {
                TimeStamps.saveNamesToFile(deletedRemoteFiles, Configurations.getDeletedRemoteFileName());
            } else {
                failed.add(fileName);
            }
            deletedRemoteFiles.remove(fileName);
        }
        deletedRemoteFiles.addAll(failed);
    }

    private void setUpTrayIcon() throws InterruptedException {
        if (this.connectionFailuresCount > 3) {
            this.gui.setErrorTrayIconImage("Connection error!");
            //Thread.sleep(15000L);//<If there are connection problems, wait longer while.
        } else {
            if (verified) {
                if ((!newLocalFiles.isEmpty()) || (!deletedLocalFiles.isEmpty()) || (!newRemoteFiles.isEmpty()) || (!deletedRemoteFiles.isEmpty())) {
                    this.gui.setSyncingTrayIconImage("Waiting for server.");
                } else {
                    this.gui.setUpToDateTrayIconImage();
                }
            } else {
                this.gui.setErrorTrayIconImage("Wrong password!");
            }
        }
    }
}
