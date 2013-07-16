package codeOrchestra.plugin.actions;

import codeOrchestra.lcs.rpc.model.COLTRemoteProject;
import codeOrchestra.plugin.COLTSettings;
import codeOrchestra.rcp.client.COLTRemoteService;
import codeOrchestra.rpc.COLTRemoteTransferableException;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;
import com.intellij.lang.javascript.flex.FlexModuleType;
import com.intellij.lang.javascript.flex.FlexUtils;
import com.intellij.lang.javascript.flex.projectStructure.model.FlexBuildConfiguration;
import com.intellij.lang.javascript.flex.projectStructure.model.FlexBuildConfigurationManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.ui.Messages;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: dimakruk
 * Date: 7/11/13
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class COLTMenu extends AnAction {

    private COLTRemoteService coltRemoteService;
    private Project project;

    public COLTMenu() {
        super("COLT Menu");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        COLTSettings coltSettings = COLTSettings.getInstance();

        try {
            coltRemoteService = getColtRemoteService();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        if (coltRemoteService != null && coltSettings.getSecurityToken().isEmpty())
        {
            makeNewSecurityToken();
        }

        project = e.getData(PlatformDataKeys.PROJECT);

        Module[] allModules = ModuleManager.getInstance(project).getModules();

        saveConfigurationForMonule(allModules[0]);
    }

    private void saveConfigurationForMonule(Module module)
    {
        ArrayList<String> lib_paths = new ArrayList<String>();
        ArrayList<String> src_paths = new ArrayList<String>();

        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);

        final OrderEntry[] orderEntries = moduleRootManager.getOrderEntries();
        for (int j = 0; j < orderEntries.length; j++) {
            if (orderEntries[j] instanceof LibraryOrderEntry)
            {
                lib_paths.add(orderEntries[j].getPresentableName());
            }
        }
        final ContentEntry[] content = moduleRootManager.getContentEntries();
        for (ContentEntry contentEntry : content) {
            final SourceFolder[] sourceFolders = contentEntry.getSourceFolders();
            for (SourceFolder sourceFolder : sourceFolders) {
                final String url = sourceFolder.getUrl().replace("file://", "");
                src_paths.add(url);
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

        project.setSources(src_paths.toArray(new String[]{}));
        project.setLibraries(lib_paths.toArray(new String[]{}));
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

    private void makeNewSecurityToken()
    {
        try {
            coltRemoteService.requestShortCode("IDEA");
            String txt = Messages.showInputDialog(project, "Put key from COLT", "Input key", Messages.getQuestionIcon());
            if (txt != null && !txt.isEmpty()) {
                String token = coltRemoteService.obtainAuthToken(txt);
                COLTSettings.getInstance().setSecurityToken(token);
                Messages.showInfoMessage("Key is correct", "COLT Property");
            } else {
                Messages.showInfoMessage("Key isn't correct", "COLT Property");
            }
        } catch (COLTRemoteTransferableException e) {
            e.printStackTrace();
        }
    }

    private COLTRemoteService getColtRemoteService() throws MalformedURLException {
        JsonRpcHttpClient client = new JsonRpcHttpClient(new URL("http://localhost:8092/rpc/coltService"));
        return ProxyUtil.createClientProxy(getClass().getClassLoader(), COLTRemoteService.class, client);
    }
}
