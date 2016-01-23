/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Tomáš
 */
public class MessageSender {

    protected PrintWriter printWriterToClient;

    public MessageSender(Socket socket) throws IOException {
        printWriterToClient = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendMessage(Object message) {
        printWriterToClient.println(message);
        printWriterToClient.flush();
    }

    public void sendTimeStamps(Map<String, Long> timeStamps) {
        Map<String, Long> tempTimeStamps = new HashMap<String, Long>();
        synchronized (timeStamps) {
            tempTimeStamps.putAll(timeStamps);
        }
        for (String s : tempTimeStamps.keySet()) {
            printWriterToClient.println(s);
            printWriterToClient.println(tempTimeStamps.get(s));
        }
        printWriterToClient.flush();
    }

    public void sendTimeStamp(String fileName, Map<String, Long> timeStamps) {
        Long timeStamp;
        synchronized (timeStamps) {
            timeStamp = timeStamps.get(fileName);
        }
        printWriterToClient.println(timeStamp);
        printWriterToClient.flush();
    }
}
