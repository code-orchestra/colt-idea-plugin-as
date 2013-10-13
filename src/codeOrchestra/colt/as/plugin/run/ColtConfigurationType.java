package codeOrchestra.colt.as.plugin.run;

import com.intellij.execution.configurations.ConfigurationTypeBase;

import javax.swing.*;

/**
 * @author Alexander Eliseyev
 */
public class ColtConfigurationType extends ConfigurationTypeBase {

    private static final Icon ICON = new ImageIcon(ColtConfigurationType.class.getResource("/codeOrchestra/colt/as/plugin/icons/colt_16.png"));

    public ColtConfigurationType() {
        super("codeOrchestra.colt", "COLT", "Start COLT Session", ICON);
    }

}
