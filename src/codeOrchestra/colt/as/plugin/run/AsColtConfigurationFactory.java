package codeOrchestra.colt.as.plugin.run;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Eliseyev
 */
public class AsColtConfigurationFactory extends ConfigurationFactory {

    public AsColtConfigurationFactory(@NotNull ConfigurationType type) {
        super(type);
    }

    @Override
    public RunConfiguration createTemplateConfiguration(Project project) {
        return null; // TODO: implement
    }

    @Override
    public String getName() {
        return "COLT ActionScript";
    }
}
