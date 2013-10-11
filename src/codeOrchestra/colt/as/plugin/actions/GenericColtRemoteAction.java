package codeOrchestra.colt.as.plugin.actions;


import codeOrchestra.colt.core.rpc.ColtRemoteService;
import codeOrchestra.colt.core.rpc.security.InvalidAuthTokenException;
import codeOrchestra.colt.as.plugin.ColtSettings;
import codeOrchestra.colt.as.rpc.ColtRemoteServiceProvider;
import codeOrchestra.colt.as.rpc.ColtRemoteServiceUnavailableException;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Eliseyev
 */
public abstract class GenericColtRemoteAction extends AnAction {

    public static final String COLT_TITLE = "Colt Connectivity";

    protected GenericColtRemoteAction(@Nullable String text) {
        super(text);
        this.text = text;
    }

    private ColtRemoteService coltRemoteService;
    protected Project ideaProject;
    protected String text;

    protected ColtRemoteService getColtRemoteService() {
        return coltRemoteService;
    }

    @Override
    public final void actionPerformed(AnActionEvent event) {
        // Get the ideaProject
        ideaProject = event.getData(PlatformDataKeys.PROJECT);
        if (ideaProject == null) {
            throw new IllegalStateException("Colt Remote Action must be aware of the current ideaProject");
        }

        // Get the service
        try {
            coltRemoteService = ColtRemoteServiceProvider.get().getService();
        } catch (ColtRemoteServiceUnavailableException e) {
            Messages.showErrorDialog(e.getMessage(), COLT_TITLE);
            return;
        }

        // Authorize if haven't done it yet
        if (!ColtRemoteServiceProvider.get().authorize()) {
            int result = Messages.showDialog("This plugin needs an authorization from the Colt application.", COLT_TITLE, new String[]{
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
            ColtSettings.getInstance().invalidate();
            actionPerformed(event);
        }
    }

    protected abstract void doRemoteAction(AnActionEvent e) throws InvalidAuthTokenException;

}
