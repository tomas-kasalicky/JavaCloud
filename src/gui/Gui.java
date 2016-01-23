/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import gui.utils.TrayIconSyncingChanger;
import gui.utils.TrayIconAdder;
import gui.utils.TrayPopupMenu;
import configurations.Configurations;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

/**
 *
 * @author Tomáš
 */
public class Gui {
    protected TrayIcon trayIcon;
    protected Image trayIconUpToDateImage, trayIconFailureImage;
    protected List<Image> syncingTrayIconsAnimation;
    protected TrayIconSyncingChanger trayIconSyncingChanger;
    protected javax.swing.Timer syncingAnimationTimer;

    public Gui() {
        PopupMenu popupMenu = new TrayPopupMenu();

        int trayWidth = SystemTray.getSystemTray().getTrayIconSize().width;
        int trayHeight = SystemTray.getSystemTray().getTrayIconSize().height;
        this.trayIconUpToDateImage = new ImageIcon(this.getClass().getResource("/images/tray_icon_180_loader.png")).getImage().getScaledInstance(trayWidth, trayHeight, Image.SCALE_SMOOTH);
        TrayIconAdder trayIconAdder = new TrayIconAdder(this.trayIconUpToDateImage, popupMenu);
        this.trayIcon = trayIconAdder.getTrayIcon();

        //load error icon
        this.trayIconFailureImage = new ImageIcon(this.getClass().getResource("/images/tray_icon_180.png")).getImage().getScaledInstance(trayWidth, trayHeight, Image.SCALE_SMOOTH);

        createAnimation(trayWidth, trayHeight);
        //create animation mover
        this.trayIconSyncingChanger = new TrayIconSyncingChanger(this.syncingTrayIconsAnimation, this.trayIcon);
        //create animation timer
        this.syncingAnimationTimer = new javax.swing.Timer(125, this.trayIconSyncingChanger);
    }

    public void setUpToDateTrayIconImage() {
        this.trayIcon.setToolTip(Configurations.getProgramName() + "\nUp to date");
        this.syncingAnimationTimer.stop();
        this.trayIcon.setImage(trayIconUpToDateImage);
    }

    public void setErrorTrayIconImage(String status) {
        this.trayIcon.setToolTip(Configurations.getProgramName() + "\n" + status);
        this.syncingAnimationTimer.stop();
        this.trayIcon.setImage(trayIconFailureImage);
    }

    public void setSyncingTrayIconImage(String status) {
        this.trayIcon.setToolTip(Configurations.getProgramName() + "\n" + status);
        this.syncingAnimationTimer.start();
        this.trayIconSyncingChanger.restart();
    }

    private void createAnimation(int trayWidth, int trayHeight) {
        this.syncingTrayIconsAnimation = new ArrayList<Image>();
        syncingTrayIconsAnimation.add(new ImageIcon(this.getClass().getResource("/images/loader/tray_icon_loader_1.png")).getImage().getScaledInstance(trayWidth, trayHeight, Image.SCALE_SMOOTH));
        syncingTrayIconsAnimation.add(new ImageIcon(this.getClass().getResource("/images/loader/tray_icon_loader_2.png")).getImage().getScaledInstance(trayWidth, trayHeight, Image.SCALE_SMOOTH));
        syncingTrayIconsAnimation.add(new ImageIcon(this.getClass().getResource("/images/loader/tray_icon_loader_3.png")).getImage().getScaledInstance(trayWidth, trayHeight, Image.SCALE_SMOOTH));
        syncingTrayIconsAnimation.add(new ImageIcon(this.getClass().getResource("/images/loader/tray_icon_loader_4.png")).getImage().getScaledInstance(trayWidth, trayHeight, Image.SCALE_SMOOTH));
        syncingTrayIconsAnimation.add(new ImageIcon(this.getClass().getResource("/images/loader/tray_icon_loader_5.png")).getImage().getScaledInstance(trayWidth, trayHeight, Image.SCALE_SMOOTH));
        syncingTrayIconsAnimation.add(new ImageIcon(this.getClass().getResource("/images/loader/tray_icon_loader_6.png")).getImage().getScaledInstance(trayWidth, trayHeight, Image.SCALE_SMOOTH));
        syncingTrayIconsAnimation.add(new ImageIcon(this.getClass().getResource("/images/loader/tray_icon_loader_7.png")).getImage().getScaledInstance(trayWidth, trayHeight, Image.SCALE_SMOOTH));
        syncingTrayIconsAnimation.add(new ImageIcon(this.getClass().getResource("/images/loader/tray_icon_loader_8.png")).getImage().getScaledInstance(trayWidth, trayHeight, Image.SCALE_SMOOTH));
    }
}
