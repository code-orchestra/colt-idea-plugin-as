package codeOrchestra.plugin.actions;


import codeOrchestra.lcs.rpc.COLTRemoteService;
import codeOrchestra.lcs.rpc.security.InvalidAuthTokenException;
import codeOrchestra.plugin.COLTSettings;
import codeOrchestra.plugin.rpc.COLTRemoteServiceProvider;
import codeOrchestra.plugin.rpc.COLTRemoteServiceUnavailableException;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Eliseyev
 */
public abstract class COLTRemoteAction extends AnAction {

    public static final String COLT_TITLE = "COLT Connectivity";

    protected COLTRemoteAction(@Nullable String text) {
        super(text);
    }

    protected COLTRemoteService coltRemoteService;
    protected Project project;

    @Override
    public final void actionPerformed(AnActionEvent event) {
        // Get the project
        project = event.getData(PlatformDataKeys.PROJECT);
        if (project == null) {
            throw new IllegalStateException("COLT Remote Action must be aware of the current project");
        }

        // Get the service
        try {
            coltRemoteService = COLTRemoteServiceProvider.get().getService();
        } catch (COLTRemoteServiceUnavailableException e) {
            Messages.showErrorDialog(e.getMessage(), COLT_TITLE);
            return;
        }

        // Authorize if haven't done it yet
        if (!COLTRemoteServiceProvider.get().authorize()) {
            int result = Messages.showDialog("This plugin needs an authorization from the COLT application.", COLT_TITLE, new String[] {
                    "Try again", "Cancel"
            }, 0, Messages.getWarningIcon());

            if (result == 0) {
                actionPerformed(event);
            } else {
                return;
            }
        }

        // Do the action
        try {
            doRemoteAction(event);
        } catch (InvalidAuthTokenException e) {
            COLTSettings.getInstance().invalidate();
        }
    }

    protected abstract void doRemoteAction(AnActionEvent e) throws InvalidAuthTokenException;

}
