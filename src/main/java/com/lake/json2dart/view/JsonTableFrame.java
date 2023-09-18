package com.lake.json2dart.view;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.lake.json2dart.config.ConfigManager;
import com.lake.json2dart.extension.MessageTip;
import com.lake.json2dart.generator.ClassFileGenerator;
import com.lake.json2dart.generator.DartClassCodeMaker;
import com.lake.json2dart.generator.DartClassMaker;
import com.lake.json2dart.model.clazz.DataClass;
import com.lake.json2dart.model.clazz.ListClass;
import com.lake.json2dart.model.clazz.Property;
import com.lake.json2dart.model.dart.DartClass;
import com.lake.json2dart.model.data.TestDataConstant;
import com.lake.json2dart.treetable.CheckBoxTreeTableAdapter;
import com.lake.json2dart.treetable.FieldTreeTableModel;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.lake.json2dart.model.data.ConvertDataKt.SafeCode;

public class JsonTableFrame extends JFrame {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JScrollPane scrollPane;

    private final DartClass dartClass;
    private final String fileName;
    private ArrayList<DefaultMutableTreeTableNode> defaultMutableTreeTableNodeList;

    public JsonTableFrame(Builder builder) {
        this.dartClass = builder.dartClass;
        this.fileName = builder.fileName;

        setTitle("Json Table");
        setContentPane(contentPane);
        setAlwaysOnTop(true);
        getRootPane().setDefaultButton(buttonOK);

        initTreeTable();
        initOperationBtn();
    }

    private void initTreeTable() {
        defaultMutableTreeTableNodeList = new ArrayList<>();

        DefaultMutableTreeTableNode defaultMutableTreeTableNode = createTreeTableNodeData();
        FieldTreeTableModel fieldTreeTableModel = new FieldTreeTableModel(defaultMutableTreeTableNode);

        DefaultListSelectionModel defaultListSelectionModel = new DefaultListSelectionModel();
        defaultListSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        defaultListSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                defaultListSelectionModel.clearSelection();
            }
        });

        JXTreeTable treeTable = new JXTreeTable(fieldTreeTableModel);
        treeTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        treeTable.setCellSelectionEnabled(true);
        treeTable.setSelectionModel(defaultListSelectionModel);
        treeTable.expandAll();
        treeTable.setRowHeight(30);

        CheckBoxTreeTableAdapter checkBoxTreeTableAdapter = new CheckBoxTreeTableAdapter(treeTable);
        checkBoxTreeTableAdapter.getSelectionModel().addPathsByNodes(defaultMutableTreeTableNodeList);

        scrollPane.setViewportView(treeTable);
    }

    private void initOperationBtn() {
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
    }

    private void onOK() {
        String code = new DartClassCodeMaker(dartClass).makeDartClassCode();
        VirtualFile file = MainAction.event.getData(CommonDataKeys.VIRTUAL_FILE);
        if (file == null) {
            MessageTip.INSTANCE.show("can not get directory path!");
            return;
        }

        try {
            File destinyFile = new File(file.getPath());
            if (destinyFile.isFile()) {
                File parentFile = new File(destinyFile.getParent());
                if (parentFile.isDirectory()) {
                    destinyFile = parentFile;
                }
            }
            if (!destinyFile.isDirectory()) {
                MessageTip.INSTANCE.show("Please choose a directory to generate dart file.");
            }
            new ClassFileGenerator(destinyFile, fileName + ".dart", code).generate();
            if (ConfigManager.INSTANCE.getGenerateSafeConvertFile()) {
                new ClassFileGenerator(destinyFile,  "safe_convert.dart", SafeCode).generate();
            }
        } catch (IOException e) {
            MessageTip.INSTANCE.show("Generate dart file failed! \n stackTrack" + e);
        } finally {
            file.refresh(false, true);
        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    private DefaultMutableTreeTableNode createTreeTableNodeData() {
        DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode(dartClass);
        createTreeTableNode(root, dartClass);
        return root;
    }

    private void createTreeTableNode(DefaultMutableTreeTableNode root, DartClass innerClass) {
        if (innerClass instanceof DataClass) {
            createTreeTableNode(root, (DataClass) innerClass);
        }
        if (innerClass instanceof ListClass) {
            createTreeTableNode(root, (DataClass) ((ListClass) innerClass).getGeneric());
        }
    }

    private void createTreeTableNode(DefaultMutableTreeTableNode root, DataClass dataClass) {
        for (Property property : dataClass.getProperties()) {
            DefaultMutableTreeTableNode node = new DefaultMutableTreeTableNode(property);
            root.add(node);
            if (property.getTypeObject() instanceof DataClass) {
                createTreeTableNode(node, property.getTypeObject());
            } else if (property.getTypeObject() instanceof ListClass) {
                DartClass dartClass = ((ListClass) property.getTypeObject()).getGeneric();
                if (dartClass instanceof DataClass) {
                    createTreeTableNode(node, dartClass);
                } else {
                    // for example: List<String>/List<int> and so on.
                    defaultMutableTreeTableNodeList.add(node);
                }
            } else {
                defaultMutableTreeTableNodeList.add(node);
            }
        }
    }

    @Override
    public void dispose() {
        defaultMutableTreeTableNodeList = null;
        super.dispose();
    }

    public static class Builder {
        private DartClass dartClass;
        private String fileName;

        public Builder() {
        }

        public Builder setDartClass(DartClass dartClass) {
            this.dartClass = dartClass;
            return this;
        }

        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public JsonTableFrame build() {
            return new JsonTableFrame(this);
        }
    }

    public static void main(String[] args) {
        String rawJson = TestDataConstant.rawJson;
        DartClass dartClass = new DartClassMaker("Test", rawJson).makeDartClass();
        JsonTableFrame dialog = new Builder()
                .setDartClass(dartClass)
                .setFileName("Test")
                .build();
        dialog.pack();
        dialog.setSize(800, 600);
        dialog.setVisible(true);
        System.exit(0);
    }
}
