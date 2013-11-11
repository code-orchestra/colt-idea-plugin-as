package codeOrchestra.colt.as.plugin.actions;

import codeOrchestra.colt.as.plugin.controller.AsColtPluginController;
import codeOrchestra.colt.as.plugin.run.AsColtConfigurationFactory;
import codeOrchestra.colt.as.plugin.run.AsColtConfigurationType;
import codeOrchestra.colt.as.plugin.run.AsColtRunConfiguration;
import codeOrchestra.colt.core.plugin.icons.Icons;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunManagerEx;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author Alexander Eliseyev
 */
public class AsRunWithColtAction extends AnAction {

    public AsRunWithColtAction() {
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);

        e.getPresentation().setIcon(Icons.COLT_ICON_16);

        if (e.getProject() == null) {
            e.getPresentation().setEnabled(false);
        }

        VirtualFile[] virtualFileArray = (VirtualFile[]) e.getDataContext().getData("virtualFileArray");
        if (virtualFileArray != null && virtualFileArray.length == 1 && !virtualFileArray[0].isDirectory() &&
                (virtualFileArray[0].getPath().toLowerCase().endsWith(".as") || virtualFileArray[0].getPath().toLowerCase().endsWith(".mxml"))) {
            e.getPresentation().setEnabled(true);
        } else {
            e.getPresentation().setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(AnActionEvent actionEvent) {
        VirtualFile[] virtualFileArray = (VirtualFile[]) actionEvent.getDataContext().getData("virtualFileArray");
        String mainClassPath = virtualFileArray[0].getPath();
        String mainClassName = virtualFileArray[0].getNameWithoutExtension();

        String runConfigurationName = mainClassName;
        runWithColt(actionEvent, mainClassPath, mainClassName, runConfigurationName);
    }

    private void runWithColt(AnActionEvent actionEvent, String mainClassPath, String mainClassName, String runConfigurationName) {
        // 0 - check if such configuration already exists
        RunManager runManager = RunManager.getInstance(actionEvent.getProject());
        for (RunnerAndConfigurationSettings runnerAndConfigurationSettings : runManager.getConfigurationSettings(getColtConfigurationType(runManager))) {
            if (runnerAndConfigurationSettings.getName().equals(runConfigurationName)) {
                if (runnerAndConfigurationSettings.getConfiguration() instanceof AsColtRunConfiguration) {
                    ProgramRunnerUtil.executeConfiguration(actionEvent.getProject(), runnerAndConfigurationSettings, DefaultRunExecutor.getRunExecutorInstance());
                    return;
                } else {
                    runWithColt(actionEvent, mainClassPath, mainClassName, runConfigurationName + " (1)");
                    return;
                }
            }
        }

        // 1 - export project
        String projectPath = AsColtPluginController.export(actionEvent.getProject(), mainClassName, mainClassPath);

        // 2 - create a run configuration
        AsColtConfigurationType coltConfigurationType = getColtConfigurationType(runManager);
        if (coltConfigurationType == null) {
            throw new IllegalStateException("Can't locate COLT configuration type");
        }
        AsColtConfigurationFactory coltConfigurationFactory = null;
        for (ConfigurationFactory configurationFactory : coltConfigurationType.getConfigurationFactories()) {
            if (configurationFactory instanceof AsColtConfigurationFactory) {
                coltConfigurationFactory = (AsColtConfigurationFactory) configurationFactory;
                break;
            }
        }
        if (coltConfigurationFactory == null) {
            throw new IllegalStateException("Can't locate COLT configuration factory");
        }
        RunnerAndConfigurationSettings runConfiguration = runManager.createRunConfiguration(runConfigurationName, coltConfigurationFactory);
        AsColtRunConfiguration asColtRunConfiguration = (AsColtRunConfiguration) runConfiguration.getConfiguration();
        asColtRunConfiguration.setColtProjectPath(projectPath);
        if (runManager instanceof RunManagerEx) {
            ((RunManagerEx) runManager).addConfiguration(runConfiguration, false);
            ((RunManagerEx) runManager).setSelectedConfiguration(runConfiguration);
        }

        // 3 - run configuration
        ProgramRunnerUtil.executeConfiguration(actionEvent.getProject(), runConfiguration, DefaultRunExecutor.getRunExecutorInstance());
    }

    private AsColtConfigurationType getColtConfigurationType(RunManager runManager) {
        AsColtConfigurationType coltConfigurationType = null;
        for (ConfigurationType configurationType : runManager.getConfigurationFactories()) {
            if (configurationType instanceof AsColtConfigurationType) {
                coltConfigurationType = (AsColtConfigurationType) configurationType;
                break;
            }
        }
        return coltConfigurationType;
    }

}
