/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.utils;

import configurations.Configurations;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author Tomáš
 */
public class TrayIconAdder {

    private TrayIcon trayIcon;
    private PopupMenu popupMenu;

    public TrayIconAdder(TrayIcon trayIcon) {
        this.trayIcon = trayIcon;
        this.popupMenu = trayIcon.getPopupMenu();
        this.trayIcon.addMouseListener(new MouseClickWorkingDirectoryOpener());
        show();
    }

    public TrayIconAdder(Image image, PopupMenu popupMenu) {
        this.trayIcon = new TrayIcon(image.getScaledInstance(SystemTray.getSystemTray().getTrayIconSize().width, SystemTray.getSystemTray().getTrayIconSize().height, Image.SCALE_SMOOTH), configurations.Configurations.getProgramName() + "\nUp to date", popupMenu);
        this.popupMenu = popupMenu;
        trayIcon.addMouseListener(new MouseClickWorkingDirectoryOpener());
        show();
    }

    public TrayIcon getTrayIcon() {
        return trayIcon;
    }

    private boolean show() {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            try {
                tray.add(trayIcon);
                if (Configurations.isFirstStart()) {
                    trayIcon.displayMessage("Welcome", "You're just first running " + Configurations.getProgramName()
                            + ".\nThe program will run here! And if you need anything,\nclick on me by the right mouse button!", TrayIcon.MessageType.INFO);
                    
                }
                return true;
            } catch (AWTException e) {
            }
        }
        JOptionPane.showMessageDialog(null, "Tray icon is not supported! You can't run this program without tray support.\n"
                + "Try to upgrade your Java or your operating system.", "Tray icon not supported!", JOptionPane.ERROR_MESSAGE);
        return false;

    }
}
