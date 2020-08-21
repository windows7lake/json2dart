package com.lake.json2dart.view;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;

import javax.swing.*;
import java.io.IOException;

public class Json2DartForm {
    public JPanel rootView;
    private RSyntaxTextArea editor;
    private JCheckBox finalField;
    private JCheckBox justOneFile;
    private JCheckBox createConstructor;
    private JCheckBox defaultValue;
    private JCheckBox safeConvert;
    private JCheckBox safeClass;
    private JLabel fileNameLabel;
    private JTextField fileName;
    private JButton generateBtn;

    private void createUIComponents() {
        editor = new RSyntaxTextArea();
        editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
        editor.setCodeFoldingEnabled(true);
        try {
            Theme theme = Theme.load(getClass().getResourceAsStream(
                    "/org/fife/ui/rsyntaxtextarea/themes/monokai.xml"));
            theme.apply(editor);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private OnGeneratorListener listener;

    public void setOnGeneratorListener(OnGeneratorListener listener) {
        this.listener = listener;
        generateBtn.addActionListener(action -> {
            if (this.listener == null) return;
            this.listener.onClick(
                    editor != null ? editor.getText() : "",
                    fileName != null ? fileName.getText() : "bean",
                    finalField != null && finalField.isSelected(),
                    justOneFile != null && justOneFile.isSelected(),
                    createConstructor != null && createConstructor.isSelected(),
                    defaultValue != null && defaultValue.isSelected(),
                    safeConvert != null && safeConvert.isSelected(),
                    safeClass != null && safeClass.isSelected()
            );
        });
    }

    public interface OnGeneratorListener {
        void onClick(String json, String fileName, Boolean finalField, Boolean justOneFile,
                     Boolean createConstructor, Boolean defaultValue, Boolean safeConvert, Boolean safeClass);
    }
}
