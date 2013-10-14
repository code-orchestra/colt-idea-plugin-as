package codeOrchestra.colt.as.plugin.run;

import com.intellij.execution.BeforeRunTask;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
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
        return new AsColtRunConfiguration("", project, AsColtConfigurationFactory.this);
    }

    @Override
    public String getName() {
        return "COLT ActionScript";
    }

    /**
     * In this method you can configure defaults for the task, which are preferable to be used for your particular configuration type
     * @param providerID
     * @param task
     */
    public void configureBeforeRunTaskDefaults(Key<? extends BeforeRunTask> providerID, BeforeRunTask task) {
        if ("Make".equals(providerID.toString())) {
            task.setEnabled(false);
        }
    }

}
