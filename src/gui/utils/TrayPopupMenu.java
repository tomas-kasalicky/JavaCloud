/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.utils;

import gui.SettingsFrame;
import java.awt.HeadlessException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Tomáš
 */
public class TrayPopupMenu extends PopupMenu {

    public TrayPopupMenu() throws HeadlessException {

        final MenuItem openFolder = new MenuItem("Open folder");
        openFolder.addActionListener(new WorkingDirectoryOpener());
        final MenuItem settings = new MenuItem("Settings");
        settings.addActionListener(new SettingsFrame());
        final MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    System.exit(0);
                }
            });

        add(openFolder);
        add(settings);
        add(exit);
    }
    
}
