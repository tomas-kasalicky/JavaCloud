/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;

import configurations.Configurations;
import configurations.OSDetector;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 *
 * @author Tomáš
 */
public class FileReceiver {

    protected BufferedInputStream inputFromClient;

    public FileReceiver(Socket socket) throws IOException {
        inputFromClient = new BufferedInputStream(socket.getInputStream());
    }

    public boolean getFile(String workingDirectory, String fileName, Map<String, Long> timeStamps) {
        boolean success = true;
        BufferedOutputStream outputToFile = null;
        File file = new File(workingDirectory + fileName);
        try {
            //file = getHiddenFile(file);
            if (!getBytes(outputToFile, file)) {
                success = false;
            }
        } catch (NumberFormatException ex) {
            success = false;
        } catch (FileNotFoundException ex) {
            success = false;
        } catch (IOException ex) {
            success = false;
        } finally {
            try {
                if (outputToFile != null) {
                    outputToFile.close();
                }
            } catch (IOException ex) {
                success = false;
            } finally {
                if ((success) /*&& (unhideFile(file))*/) {
                    return true;
                } else {
                    file.delete();
                    return false;
                }
            }
        }
    }

    private boolean getBytes(BufferedOutputStream outputToFile, File file) throws IOException {
        int bytesCount;
        byte[] data = new byte[16384];
        //open the received file
        outputToFile = new BufferedOutputStream(new FileOutputStream(file));
        file.getParentFile().mkdirs();
        byte[] lengthBytes = new byte[8];
        inputFromClient.read(lengthBytes, 0, 8);
        Long length = ByteBuffer.wrap(lengthBytes).getLong();
        //receiving data
        while ((bytesCount = inputFromClient.read(data)) != -1) {
            outputToFile.write(data, 0, bytesCount);
            length -= bytesCount;
        }
        outputToFile.close();
        inputFromClient.close();
        if (length != 0) {
            System.out.println("spatny bajty");
            return false;
        }
        return true;
    }

    public boolean createDirectory(String directoryName, Map<String, Long> timeStamps) {
        File directory = new File(Configurations.getWorkingDirectoryName() + directoryName);
        if (directory.exists() || directory.mkdirs()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteFile(String fileName, Map<String, Long> timeStamps) {
        File file = new File(Configurations.getWorkingDirectoryName() + fileName);
        if (!file.exists()) {
            removeTimeStamp(fileName, timeStamps);
            return true;
        }
        if (file.isDirectory()) {
            if (removeFileRecursively(file, timeStamps)) {
                removeTimeStamp(fileName, timeStamps);
                return true;
            } else {
                return false;
            }
        }
        if (file.delete()) {
            removeTimeStamp(fileName, timeStamps);
            return true;
        }
        return false;
    }

    private boolean removeFileRecursively(File file, Map<String, Long> timeStamps) {
        int trim = Configurations.getWorkingDirectoryName().length();
        if (file == null) {
            return true;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            boolean succes = true;
            for (File f : files) {
                if (!removeFileRecursively(f, timeStamps)) {
                    succes = false;
                }
            }
            if (succes) {
                if (file.delete()) {
                    removeTimeStamp(file.getAbsolutePath().substring(trim), timeStamps);
                    return true;
                }
            } else {
                return false;
            }
        }
        if (file.delete()) {
            removeTimeStamp(file.getAbsolutePath().substring(trim), timeStamps);
            return true;
        }
        return false;
    }

    private void putTimeStamp(File file, String fileName, Map<String, Long> timeStamps) {
        if (file.lastModified() != 0) {
            synchronized (timeStamps) {
                timeStamps.put(fileName, file.lastModified());
            }
        }
    }

    private void removeTimeStamp(String fileName, Map<String, Long> timeStamps) {
        synchronized (timeStamps) {
            timeStamps.remove(fileName);
        }
    }

    private File getHiddenFile(File file) throws IOException {
        int i = 0;
        File newNamedFile = new File(file.getParent() + System.getProperty("file.separator") + "." + i + file.getName());
        while (newNamedFile.exists()) {
            i++;
            newNamedFile = new File(file.getParent() + System.getProperty("file.separator") + "." + i + file.getName());
        }
        file = newNamedFile;
        file.createNewFile();
        file.deleteOnExit();
        if (OSDetector.isWindows()) {
            Runtime.getRuntime().exec("attrib +H " + file.getAbsolutePath());
        }
        return file;
    }

    private boolean unhideFile(File file) {
        if (OSDetector.isWindows()) {
            try {
                Runtime.getRuntime().exec("attrib -H " + file.getAbsolutePath());
            } catch (IOException ex) {
                return false;
            }
        }
        File newNamedFile = new File(file.getParent() + System.getProperty("file.separator") + file.getName().substring(2));
        newNamedFile.delete();
        if ((!newNamedFile.exists()) && file.renameTo(newNamedFile)) {
            return true;
        } else {
            return makeConflictedFile(file);
        }

    }

    private boolean makeConflictedFile(File file) {
        File newNamedFile = new File(file.getParent() + System.getProperty("file.separator") + "(Conflicted file)" + file.getName().substring(2));
        int i = 0;
        while (newNamedFile.exists()) {
            i++;
            newNamedFile = new File(file.getParent() + System.getProperty("file.separator") + "(Conflicted file " + i + ")" + file.getName().substring(2));
        }
        return file.renameTo(newNamedFile);
    }
}
