/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.utils;

import configurations.Configurations;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author Tomáš
 */
public class MouseClickWorkingDirectoryOpener implements MouseListener {

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (me.getButton() == MouseEvent.BUTTON1) {
            try {
                Desktop.getDesktop().open(new File(Configurations.getWorkingDirectoryName()));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "File system error! The requested folder acces denied:\n" + ex, "IOException", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }
}
