/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import configurations.Configurations;
import configurations.TimeStamps;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;

/**
 *
 * @author Tomáš
 */
public class Server implements Runnable {
    private static final int TIMEOUT = 100;

    @Override
    public void run() {
        Map<String, Long> timeStamps = new TimeStamps<String, Long>(null,Configurations.getWorkingDirectoryName());


        ServerSocket serverSocket = null;
        Socket communicationSocket;
        try {
            serverSocket = new ServerSocket(Configurations.getSocket());
            serverSocket.setSoTimeout(TIMEOUT);

            while (!Thread.interrupted()) {
                try {
                    communicationSocket = serverSocket.accept();
                } catch (SocketTimeoutException stex) {
                    // nobody connected, go to the begining of while to check if thread 
                    // was not interrupted
                    continue;
                }

                communicationSocket.setSoTimeout(10000);
                ClientHandler clientHandler = new ClientHandler(communicationSocket, timeStamps);
                new Thread(clientHandler).start();

            }
        } catch (IOException ioex) {
            ioex.printStackTrace(System.err);
        } finally {
            // close all resources !!!           
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }



    }
}
