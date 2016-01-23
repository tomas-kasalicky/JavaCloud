/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package configurations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Tomáš
 */
public class TimeStamps<K extends Object, V extends Object> extends HashMap {

    public TimeStamps(String source) {
        super();
        loadTimeStampsFromFile(source);
    }

    public TimeStamps(String source, String workingDirectory) {
        super();
        if (source != null) {
            loadTimeStampsFromFile(source);
        }
        if (this.isEmpty()) {
            loadTimeStamps(new File(workingDirectory), workingDirectory);
        }
    }

    private void loadTimeStampsFromFile(String source) {
        BufferedReader readLine = null;
        try {
            readLine = new BufferedReader(new FileReader(source));
            String line;
            Long timeStamp;
            while ((line = readLine.readLine()) != null) {
                timeStamp = Long.parseLong(readLine.readLine());
                put(line, timeStamp);
            }
        } catch (FileNotFoundException ex) {
            //That means that it is probably the first start -> so it doesn't mean a trouble.
        } catch (IOException ex) {
            //But this is trouble.
            System.out.println("Data loading error.");
        } finally {
            try {
                if (readLine != null) {
                    readLine.close();
                }
            } catch (IOException ex) {
                System.out.println("File acces error.");
            }
        }
    }

    private void loadTimeStamps(File file, String workingDirectory) {
        for (File f : file.listFiles()) {
            if (!f.isHidden()) {
                put(f.getAbsolutePath().substring(workingDirectory.length()), f.lastModified());
                if (f.isDirectory()) {
                    loadTimeStamps(f, workingDirectory);
                }
            }
        }
    }

    public static boolean saveTimeStampsToFile(Map<String, Long> timeStamps, String fileName) {
        PrintWriter printWriter = null;
        File file = new File(fileName);
        try {
            file.getParentFile().mkdirs();
            printWriter = new PrintWriter(file);
            for (String s : timeStamps.keySet()) {
                printWriter.println(s);
                printWriter.println(timeStamps.get(s));
            }
            return true;
        } catch (FileNotFoundException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }

    public static boolean saveNamesToFile(Set<String> names, String fileName) {
        PrintWriter printWriter = null;
        File file = new File(fileName);
        try {
            file.getParentFile().mkdirs();
            printWriter = new PrintWriter(file);
            for (String s : names) {
                printWriter.println(s);
            }
            return true;
        } catch (FileNotFoundException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }

    public static boolean getNamesFromFile(Set<String> names, String fileName) {
        BufferedReader readLine = null;
        try {
            readLine = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = readLine.readLine()) != null) {
                names.add(line);
            }
            return true;
        } catch (FileNotFoundException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if (readLine != null) {
                try {
                    readLine.close();
                } catch (IOException ex) {
                    System.out.println("File acces failure.");
                }
            }
        }
    }
}
