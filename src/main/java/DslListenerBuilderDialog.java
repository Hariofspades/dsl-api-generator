import extensions.UIExtensionsKt;
import model.JModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

public class DslListenerBuilderDialog extends JDialog {
    private static final int MAX_ITEM_COUNT = 30;
    private JButton mBtnOK;
    private JButton mBtnCancel;
    private JButton mBtnAdd;
    private JButton mBtnDelete;
    private JTextField mTFieldActionName;
    private JTextField mClassName;
    private JPanel contentPane;
    private JList mActionListView;
    private JComboBox<String> mCBoxValueType;
    private JCheckBox pascalCase;
    private List<JModel> mModelListenerList = new ArrayList<>();
    private int[] mSelectedIndexArray;
    private DialogListener mDialogListener;

    public DslListenerBuilderDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(mBtnOK);
        initView();
        initEvent();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    }

    private void initView() {
        mActionListView.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        mCBoxValueType.setEditable(true);
        mBtnAdd.setEnabled(true);
        mBtnDelete.setEnabled(false);
        mModelListenerList.clear();
    }

    private void initEvent() {
        mBtnOK.addActionListener(e -> onGenerate());
        mBtnCancel.addActionListener(e -> onCancel());
        mBtnAdd.addActionListener(e -> addProperty());
        mBtnDelete.addActionListener(e -> {
            if (mSelectedIndexArray.length > 0 && mSelectedIndexArray.length <= mModelListenerList.size()) {
                for (int selectedIndex : mSelectedIndexArray) {
                    if (selectedIndex >= 0 && selectedIndex <= mModelListenerList.size() - 1 && mModelListenerList.get(selectedIndex) != null) {
                        mModelListenerList.remove(selectedIndex);
                    }
                }
                refreshActionListView();
            }
        });
        mActionListView.addListSelectionListener(e -> {
            mBtnDelete.setEnabled(true);
            mSelectedIndexArray = mActionListView.getSelectedIndices();
        });

    }

    private void addProperty() {
        String actionName = mTFieldActionName.getText();
        Object selectedItem = mCBoxValueType.getSelectedItem();
        String returnValueType = (selectedItem != null) ? selectedItem.toString() : "Unit";
        if (mModelListenerList.size() < MAX_ITEM_COUNT) {
            if (!actionName.equals("")) {
                mModelListenerList.add(mModelListenerList.size(), new JModel(actionName, returnValueType));
                refreshActionListView();
            } else {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "property cannot be empty", "DSL API Generator", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "cannot add more than 30 items", "DSL API Generator", JOptionPane.ERROR_MESSAGE);
        }
        resetInput();
    }

    private void resetInput() {
        mTFieldActionName.setText("");
    }

    private void refreshActionListView() {
        mActionListView.setModel(new DefaultComboBoxModel(mModelListenerList.stream().map(it ->
                String.format(Locale.US, "%s: %s", it.getPropertyName(), it.getReturnValueType())
        ).toArray()));
        mActionListView.setSelectedIndex(0);
    }


    private void onGenerate() {
        if (mDialogListener != null) {
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            for (int index = 0; index < mModelListenerList.size(); index++) {
                map.put(mModelListenerList.get(index).getPropertyName(),
                        String.format(Locale.US, "%s", mModelListenerList.get(index).getReturnValueType()));
            }
            if (!mClassName.getText().equals("")) {
                mDialogListener.onGenerateClicked(map, mClassName.getText(), pascalCase.isSelected());
                dispose();
            } else {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "Please enter class name", "DSL API Generator", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        DslListenerBuilderDialog dialog = new DslListenerBuilderDialog();
        UIExtensionsKt.showDialog(dialog, 550, 600, true, false);
        System.exit(0);
    }

    public void setDialogListener(DialogListener listener) {
        mDialogListener = listener;
    }

    public interface DialogListener {
        void onGenerateClicked(LinkedHashMap<String, String> map, String className, Boolean pascalCase);
        void onCancelBtnClicked();
    }
}
