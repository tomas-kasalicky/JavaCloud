/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.utils;

import java.awt.Image;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 *
 * @author Tomáš
 */
public class TrayIconSyncingChanger implements ActionListener {

    private int position;
    private List<Image> animation;
    private TrayIcon trayIcon;

    public TrayIconSyncingChanger(List<Image> animation, TrayIcon trayIcon) {
        super();
        position = 0;
        this.animation = animation;
        this.trayIcon = trayIcon;
    }

    public List<Image> getAnimation() {
        return animation;
    }

    public void setAnimation(List<Image> animation) {
        this.animation = animation;
    }

    public TrayIcon getTrayIcon() {
        return trayIcon;
    }

    public void setTrayIcon(TrayIcon trayIcon) {
        this.trayIcon = trayIcon;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (position < animation.size() - 1) {
            position++;
        } else {
            position = 0;
        }
        trayIcon.setImage(animation.get(position));
    }

    public void restart() {
        position = 0;
        trayIcon.setImage(animation.get(position));
    }
}
