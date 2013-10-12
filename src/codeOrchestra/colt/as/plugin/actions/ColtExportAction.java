package codeOrchestra.colt.as.plugin.actions;

import codeOrchestra.colt.as.rpc.ColtAsRemoteService;
import codeOrchestra.colt.as.rpc.model.ColtRemoteProject;
import codeOrchestra.colt.as.rpc.model.codec.ColtRemoteProjectEncoder;
import codeOrchestra.colt.core.rpc.discovery.ColtServiceLocator;
import codeOrchestra.colt.core.workset.Workset;
import codeOrchestra.utils.XMLUtils;
import com.intellij.lang.javascript.flex.FlexUtils;
import com.intellij.lang.javascript.flex.projectStructure.model.FlexBuildConfiguration;
import com.intellij.lang.javascript.flex.projectStructure.model.FlexBuildConfigurationManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Pair;

import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dima Kruk
 * @author Alexander Eliseyev
 */
public class ColtExportAction extends AnAction {

    private Project ideaProject;

    public ColtExportAction() {
        super("Export Colt Project");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        ideaProject = e.getData(PlatformDataKeys.PROJECT);

        Module[] allModules = ModuleManager.getInstance(ideaProject).getModules();
        if (allModules.length == 0) {
            Messages.showErrorDialog("No modules in the ideaProject", GenericColtRemoteAction.COLT_TITLE);
            return;
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

            int result = Messages.showChooseDialog("Choose the Main module", GenericColtRemoteAction.COLT_TITLE, values, values[0], null);
            if (result == -1) {
                Messages.showErrorDialog("No Main module was selected", GenericColtRemoteAction.COLT_TITLE);
                return;
            } else {
                for (int i = 0; i < allModules.length; i++) {
                    modulesPairs.add(new Pair<Module, Boolean>(allModules[i], i == result));
                }
            }
        }

        ColtRemoteProject project = saveConfiguration(modulesPairs);
        Workset.addProjectPath(project.getPath(), true);

        ColtLaunchAction.launch();

        ColtServiceLocator serviceLocator = ideaProject.getComponent(ColtServiceLocator.class);
        ColtAsRemoteService coltAsRemoteService = serviceLocator.waitForService(ColtAsRemoteService.class, project.getPath(), project.getName());

        System.out.println(coltAsRemoteService);

        // TODO: connect to colt
    }

    private ColtRemoteProject saveConfiguration(List<Pair<Module, Boolean>> modules) {
        Module mainModule = null;
        for (Pair<Module, Boolean> modulePair : modules) {
            if (modulePair.getSecond()) {
                mainModule = modulePair.getFirst();
                break;
            }
        }

        ColtRemoteProject project = new ColtRemoteProject();

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

        String projectName = ideaProject.getName();

        project.setName(projectName);
        project.setParentIDEProjectPath(modulePath);
        project.setPath(workDir + "/" + projectName + ".colt");

        project.setSources(srcPaths.toArray(new String[]{}));
        project.setLibraries(libPaths.toArray(new String[]{}));

//        ideaProject.setAssets(new String[]{"/Users/user/TMP/OriginalProject/assets"});
//        ideaProject.setHtmlTemplateDir("/Users/user/TMP/OriginalProject/html_template");
//        ideaProject.setFlashPlayerPath("/Applications/Flash Player Debugger.app");

        project.setFlexSDKPath(mainModuleRootManager.getSdk().getHomePath());

        final FlexBuildConfiguration activeBC = FlexBuildConfigurationManager.getInstance(mainModule).getActiveConfiguration();

        project.setCustomConfigPath(activeBC.getCompilerOptions().getAdditionalConfigFilePath());
        project.setMainClass(FlexUtils.getPathToMainClassFile(activeBC.getMainClass(), mainModule));

        project.setOutputFileName(activeBC.getOutputFileName());
        project.setOutputPath(workDir + "/output");
        project.setTargetPlayerVersion(activeBC.getDependencies().getTargetPlayer());

        project.setCompilerOptions(activeBC.getCompilerOptions().getAdditionalOptions());

        try {
            XMLUtils.saveToFile(project.getPath(), new ColtRemoteProjectEncoder(project).toDocument());
        } catch (TransformerException e) {
            // TODO: handle nicely
            e.printStackTrace();
        }

        return project;
    }

}
