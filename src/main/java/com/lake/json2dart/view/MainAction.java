package com.lake.json2dart.view;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class MainAction extends AnAction {

    public static AnActionEvent event;

    @Override
    public void actionPerformed(AnActionEvent e) {
        event = e;

        JsonEditorFrame frame = new JsonEditorFrame();
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
