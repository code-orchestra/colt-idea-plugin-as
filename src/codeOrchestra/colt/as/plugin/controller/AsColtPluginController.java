package codeOrchestra.colt.as.plugin.controller;

import codeOrchestra.colt.as.plugin.actions.AsGenericColtRemoteAction;
import codeOrchestra.colt.as.rpc.ColtAsRemoteService;
import codeOrchestra.colt.as.rpc.model.ColtAsRemoteProject;
import codeOrchestra.colt.as.rpc.model.ColtCompilationResult;
import codeOrchestra.colt.as.rpc.model.codec.ColtAsRemoteProjectEncoder;
import codeOrchestra.colt.core.rpc.ColtRemoteServiceProvider;
import codeOrchestra.colt.core.rpc.model.ColtMessage;
import codeOrchestra.colt.core.plugin.ColtSettings;
import codeOrchestra.colt.core.rpc.ColtRemoteTransferableException;
import codeOrchestra.colt.core.rpc.security.InvalidAuthTokenException;
import utils.EventUtils;
import utils.XMLUtils;
import com.intellij.lang.javascript.flex.FlexUtils;
import com.intellij.lang.javascript.flex.projectStructure.model.FlexBuildConfiguration;
import com.intellij.lang.javascript.flex.projectStructure.model.FlexBuildConfigurationManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.ex.StatusBarEx;
import org.jetbrains.annotations.NotNull;

