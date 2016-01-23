/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import gui.utils.OKButton;
import gui.utils.CancelButton;
import configurations.Configurations;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author Tomáš
 */
public class SettingsFrame extends JFrame implements ActionListener {

    private JTextField folderTextField;
    private FolderChooserFrame folderChooser;
    private JTextField hostnameField;
    private JPasswordField passwordField;
    private static final int defaultWidth = 500;
    private static final int defaultHeight = 300;

    private void addIconImages() {
        List<Image> icons = new ArrayList<Image>();
        icons.add(new ImageIcon(this.getClass().getResource("/images/tray_icon_180_loader.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        icons.add(new ImageIcon(this.getClass().getResource("/images/tray_icon_180_loader.png")).getImage());
        icons.add(new ImageIcon(this.getClass().getResource("/images/tray_icon_180_loader.png")).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        icons.add(new ImageIcon(this.getClass().getResource("/images/tray_icon_180_loader.png")).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
        setIconImages(icons);
    }

    public String getPassword() {
        return new String(this.passwordField.getPassword());
    }

    public String getHostname() {
        return this.hostnameField.getText();
    }

    public SettingsFrame() throws HeadlessException {
        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenDimension.width / 2 - defaultWidth / 2, screenDimension.height / 2 - defaultHeight / 2);

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        setName("Settings");
        setTitle("Settings");

        setResizable(false);
        setSize(defaultWidth, defaultHeight);
        setLayout(null);

        addIconImages();

        this.folderChooser = new FolderChooserFrame(this);
        this.folderChooser.setIconImages(getIconImages());

        JLabel folderLabel = new JLabel("Synchronized folder:");

        JButton folderButton = new JButton("Change");
        folderButton.addMouseListener(this.folderChooser);

        this.folderTextField = new JTextField(Configurations.getWorkingDirectoryName());
        this.folderTextField.setEditable(false);


        JLabel connectionLabel = new JLabel("Connection preferences:");

        JLabel hostnameLabel = new JLabel("Hostname:");
        this.hostnameField = new JTextField(Configurations.getHost());

        JLabel passwordLabel = new JLabel("Your password:");
        this.passwordField = new JPasswordField(Configurations.getPassword());

        OKButton okButton = new OKButton(this);
        CancelButton cancelButton = new CancelButton(this);

        folderLabel.setBounds(40, 25, 400, 20);
        this.folderTextField.setBounds(40, 45, 300, 20);
        folderButton.setBounds(340, 45, 100, 20);

        connectionLabel.setBounds(40, 85, 400, 20);
        hostnameLabel.setBounds(100, 105, 280, 20);
        hostnameField.setBounds(100, 125, 280, 20);
        passwordLabel.setBounds(100, 145, 280, 20);
        passwordField.setBounds(100, 165, 280, 20);

        okButton.setBounds(180, 205, 100, 20);
        cancelButton.setBounds(280, 205, 100, 20);

        add(folderLabel);
        add(this.folderTextField);
        add(folderButton);

        add(connectionLabel);
        add(hostnameLabel);
        add(hostnameField);
        add(passwordLabel);
        add(passwordField);

        add(okButton);
        add(cancelButton);

        setVisible(Configurations.isFirstStart());
        if (Configurations.isFirstStart()) {
            JOptionPane.showMessageDialog(this, "This is your first run of this application,\n"
                    + "please fill in your data.", "First run", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void setVisible(boolean state) {
        super.setVisible(state);
        //When closing, we can be sure, that the user already seen the settings, so we can allow folder synchronizing
        if ((!state) && (Configurations.isFirstStart())) {
            Configurations.setFirstStart(false);//while is the firstStart variable set on true, the folder syncing is paused.
        }
    }

    public void changeFolderName(String name) {
        //If the folder was really changed:
        if (!name.equals(Configurations.getWorkingDirectoryName())) {
            this.folderTextField.setText(name);
            Configurations.clearTimeStamps();
            Configurations.setWorkingDirectory(name);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        this.hostnameField.setText(Configurations.getHost());
        this.passwordField.setText(Configurations.getPassword());
        setVisible(true);
    }
}
