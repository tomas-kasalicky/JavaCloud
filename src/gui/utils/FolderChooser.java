/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.utils;

import configurations.Configurations;
import gui.FolderChooserFrame;
import javax.swing.JFileChooser;

/**
 *
 * @author Tomáš
 */
public class FolderChooser extends JFileChooser {
    private FolderChooserFrame parent;
    public FolderChooser(FolderChooserFrame parent) {
        super(Configurations.getWorkingDirectoryName());
        this.parent = parent;
        setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }

    @Override
    public void approveSelection() {
        super.approveSelection();
        parent.fileChooseConfirmed();
    }

    @Override
    public void cancelSelection() {
        super.cancelSelection();
        parent.fileChooseCanceled();
    }
}
