package codeOrchestra.colt.as.plugin.controller;

import codeOrchestra.colt.as.plugin.actions.AsGenericColtRemoteAction;
import codeOrchestra.colt.as.rpc.model.ColtRemoteProject;
import codeOrchestra.colt.as.rpc.model.codec.ColtRemoteProjectEncoder;
import codeOrchestra.utils.XMLUtils;
import com.intellij.lang.javascript.flex.FlexUtils;
import com.intellij.lang.javascript.flex.projectStructure.model.FlexBuildConfiguration;
import com.intellij.lang.javascript.flex.projectStructure.model.FlexBuildConfigurationManager;
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
 * @author Alexander Eliseyev
 */
public class AsColtPluginController {

    /**
     * @param project IDEA project
     * @return COLT project path
     */
    public static String export(Project project) {
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

        ColtRemoteProject coltProject = saveConfiguration(modulesPairs, project);
        return coltProject.getPath();
    }

    private static ColtRemoteProject saveConfiguration(List<Pair<Module, Boolean>> modules, Project ideaProject) {
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
