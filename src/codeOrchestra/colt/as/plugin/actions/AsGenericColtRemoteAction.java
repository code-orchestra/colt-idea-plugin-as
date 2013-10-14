package codeOrchestra.colt.as.plugin.actions;


import codeOrchestra.colt.as.rpc.ColtAsRemoteService;
import codeOrchestra.colt.core.plugin.ColtSettings;
import codeOrchestra.colt.core.rpc.ColtRemoteServiceProvider;
import codeOrchestra.colt.core.rpc.security.InvalidAuthTokenException;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Eliseyev
 */
public abstract class AsGenericColtRemoteAction extends AnAction {

    public static final String COLT_TITLE = "COLT Connectivity";

    protected AsGenericColtRemoteAction(@Nullable String text) {
        super(text);
        this.text = text;
    }

    protected Project ideaProject;
    protected String text;

    @Override
    public void update(AnActionEvent e) {
        super.update(e);

        Project project = e.getProject();
        if (project != null) {
            ColtRemoteServiceProvider remoteServiceProvider = project.getComponent(ColtRemoteServiceProvider.class);
            e.getPresentation().setEnabled(remoteServiceProvider.isConnected());
        }
    }

    @Override
    public final void actionPerformed(AnActionEvent event) {
        // Get the ideaProject
        ideaProject = event.getData(PlatformDataKeys.PROJECT);
        if (ideaProject == null) {
            throw new IllegalStateException("Colt Remote Action must be aware of the current ideaProject");
        }

        // Get the service
        ColtRemoteServiceProvider remoteServiceProvider = ideaProject.getComponent(ColtRemoteServiceProvider.class);
        ColtAsRemoteService coltRemoteService = remoteServiceProvider.getService();

        /*
        try {
            coltRemoteService = DeadClass.get().getService();
        } catch (ColtRemoteServiceUnavailableException e) {
            Messages.showErrorDialog(e.getMessage(), COLT_TITLE);
            return;
        }
        */

        // Authorize if haven't done it yet
        if (!remoteServiceProvider.authorize()) {
            int result = Messages.showDialog("This plugin needs an authorization from the COLT application.", COLT_TITLE, new String[]{
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
            doRemoteAction(event, coltRemoteService);
        } catch (InvalidAuthTokenException e) {
            ColtSettings.getInstance().invalidate();
            actionPerformed(event);
        }
    }

    protected abstract void doRemoteAction(AnActionEvent e, ColtAsRemoteService coltRemoteService) throws InvalidAuthTokenException;

}
