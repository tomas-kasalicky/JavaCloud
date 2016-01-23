/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import gui.utils.FolderChooser;
import java.awt.HeadlessException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Tomáš
 */
public class FolderChooserFrame extends JDialog implements MouseListener {

    private SettingsFrame parent;
    private JFileChooser fileChooser;

    public FolderChooserFrame(SettingsFrame parent) throws HeadlessException {
        this.parent = parent;
        this.fileChooser = new FolderChooser(this);
        add(this.fileChooser);
        setModal(true);
        setResizable(false);
        
        this.pack();
        setVisible(false);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(this.parent);
        setName("Choose the new folder");
        setTitle("Choose the new folder");
        //setAlwaysOnTop(true);
    }

    public void fileChooseConfirmed(){
        this.setVisible(false);
        this.parent.changeFolderName(this.fileChooser.getSelectedFile().getAbsolutePath());
    }
    
    public void fileChooseCanceled(){
        this.setVisible(false);
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (me.getButton() == MouseEvent.BUTTON1) {
            int confirmation = JOptionPane.showConfirmDialog(this.parent, "Please notice: \n"
                    + "If you're gonna change the synchronized directory, \n"
                    + "the content of the new directory will be merged. \n"
                    + "Some conflicted files might be accidently deleted, \n"
                    + "so it's recommended the new directory to be empty.", "Changing directory", JOptionPane.OK_CANCEL_OPTION);
            if (confirmation == JOptionPane.OK_OPTION) {
                setVisible(true);
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
