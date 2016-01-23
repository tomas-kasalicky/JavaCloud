package server;

import configurations.Configurations;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tomas Kasalicky
 */
public class Main {

    public static void main(String[] args) {
        Configurations.init("server");
        Thread server = new Thread(new Server());
        if (Configurations.isFirstStart()) {
            if (!getInitialSettings()) {
                System.err.println("IO exception. The Server will be ended.");
                return;
            }
        }
        server.start();
        waitForCommands(server);
        server.interrupt();

        // then stop the server
    }

    private static void getWorkingDirectoryName() throws IOException {
        System.out.println("Please enter absolute path of the storage directory (it should be empty).");
        System.out.println("If you leave the line empty, default directory wil be used: \n" + Configurations.getDefaultWorkingDirectoryName());
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String fileName = reader.readLine();
        if (fileName.equals("")) {
            fileName = Configurations.getDefaultWorkingDirectoryName();
        }
        File file = new File(fileName);
        file.mkdirs();
        while (!file.exists()) {
            System.out.println("Unsuitable directory, please check your file acces permissions or enter a different directory: ");
            fileName = reader.readLine();
            if (fileName.equals("")) {
                fileName = Configurations.getDefaultWorkingDirectoryName();
            }
            file = new File(fileName);
            file.mkdirs();
        }
        Configurations.setWorkingDirectory(fileName);
        System.out.println("Working directory is set to: \n" + Configurations.getWorkingDirectoryName());
    }

    private static void getPassword() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter your password for this directory:");
        String password = reader.readLine();
        Configurations.setPassword(password);
        System.out.println("Your password is set.");
    }

    private static boolean getInitialSettings() {
        System.out.println("This is the first run, please enter initial settings:");
        try {
            getWorkingDirectoryName();
            getPassword();
        } catch (IOException ex) {
            System.err.println("IO Exception: " + ex);
            return false;
        }
        Configurations.setFirstStart(false);
        return true;
    }

    private static void waitForCommands(Thread server) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("SERVER | " + Configurations.getProgramName());
            System.out.println("Please do no change the content of the working directory, if you do,\n"
                    + "type 'f' to force manual reset of the synchronization thread.\n");
            System.out.println("Server started, to quit type 'q'. For changing password type 'p', for changing directory type 'd'");
            String command = reader.readLine();
            while (!command.equals("q")) {
                if (command.equals("p")) {
                    getPassword();
                } else if (command.equals("d")) {
                    getWorkingDirectoryName();
                } else if (command.equals("f")) {
                    resetThread(server);
                }
                System.out.println("SERVER | " + Configurations.getProgramName());
                System.out.println("Please do no change the content of the working directory, if you do,\n"
                        + "type 'f' to force manual reset of the synchronization thread.\n");
                System.out.println("Server is running, to quit type 'q'. For changing password type 'p', for changing directory type 'd'");
                command = reader.readLine();
            }
        } catch (IOException ex) {
            System.err.println("IO Exception" + ex);
        }
    }

    private static void resetThread(Thread server) {
        server.interrupt();
        try {
            while (server.isAlive()) {
                Thread.sleep(500);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        server = new Thread(new Server());
        server.start();
    }
}
