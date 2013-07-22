package codeOrchestra.plugin.actions;

import codeOrchestra.plugin.launch.COLTLauncher;
import codeOrchestra.plugin.launch.COLTPathNotConfiguredException;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;

/**
 * @author Alexander Eliseyev
 */
public class COLTLaunchAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        try {
            COLTLauncher.launch();
        } catch (COLTPathNotConfiguredException e) {
            Messages.showErrorDialog("COLT location not configured.\nTo specify the COLT path, go to Settings -> COLT", COLTRemoteAction.COLT_TITLE);
            return;
        }

        // Report launch
        // TODO: implement
    }

}
