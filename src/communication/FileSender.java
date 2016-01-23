/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 *
 * @author Tomáš
 */
public class FileSender {

    BufferedOutputStream bufferedOutputStream;

    public FileSender(Socket socket) throws IOException {
        bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
    }

    public boolean sendFile(String fileName) {
        BufferedInputStream fileInput = null;
        try {
            sendBytes(fileName, fileInput);
        } catch (FileNotFoundException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            try {
                if (fileInput != null) {
                    fileInput.close();
                }
            } catch (IOException ex) {
                return false;
            }
        }
        return true;
    }

    private void sendBytes(String fileName, BufferedInputStream fileInput) throws FileNotFoundException, IOException {
        File file = new File(fileName);
        fileInput = new BufferedInputStream(new FileInputStream(file));
        int bytesCount;
        byte[] bytesToSend = new byte[16384];
        bufferedOutputStream.write(ByteBuffer.allocate(8).putLong(file.length()).array(),0,8);
        while ((bytesCount = fileInput.read(bytesToSend)) != -1) {
            bufferedOutputStream.write(bytesToSend, 0, bytesCount);
        }
        fileInput.close();
        bufferedOutputStream.close();
    }
}
