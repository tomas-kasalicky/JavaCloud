/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.utils;

import configurations.Configurations;
import gui.SettingsFrame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;

/**
 *
 * @author Tomáš
 */
public class OKButton extends JButton implements MouseListener {

    SettingsFrame parent;

    public OKButton(SettingsFrame parent) {
        super("OK");
        this.parent = parent;
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        Configurations.setHost(parent.getHostname());
        Configurations.setPassword(parent.getPassword());
        parent.setVisible(false);
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }
}
