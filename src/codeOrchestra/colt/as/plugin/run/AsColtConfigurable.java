package codeOrchestra.colt.as.plugin.run;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @author Alexander Eliseyev
 */
public class AsColtConfigurable extends SettingsEditor<AsColtRunConfiguration> {

    private LabeledComponent<TextFieldWithBrowseButton> coltProjectPathEditor;
    private JPanel mainPanel;

    private Project project;

    public AsColtConfigurable(Project project) {
        this.project = project;

        createUIComponents();
    }

    private void createUIComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        coltProjectPathEditor = new LabeledComponent<TextFieldWithBrowseButton>();
        TextFieldWithBrowseButton coltProjectPathChooser = new TextFieldWithBrowseButton();
        coltProjectPathChooser.addBrowseFolderListener(
                "COLT Project Path",
                "Specify the COLT project location",
                null,
                new FileChooserDescriptor(true, false, false, false, false, false) {
                    @Override
                    public boolean isFileSelectable(VirtualFile file) {
                        return file.getPath().toLowerCase().endsWith(".colt");
                    }
                },
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT,
                false);
        coltProjectPathEditor.setComponent(coltProjectPathChooser);
        coltProjectPathEditor.setText("COLT Project Path");

        mainPanel.add(coltProjectPathEditor, BorderLayout.PAGE_START);
    }

    @Override
    protected void resetEditorFrom(AsColtRunConfiguration asColtRunConfiguration) {
        coltProjectPathEditor.getComponent().setText(asColtRunConfiguration.getColtProjectPath());
    }

    @Override
    protected void applyEditorTo(AsColtRunConfiguration asColtRunConfiguration) throws ConfigurationException {
        asColtRunConfiguration.setColtProjectPath(coltProjectPathEditor.getComponent().getText());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return mainPanel;
    }

    @Override
    protected void disposeEditor() {
    }

}
