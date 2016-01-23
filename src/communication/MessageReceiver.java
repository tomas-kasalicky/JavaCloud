/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Tomáš
 */
public class MessageReceiver {

    protected BufferedReader lineInputFromClient;

    public MessageReceiver(Socket socket) throws IOException {
        lineInputFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public String getMessage() throws IOException {
        return lineInputFromClient.readLine();
    }

    public boolean receiveTimeStamps(Map<String, Long> remoteTimeStamps, Set<String> newRemoteFiles, Set<String> deletedRemoteFiles) {
        try {
            processIncomingTimeStamps(remoteTimeStamps, newRemoteFiles, deletedRemoteFiles);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        }
    }

    private void processIncomingTimeStamps(Map<String, Long> remoteTimeStamps, Set<String> newRemoteFiles, Set<String> deletedRemoteFiles) throws IOException {
        String fileName;
        long timeStamp;
        deletedRemoteFiles.addAll(remoteTimeStamps.keySet());
        while ((fileName = lineInputFromClient.readLine()) != null) {
            timeStamp = Long.parseLong(lineInputFromClient.readLine());
            if (remoteTimeStamps.containsKey(fileName)) {
                deletedRemoteFiles.remove(fileName);
                if ((remoteTimeStamps.get(fileName) != null) && (remoteTimeStamps.get(fileName) < timeStamp)) {
                    newRemoteFiles.add(fileName);
                }
            } else {
                newRemoteFiles.add(fileName);
            }
            remoteTimeStamps.put(fileName, timeStamp);
        }
        Iterator<String> it = deletedRemoteFiles.iterator();
        while (it.hasNext()) {
            fileName = it.next();
            remoteTimeStamps.remove(fileName);
            newRemoteFiles.remove(fileName);
        }        
    }
}
