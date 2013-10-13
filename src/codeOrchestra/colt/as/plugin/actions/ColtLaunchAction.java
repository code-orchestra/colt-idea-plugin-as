package codeOrchestra.colt.as.plugin.actions;

import codeOrchestra.colt.core.plugin.launch.ColtLauncher;
import codeOrchestra.colt.core.plugin.launch.ColtPathNotConfiguredException;
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
        launch();
    }

    public static void launch() {
        try {
            ColtLauncher.launch();
        } catch (ColtPathNotConfiguredException e) {
            Messages.showErrorDialog("COLT location not configured.\nTo specify the COLT path, go to Settings -> COLT", GenericColtRemoteAction.COLT_TITLE);
            return;
        } catch (ExecutionException e) {
            Messages.showErrorDialog("Can't start COLT:\n" + e.getMessage(), GenericColtRemoteAction.COLT_TITLE);
            return;
        } catch (IOException e) {
            Messages.showErrorDialog("Can't start COLT:\n" + e.getMessage(), GenericColtRemoteAction.COLT_TITLE);
            return;
        }
    }

}
