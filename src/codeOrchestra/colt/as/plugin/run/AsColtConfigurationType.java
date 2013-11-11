package codeOrchestra.colt.as.plugin.run;

import codeOrchestra.colt.core.plugin.icons.Icons;
import com.intellij.execution.configurations.ConfigurationTypeBase;

/**
 * @author Alexander Eliseyev
 */
public class AsColtConfigurationType extends ConfigurationTypeBase {

    public AsColtConfigurationType() {
        super("codeOrchestra.colt.as", "COLT ActionScript", "Start COLT Session", Icons.COLT_ICON_16);
        addFactory(new AsColtConfigurationFactory(this));
    }

}
