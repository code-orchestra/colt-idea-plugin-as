package codeOrchestra.colt.core.plugin.run;

import codeOrchestra.colt.as.plugin.controller.AsColtPluginController;
import codeOrchestra.colt.as.rpc.ColtAsRemoteService;
import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationPerRunnerSettings;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Eliseyev
 */
public class ColtRunProfileState implements RunProfileState {

    private Project project;

    public ColtRunProfileState(Project project) {
        this.project = project;
    }

    @Nullable
    @Override
    public ExecutionResult execute(Executor executor, @NotNull ProgramRunner programRunner) throws ExecutionException {
        ConsoleViewImpl consoleView = new ConsoleViewImpl(project, false);
        ColtRemoteProcessHandler process = new ColtRemoteProcessHandler(project);
        consoleView.attachToProcess(process);

        ColtAsRemoteService service = process.getService();
        AsColtPluginController.runCompilationAction(service, project, AsColtPluginController.BASE_LIVE, null);

        return new DefaultExecutionResult(consoleView, process);
    }

    @Override
    public RunnerSettings getRunnerSettings() {
        return null;
    }

    @Override
    public ConfigurationPerRunnerSettings getConfigurationSettings() {
        return null;
    }

}
