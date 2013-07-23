package codeOrchestra.plugin.actions;

import codeOrchestra.plugin.launch.COLTLauncher;
import codeOrchestra.plugin.launch.COLTPathNotConfiguredException;
import com.intellij.execution.ExecutionException;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;

import java.io.IOException;

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
        } catch (ExecutionException e) {
            Messages.showErrorDialog("Can't start COLT:\n" + e.getMessage(), COLTRemoteAction.COLT_TITLE);
            return;
        } catch (IOException e) {
            Messages.showErrorDialog("Can't start COLT:\n" + e.getMessage(), COLTRemoteAction.COLT_TITLE);
            return;
        }
    }

}
