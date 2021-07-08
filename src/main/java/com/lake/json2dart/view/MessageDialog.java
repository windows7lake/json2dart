package com.lake.json2dart.view;

import javax.swing.*;
import java.awt.event.*;

public class MessageDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel label;

    public MessageDialog(String error) {
        setContentPane(contentPane);
        setModal(true);
        setAlwaysOnTop(true);
        getRootPane().setDefaultButton(buttonOK);

        label.setText(error);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onOK();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        dispose();
    }

    public static void main(String[] args) {
        MessageDialog dialog = new MessageDialog("Error");
        dialog.pack();
        dialog.setSize(200, 200);
        dialog.setVisible(true);
        System.exit(0);
    }
}
