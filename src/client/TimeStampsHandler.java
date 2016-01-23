/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import configurations.Configurations;
import configurations.TimeStamps;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Tomáš
 */
public class TimeStampsHandler implements Runnable {

    public Map<String, Long> timeStamps;
    public Set<String> newLocalFiles, deletedLocalFiles;

    public TimeStampsHandler(Map<String, Long> timeStamps, Set<String> newLocalFiles, Set<String> deletedLocalFiles) {
        this.timeStamps = timeStamps;
        this.newLocalFiles = newLocalFiles;
        this.deletedLocalFiles = deletedLocalFiles;
    }

    @Override
    public void run() {

        while (!Thread.interrupted()) {
            updateLocalTimeStamps(timeStamps, newLocalFiles, deletedLocalFiles);

            if (!TimeStamps.saveTimeStampsToFile(timeStamps, Configurations.getTimeStampsFileName())) {
                System.out.println("File acces error! Check your permissions!");
            }
            TimeStamps.saveNamesToFile(newLocalFiles, Configurations.getNewLocalFileName());
            TimeStamps.saveNamesToFile(deletedLocalFiles, Configurations.getDeletedLocalFileName());

            try {
                Thread.sleep(1500L);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }

    private void updateLocalTimeStamps(Map<String, Long> timeStamps, Set<String> newLocalFiles, Set<String> deletedLocalFiles) {
        Set<String> deletedFiles = new HashSet<String>();
        deletedFiles.clear();
        deletedFiles.addAll(timeStamps.keySet());
        File directory = new File(Configurations.getWorkingDirectoryName());
        if(!directory.exists()){
            if(!directory.mkdirs()){
                System.out.println("failed creating");
            }
        }
        updateLocalTimeStampsRecursive(directory, timeStamps, newLocalFiles, deletedFiles);
        deletedLocalFiles.addAll(deletedFiles);
        Iterator<String> it = deletedFiles.iterator();
        while (it.hasNext()) {
            String fileName = it.next();
            timeStamps.remove(fileName);
            newLocalFiles.remove(fileName);
        }
    }

    private void updateLocalTimeStampsRecursive(File file, Map<String, Long> timeStamps, Set<String> newFiles, Set<String> deletedFiles) {
        if (!file.isDirectory()) {
            return;
        }
        for (File f : file.listFiles()) {
            if (!f.isHidden()) {
                String fileName = trimFileName(f);
                if (timeStamps.containsKey(fileName)) {
                    deletedFiles.remove(fileName);
                    if (timeStamps.get(fileName) != f.lastModified()) {
                        newFiles.add(fileName);
                        timeStamps.put(fileName, f.lastModified());
                    }
                } else {
                    newFiles.add(fileName);
                    timeStamps.put(fileName, f.lastModified());
                }
                if (f.isDirectory()) {
                    updateLocalTimeStampsRecursive(f, timeStamps, newFiles, deletedFiles);
                }
            }
        }
    }

    public static String trimFileName(File file) {
        return file.getAbsolutePath().substring(Configurations.getWorkingDirectoryName().length());
    }
}
