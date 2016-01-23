/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import communication.FileReceiver;
import communication.FileSender;
import communication.MessageReceiver;
import communication.MessageSender;
import configurations.Configurations;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

/**
 *
 * @author Tomáš
 */
public class ClientHandler implements Runnable {

    protected Socket socket;
    protected MessageSender messageSender;
    protected MessageReceiver messageReceiver;
    protected FileSender fileSender;
    protected FileReceiver fileReceiver;
    protected Map<String, Long> timeStamps;
    protected boolean established;

    public ClientHandler(Socket socket, Map<String, Long> timeStamps) {
        try {
            this.socket = socket;
            this.timeStamps = timeStamps;
            messageSender = new MessageSender(socket);
            messageReceiver = new MessageReceiver(socket);
            fileSender = new FileSender(socket);
            fileReceiver = new FileReceiver(socket);
            established = true;
        } catch (IOException ex) {
            established = false;
            if (this.socket != null) {
                try {
                    socket.close();
                } catch (IOException exc) {
                    System.err.println("connection failure: " + exc);
                }
            }
        }
    }

    @Override
    public void run() {

        try {
            if (established) {
                if (!verify()) {
                    return;
                }
                String request = messageReceiver.getMessage();
                String fileName;
                long timeStamp;
                if (request != null) {
                    if (request.equals("sendingFile")) {
                        fileName = messageReceiver.getMessage();
                        timeStamp = Long.parseLong(messageReceiver.getMessage());
                        if (fileReceiver.getFile(Configurations.getWorkingDirectoryName(), fileName, timeStamps)) {
                            (new File(Configurations.getWorkingDirectoryName() + fileName)).setLastModified(timeStamp);
                            timeStamps.put(fileName, timeStamp);
                        }
                    } else if (request.equals("sendingDirectory")) {
                        fileName = messageReceiver.getMessage();
                        timeStamp = Long.parseLong(messageReceiver.getMessage());
                        if (fileReceiver.createDirectory(fileName, timeStamps)) {
                            (new File(Configurations.getWorkingDirectoryName() + fileName)).setLastModified(timeStamp);
                            timeStamps.put(fileName, timeStamp);
                        }
                    } else if (request.equals("deletingFile")) {
                        messageSender.sendMessage(fileReceiver.deleteFile(messageReceiver.getMessage(), timeStamps));
                    } else if (request.equals("requestingTimeStamps")) {
                        messageSender.sendTimeStamps(timeStamps);
                    } else if (request.equals("requestingFile")) {
                        fileName = messageReceiver.getMessage();
                        File file = new File(Configurations.getWorkingDirectoryName() + fileName);
                        if (file.isDirectory()) {
                            messageSender.sendMessage("directory");
                        } else {
                            messageSender.sendMessage("file");
                            fileSender.sendFile(Configurations.getWorkingDirectoryName() + fileName);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println("Connection failure: " + ex);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    System.err.println("Connection closing failure:" + ex);
                }
            }
        }
    }

    private boolean verify() {
        try {
            String password = messageReceiver.getMessage();
            if (password.equals(Configurations.getPassword())) {
                messageSender.sendMessage("verified");
                return true;
            } else {
                messageSender.sendMessage("Wrong password!");
                return false;
            }
        } catch (IOException ex) {
            return false;
        }

    }
}
