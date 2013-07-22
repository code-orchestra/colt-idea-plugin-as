package codeOrchestra.plugin.view;

import codeOrchestra.plugin.COLTSettings;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.apache.commons.lang.ObjectUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * @author Alexander Eliseyev
 */
public class COLTConfigurationPage {

    private COLTSettings coltSettings;

    private JPanel mainPanel;
    private TextFieldWithBrowseButton fileChooser;

    public COLTConfigurationPage(COLTSettings coltSettings) {
        this.coltSettings = coltSettings;
        this.mainPanel = new JPanel();

        mainPanel.setLayout(new GridLayoutManager(5, 1, new Insets(10, 10, 10, 10), -1, -1));

        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(new JLabel("COLT path:"), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        mainPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));

        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        fileChooser = new TextFieldWithBrowseButton();
        fileChooser.addBrowseFolderListener(
                "COLT Installation Path",
                "Specify the COLT location",
                null,
                new FileChooserDescriptor(false, true, false, false, false, false),
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT,
                false);
        panel2.add(fileChooser, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        mainPanel.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));

        // Spacer
        mainPanel.add(new Spacer(), new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    private boolean isCOLTPathChanged() {
        return !ObjectUtils.equals(coltSettings.getCOLTPath(), fileChooser.getText());
    }

    public boolean isModified() {
        return isCOLTPathChanged();
    }

    public void apply() throws ConfigurationException {
        String coltPath = fileChooser.getText();

        File coltDir = new File(coltPath);
        if (!coltDir.exists() || !coltDir.isDirectory() || !new File(coltDir, "flex_sdk").exists()) {
            throw new ConfigurationException("Invalid COLT location is specified");
        }

        coltSettings.setCOLTPath(coltPath);
    }

    public void reset() {
        fileChooser.setText(coltSettings.getCOLTPath());
    }

    public JPanel getContentPane() {
        return mainPanel;
    }

    public void dispose() {
        // TODO: implement
    }
}
