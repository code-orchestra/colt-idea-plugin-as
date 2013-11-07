package codeOrchestra.colt.as.plugin.run;

import codeOrchestra.colt.as.plugin.run.AsColtConfigurationFactory;
import codeOrchestra.colt.core.plugin.icons.Icons;
import com.intellij.execution.configurations.ConfigurationTypeBase;

/**
 * @author Alexander Eliseyev
 */
public class ColtConfigurationType extends ConfigurationTypeBase {

    public ColtConfigurationType() {
        super("codeOrchestra.colt", "COLT", "Start COLT Session", Icons.COLT_ICON_16);
        addFactory(new AsColtConfigurationFactory(this));
    }

}
