package codeOrchestra.plugin.actions;

import codeOrchestra.plugin.launch.ColtLauncher;
import codeOrchestra.plugin.launch.ColtPathNotConfiguredException;
import com.intellij.execution.ExecutionException;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;

import java.io.IOException;

/**
 * @author Alexander Eliseyev
 */
public class ColtLaunchAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        try {
            ColtLauncher.launch();
        } catch (ColtPathNotConfiguredException e) {
            Messages.showErrorDialog("Colt location not configured.\nTo specify the Colt path, go to Settings -> Colt", GenericColtRemoteAction.COLT_TITLE);
            return;
        } catch (ExecutionException e) {
            Messages.showErrorDialog("Can't start Colt:\n" + e.getMessage(), GenericColtRemoteAction.COLT_TITLE);
            return;
        } catch (IOException e) {
            Messages.showErrorDialog("Can't start Colt:\n" + e.getMessage(), GenericColtRemoteAction.COLT_TITLE);
            return;
        }
    }

}
