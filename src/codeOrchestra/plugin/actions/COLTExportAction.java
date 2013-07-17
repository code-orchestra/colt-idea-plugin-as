package codeOrchestra.plugin.actions;

import codeOrchestra.lcs.rpc.COLTRemoteTransferableException;
import codeOrchestra.lcs.rpc.model.COLTRemoteProject;
import codeOrchestra.lcs.rpc.security.InvalidAuthTokenException;
import codeOrchestra.plugin.COLTSettings;
import com.intellij.lang.javascript.flex.FlexUtils;
import com.intellij.lang.javascript.flex.projectStructure.model.FlexBuildConfiguration;
import com.intellij.lang.javascript.flex.projectStructure.model.FlexBuildConfigurationManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.roots.*;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Dima Kruk
 * @author Alexander Eliseyev
 */
public class COLTExportAction extends COLTRemoteAction {

    public COLTExportAction() {
        super("Export COLT Project");
    }

    @Override
    protected void doRemoteAction(AnActionEvent e) throws InvalidAuthTokenException {
        Module[] allModules = ModuleManager.getInstance(project).getModules();
        saveConfigurationForModule(allModules[0]); // TODO: eliseyev: why only the first one?
    }

    private void saveConfigurationForModule(Module module) {
        ArrayList<String> libPaths = new ArrayList<String>();
        ArrayList<String> srcPaths = new ArrayList<String>();

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

        //TODO: need better solution for get path
        String modulePath = moduleRootManager.getContentRootUrls()[0].replace("file://", "");

        String workDir = modulePath + "/colt";

        (new File(workDir)).mkdir();

        String moduleName = module.getName();

        COLTRemoteProject project = new COLTRemoteProject();

        project.setName(moduleName);
        project.setParentIDEProjectPath(modulePath);
        project.setPath(workDir + "/" + moduleName + ".colt");

        project.setSources(srcPaths.toArray(new String[]{}));
        project.setLibraries(libPaths.toArray(new String[]{}));
//        project.setAssets(new String[]{"/Users/user/TMP/OriginalProject/assets"});
//        project.setHtmlTemplateDir("/Users/user/TMP/OriginalProject/html_template");

//        project.setFlashPlayerPath("/Applications/Flash Player Debugger.app");
        project.setFlexSDKPath(moduleRootManager.getSdk().getHomePath());

        final FlexBuildConfiguration activeBC = FlexBuildConfigurationManager.getInstance(module).getActiveConfiguration();

        project.setCustomConfigPath(activeBC.getCompilerOptions().getAdditionalConfigFilePath());
        project.setMainClass(FlexUtils.getPathToMainClassFile(activeBC.getMainClass(), module));

        project.setOutputFileName(activeBC.getOutputFileName());
        project.setOutputPath(workDir + "/output");
        project.setTargetPlayerVersion(activeBC.getDependencies().getTargetPlayer());

        project.setCompilerOptions(activeBC.getCompilerOptions().getAdditionalOptions());

        try {
            coltRemoteService.createProject(COLTSettings.getInstance().getSecurityToken(), project);
        } catch (COLTRemoteTransferableException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
