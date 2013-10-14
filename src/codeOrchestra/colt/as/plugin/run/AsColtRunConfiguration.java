package codeOrchestra.colt.as.plugin.run;

import codeOrchestra.colt.as.rpc.ColtAsRemoteService;
import codeOrchestra.colt.core.plugin.launch.ColtPathNotConfiguredException;
import codeOrchestra.colt.core.plugin.run.ColtRunProfileState;
import codeOrchestra.colt.core.rpc.ColtRemoteServiceProvider;
import codeOrchestra.utils.StringUtils;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Alexander Eliseyev
 */
public class AsColtRunConfiguration extends ModuleBasedConfiguration<AsRunConfigurationModule> implements RunConfiguration {

    private String coltProjectPath;

    public AsColtRunConfiguration(final String name, final Project project, ApplicationConfigurationType applicationConfigurationType) {
        this(name, project, applicationConfigurationType.getConfigurationFactories()[0]);
    }

    protected AsColtRunConfiguration(final String name, final Project project, final ConfigurationFactory factory) {
        super(name, new AsRunConfigurationModule(project), factory);
    }

    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new AsColtConfigurable(getProject());
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {
        ColtRemoteServiceProvider coltRemoteServiceProvider = getProject().getComponent(ColtRemoteServiceProvider.class);

        try {
            coltRemoteServiceProvider.initAndConnect(ColtAsRemoteService.class, coltProjectPath, getProject().getName());
        } catch (ColtPathNotConfiguredException e) {
            throw new ExecutionException("COLT installation path is not configured. Go to Preferences -> COLT", e);
        } catch (IOException e) {
            throw new ExecutionException("Error while trying to establish COLT connection", e);
        }
        return new ColtRunProfileState(getProject());
    }

    @Override
    public Collection<Module> getValidModules() {
        return Arrays.asList(ModuleManager.getInstance(getProject()).getModules());
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);

        this.coltProjectPath = element.getAttributeValue("coltProjectPath");
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);

        element.setAttribute("coltProjectPath", StringUtils.safe(this.coltProjectPath));
    }

    @Override
    protected ModuleBasedConfiguration createInstance() {
        ModuleBasedConfiguration<AsRunConfigurationModule> configuration =
                (ModuleBasedConfiguration<AsRunConfigurationModule>) getFactory().createTemplateConfiguration(getProject());
        configuration.setName(getName());
        return configuration;
    }

    public String getColtProjectPath() {
        return coltProjectPath;
    }

    public void setColtProjectPath(String coltProjectPath) {
        this.coltProjectPath = coltProjectPath;
    }
}
