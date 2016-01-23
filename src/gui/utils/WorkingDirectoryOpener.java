/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.utils;

import configurations.Configurations;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author Tomáš
 */
    public class WorkingDirectoryOpener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            try {
                Desktop.getDesktop().open(new File(Configurations.getWorkingDirectoryName()));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "File system error! The requested folder acces denied:\n" + ex, "IOException", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
