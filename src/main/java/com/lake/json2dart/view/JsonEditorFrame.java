package com.lake.json2dart.view;

import com.google.gson.*;
import com.lake.json2dart.config.ConfigManager;
import com.lake.json2dart.extension.Log;
import com.lake.json2dart.extension.MessageTip;
import com.lake.json2dart.generator.DartClassCodeMaker;
import com.lake.json2dart.generator.DartClassMaker;
import com.lake.json2dart.model.dart.DartClass;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class JsonEditorFrame extends JFrame {
    private JPanel contentPane;
    private RSyntaxTextArea editText;
    private JTextField textFileName;
    private JTextField textClassName;
    private JCheckBox checkBoxFinal;
    private JCheckBox checkBoxComment;
    private JCheckBox checkBoxSafe;
    private JCheckBox checkBoxGenerate;
    private JCheckBox checkBoxNullSafety;
    private JCheckBox checkBoxGeneric;
    private JButton buttonOK;
    private JButton buttonClose;
    private JButton buttonFormat;

    public JsonEditorFrame() {
        setContentPane(contentPane);
        setAlwaysOnTop(true);
        getRootPane().setDefaultButton(buttonOK);

        initUI();
    }

    private void initUI() {
        editText.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
        editText.setCodeFoldingEnabled(true);
        try {
            Theme theme = Theme.load(getClass().getResourceAsStream(
                    "/org/fife/ui/rsyntaxtextarea/themes/monokai.xml"));
            theme.apply(editText);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        buttonClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onClose();
            }
        });
        buttonFormat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onFormat();
            }
        });
    }

    private void onOK() {
        String jsonStr = editText.getText();
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonElement jsonElement = new JsonParser().parse(jsonStr);
            editText.setText(gson.toJson(jsonElement));

            if (jsonStr.isEmpty()) {
                MessageTip.INSTANCE.show("Input text should not be empty");
                return;
            }

            ConfigManager.INSTANCE.setFinalProperty(checkBoxFinal.isSelected());
            ConfigManager.INSTANCE.setCommentOff(!checkBoxComment.isSelected());
            ConfigManager.INSTANCE.setEnableSafeConvert(checkBoxSafe.isSelected());
            ConfigManager.INSTANCE.setGenerateSafeConvertFile(checkBoxGenerate.isSelected());
            ConfigManager.INSTANCE.setEnableNullSafety(checkBoxNullSafety.isSelected());
            ConfigManager.INSTANCE.setUseGeneric(checkBoxGeneric.isSelected());

            String fileName = textFileName.getText();
            String className = textClassName.getText();
            fileName = fileName.isEmpty() ? "test" : fileName;
            className = className.isEmpty() ? "Test" : className;
            DartClass dartClass = new DartClassMaker(className, jsonStr).makeDartClass();
            JsonTableFrame dialog = new JsonTableFrame.Builder()
                    .setDartClass(dartClass)
                    .setFileName(fileName)
                    .build();
            dialog.setSize(800, 600);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);

            String code = new DartClassCodeMaker(dartClass).makeDartClassCode();
            if (code.contains("class Map") || code.contains("class List")) {
                MessageTip.INSTANCE.show("Map or List should not be class name");
            }
        } catch (JsonSyntaxException e) {
            MessageTip.INSTANCE.show("Json Syntax Error");
            return;
        } catch (Exception e) {
            Log.INSTANCE.e(e);
        }
        dispose();
    }

    private void onClose() {
        dispose();
    }

    private void onFormat() {
        try {
            String jsonStr = editText.getText();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonElement jsonElement = new JsonParser().parse(jsonStr);
            editText.setText(gson.toJson(jsonElement));
        } catch (JsonSyntaxException e) {
            MessageTip.INSTANCE.show("Json Syntax Error");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                displayJFrame();
            }
        });
    }

    private static void displayJFrame() {
        JsonEditorFrame frame = new JsonEditorFrame();
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
