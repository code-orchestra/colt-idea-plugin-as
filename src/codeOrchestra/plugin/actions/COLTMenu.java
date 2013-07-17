package codeOrchestra.plugin.actions;

import codeOrchestra.lcs.rpc.model.COLTRemoteProject;
import codeOrchestra.lcs.rpc.security.InvalidShortCodeException;
import codeOrchestra.lcs.rpc.security.TooManyFailedCodeTypeAttemptsException;
import codeOrchestra.plugin.COLTSettings;
import codeOrchestra.rcp.client.COLTRemoteService;
import codeOrchestra.lcs.rpc.COLTRemoteTransferableException;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;
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
import org.apache.commons.lang.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.io.File;

/**
 * @author Dima Kruk
 * @author Alexander Eliseyev
 */
public class COLTMenu extends AnAction {

    public static final String COLT_TITLE = "COLT Connectivity";
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

        if (coltRemoteService != null && coltSettings.getSecurityToken().isEmpty()) {
            makeNewSecurityToken(true);
        } else {
            // TODO: eliseyev - else ???
        }

        project = e.getData(PlatformDataKeys.PROJECT);

        Module[] allModules = ModuleManager.getInstance(project).getModules();

        saveConfigurationForModule(allModules[0]);
    }

    private void saveConfigurationForModule(Module module)
    {
        ArrayList<String> libPaths = new ArrayList<String>();
        ArrayList<String> srcPaths = new ArrayList<String>();

        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);

        final OrderEntry[] orderEntries = moduleRootManager.getOrderEntries();
        for (int j = 0; j < orderEntries.length; j++) {
            if (orderEntries[j] instanceof LibraryOrderEntry)
            {
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

    private boolean makeNewSecurityToken(boolean newRequest) {
        if (newRequest) {
            try {
                coltRemoteService.requestShortCode("COLT IntelliJ IDEA Plugin");
            } catch (COLTRemoteTransferableException e) {
                Messages.showErrorDialog("Can't request an authorization key from COLT.\nMake sure COLT is active and running", COLT_TITLE);
                return false;
            }
        }

        String shortCode = Messages.showInputDialog(project, "Enter the short key displayed in COLT", COLT_TITLE, Messages.getQuestionIcon());
        if (StringUtils.isNotEmpty(shortCode)) {
            String token;
            try {
                token = coltRemoteService.obtainAuthToken(shortCode);
            } catch (TooManyFailedCodeTypeAttemptsException e) {
                Messages.showErrorDialog("Too many failed code input attempts, try again later", COLT_TITLE);
                return false;
            } catch (InvalidShortCodeException e) {
                int result = Messages.showDialog("Invalid short code entered", COLT_TITLE, new String[] {
                        "Try again", "Cancel"
                }, 0, Messages.getWarningIcon());

                if (result == 0) {
                    return makeNewSecurityToken(false);
                }

                return false;
            }

            COLTSettings.getInstance().setSecurityToken(token);
            Messages.showInfoMessage("Successfully connected to COLT", COLT_TITLE);
        } else {
            int result = Messages.showDialog("Empty short code entered", COLT_TITLE, new String[] {
                    "Try again", "Cancel"
            }, 0, Messages.getWarningIcon());

            if (result == 0) {
                return makeNewSecurityToken(false);
            }
        }

        return false;
    }

    private COLTRemoteService getColtRemoteService() throws MalformedURLException {
        JsonRpcHttpClient client = new JsonRpcHttpClient(new URL("http://localhost:8092/rpc/coltService"));
        return ProxyUtil.createClientProxy(getClass().getClassLoader(), COLTRemoteService.class, client);
    }
}