import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public class AsColtPluginController {

    public static final CompilationAction BASE_LIVE = new CompilationAction() {
        @Override
        public String getName() {
            return "Live Build";
        }
        @Override
        public ColtCompilationResult doRunCompilation(ColtAsRemoteService coltRemoteService, AnActionEvent actionEvent) throws ColtRemoteTransferableException {
            return coltRemoteService.runBaseCompilation(ColtSettings.getInstance().getSecurityToken());
        }
    };

    public static final CompilationAction BASE_LIVE_AND_RUN = new CompilationAndRunAction() {
        @Override
        public String getName() {
            return "Live Build and Exec Run";
        }

        @Override
        protected ColtCompilationResult doRunCompilationWithoutRun(ColtAsRemoteService coltRemoteService) throws ColtRemoteTransferableException {
            return coltRemoteService.runBaseCompilation(ColtSettings.getInstance().getSecurityToken(), false);
        }
    };

    public static final CompilationAction PRODUCTION = new CompilationAction() {
        @Override
        public String getName() {
            return "Production Build";
        }
        @Override
        public ColtCompilationResult doRunCompilation(ColtAsRemoteService coltRemoteService, AnActionEvent actionEvent) throws ColtRemoteTransferableException {
            return coltRemoteService.runProductionCompilation(ColtSettings.getInstance().getSecurityToken(), true);
        }
    };

    public static final CompilationAction PRODUCTION_AND_RUN = new CompilationAndRunAction() {
        @Override
        public String getName() {
            return "Production Build and Exec Run";
        }
        @Override
        protected ColtCompilationResult doRunCompilationWithoutRun(ColtAsRemoteService coltRemoteService) throws ColtRemoteTransferableException {
            return coltRemoteService.runProductionCompilation(ColtSettings.getInstance().getSecurityToken(), true);
        }
    };

    public static void runCompilationAction(final ColtAsRemoteService coltRemoteService, final Project ideaProject, final CompilationAction compilationAction, final AnActionEvent actionEvent) {
        // Report errors and warnings
        final ColtRemoteServiceProvider remoteServiceProvider = ideaProject.getComponent(ColtRemoteServiceProvider.class);

        if (!remoteServiceProvider.authorize()) {
            int result = Messages.showDialog("This plugin needs an authorization from the COLT application.", "COLT Connectivity", new String[]{
                    "Try again", "Cancel"
            }, 0, Messages.getWarningIcon());

            if (result == 0) {
                runCompilationAction(coltRemoteService, ideaProject, compilationAction, actionEvent);
            } else {
                return;
            }
        }

        try {
            coltRemoteService.checkAuth(ColtSettings.getInstance().getSecurityToken());
        } catch (InvalidAuthTokenException e) {
            ColtSettings.getInstance().invalidate();
            runCompilationAction(coltRemoteService, ideaProject, compilationAction, actionEvent);
        }

        new Task.Backgroundable(ideaProject, "COLT Build", false) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // ignore
                }
                ideaProject.getComponent(ColtRemoteServiceProvider.class).fireMessageAvailable("Running " + compilationAction.getName());

                try {
                    ColtCompilationResult coltCompilationResult = compilationAction.doRunCompilation(coltRemoteService, actionEvent);

                    final IdeFrame ideFrame = WindowManager.getInstance().getIdeFrame(myProject);
                    StatusBarEx statusBar = (StatusBarEx) ideFrame.getStatusBar();

                    if (coltCompilationResult.isSuccessful()) {
                        statusBar.notifyProgressByBalloon(MessageType.INFO, compilationAction.getName() + " is successful");
                    } else {
                        statusBar.notifyProgressByBalloon(MessageType.ERROR, compilationAction.getName() + " has failed, check the error messages");
                    }

                    for (ColtMessage coltCompilerMessage : coltCompilationResult.getErrorMessages()) {
                        remoteServiceProvider.fireCompileMessageAvailable(coltCompilerMessage);
                    }
                    for (ColtMessage coltCompilerMessage : coltCompilationResult.getWarningMessages()) {
                        remoteServiceProvider.fireCompileMessageAvailable(coltCompilerMessage);

                    }
                } catch (ColtRemoteTransferableException e) {
                    remoteServiceProvider.fireCompileMessageAvailable(new ColtMessage("Can't compile with COLT: " + e.getMessage()));
                }
            }
        }.queue();
    }

    public static String export(Project project) {
        return export(project, project.getName(), null);
    }

    /**
     * @param project IDEA project
     * @return COLT project path
     */
    public static String export(Project project, String projectName, String mainClass) {
        Module[] allModules = ModuleManager.getInstance(project).getModules();
        if (allModules.length == 0) {
            Messages.showErrorDialog("No modules in the ideaProject", AsGenericColtRemoteAction.COLT_TITLE);
            return null;
        }

        // Figure out the main module
        List<Pair<Module, Boolean>> modulesPairs = new ArrayList<Pair<Module, Boolean>>();
        if (allModules.length == 1) {
            modulesPairs.add(new Pair<Module, Boolean>(allModules[0], true));
        } else {
            String[] values = new String[allModules.length];
            for (int i = 0; i < allModules.length; i++) {
                values[i] = allModules[i].getName();
            }

            int result = Messages.showChooseDialog("Choose the Main module", AsGenericColtRemoteAction.COLT_TITLE, values, values[0], null);
            if (result == -1) {
                Messages.showErrorDialog("No Main module was selected", AsGenericColtRemoteAction.COLT_TITLE);
                return null;
            } else {
                for (int i = 0; i < allModules.length; i++) {
                    modulesPairs.add(new Pair<Module, Boolean>(allModules[i], i == result));
                }
            }
        }

        ColtAsRemoteProject coltProject = saveConfiguration(modulesPairs, project, projectName, mainClass);
        return coltProject.getPath();
    }

    private static ColtAsRemoteProject saveConfiguration(List<Pair<Module, Boolean>> modules, Project ideaProject, String projectName, String mainClass) {
        Module mainModule = null;
        for (Pair<Module, Boolean> modulePair : modules) {
            if (modulePair.getSecond()) {
                mainModule = modulePair.getFirst();
                break;
            }
        }

        ColtAsRemoteProject project = new ColtAsRemoteProject();

        ArrayList<String> libPaths = new ArrayList<String>();
        ArrayList<String> srcPaths = new ArrayList<String>();

        ModuleRootManager mainModuleRootManager = ModuleRootManager.getInstance(mainModule);

        // Prepare all the modules paths
        for (Pair<Module, Boolean> modulePair : modules) {
            Module module = modulePair.getFirst();
            ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
            final OrderEntry[] orderEntries = moduleRootManager.getOrderEntries();
            for (int j = 0; j < orderEntries.length; j++) {
                if (orderEntries[j] instanceof LibraryOrderEntry) {
                    libPaths.add(orderEntries[j].getPresentableName());
                }
            }
            final ContentEntry[] content = moduleRootManager.getContentEntries();
            for (ContentEntry contentEntry : content) {
                final SourceFolder[] sourceFolders = contentEntry.getSourceFolders();
                for (SourceFolder sourceFolder : sourceFolders) {
                    final String url = sourceFolder.getUrl().replace("file://", "");
                    srcPaths.add(url);
                }
            }
        }

        // Prepare the Colt shenannigans dir
        // TODO: need better solution for get path
        String modulePath = mainModuleRootManager.getContentRootUrls()[0].replace("file://", "");
        String workDir = ideaProject.getBasePath();

        project.setName(projectName);
        project.setParentIDEProjectPath(modulePath);
        project.setPath(workDir + "/" + projectName + ".colt");

        project.setSources(srcPaths.toArray(new String[]{}));
        project.setLibraries(libPaths.toArray(new String[]{}));

        project.setFlexSDKPath(mainModuleRootManager.getSdk().getHomePath());

        final FlexBuildConfiguration activeBC = FlexBuildConfigurationManager.getInstance(mainModule).getActiveConfiguration();

        project.setCustomConfigPath(activeBC.getCompilerOptions().getAdditionalConfigFilePath());

        if (mainClass == null) {
            project.setMainClass(FlexUtils.getPathToMainClassFile(activeBC.getMainClass(), mainModule));
        } else {
            project.setMainClass(mainClass);
        }

        project.setOutputFileName(activeBC.getOutputFileName());
        project.setOutputPath(workDir + "/output");
        project.setTargetPlayerVersion(activeBC.getDependencies().getTargetPlayer());

        project.setCompilerOptions(activeBC.getCompilerOptions().getAdditionalOptions());

        try {
            XMLUtils.saveToFile(project.getPath(), new ColtAsRemoteProjectEncoder(project).toDocument());
        } catch (TransformerException e) {
            // TODO: handle nicely
            e.printStackTrace();
        }

        return project;
    }

    public static interface CompilationAction {
        String getName();
        ColtCompilationResult doRunCompilation(ColtAsRemoteService coltRemoteService, AnActionEvent actionEvent) throws ColtRemoteTransferableException;
    }

    private static abstract class CompilationAndRunAction implements CompilationAction {
        @Override
        public final ColtCompilationResult doRunCompilation(ColtAsRemoteService coltRemoteService, final AnActionEvent actionEvent) throws ColtRemoteTransferableException {
            ColtCompilationResult coltCompilationResult = doRunCompilationWithoutRun(coltRemoteService);

            if (coltCompilationResult.isSuccessful()) {
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        ApplicationManager.getApplication().runReadAction(new Runnable() {
                            @Override
                            public void run() {
                                final AnAction runConfiguration = ActionManager.getInstance().getAction("RunClass");
                                final AnActionEvent newEvent = EventUtils.cloneEvent(actionEvent);

                                for (int i = 0; i < 5; i++) {
                                    runConfiguration.update(newEvent);
                                }
                                runConfiguration.actionPerformed(newEvent);
                            }
                        });
                    }
                });
            }

            return coltCompilationResult;
        }
        protected abstract ColtCompilationResult doRunCompilationWithoutRun(ColtAsRemoteService coltRemoteService) throws ColtRemoteTransferableException;
    }

}
