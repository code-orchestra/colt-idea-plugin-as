package codeOrchestra.plugin.actions;

import codeOrchestra.colt.as.rpc.model.ColtCompilationResult;
import codeOrchestra.colt.as.rpc.model.ColtCompilerMessage;
import codeOrchestra.colt.core.rpc.ColtRemoteTransferableException;
import codeOrchestra.colt.core.rpc.security.InvalidAuthTokenException;
import codeOrchestra.plugin.ColtSettings;
import codeOrchestra.plugin.view.ColtConsole;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.ex.StatusBarEx;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Eliseyev
 */
public abstract class ColtAbstractCompileAction extends AsColeRemoteAction {

    protected ColtAbstractCompileAction(@Nullable String text) {
        super(text);
    }

    @Override
    protected final void doRemoteAction(final AnActionEvent event) throws InvalidAuthTokenException {
        getColtRemoteService().checkAuth(ColtSettings.getInstance().getSecurityToken());

        new Task.Backgroundable(ideaProject, "Live Build", false) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                try {
                    ColtConsole.getInstance(ideaProject).clear();
                    ColtCompilationResult coltCompilationResult = doRunCompilation(event);

                    final IdeFrame ideFrame = WindowManager.getInstance().getIdeFrame(myProject);
                    StatusBarEx statusBar = (StatusBarEx) ideFrame.getStatusBar();

                    if (coltCompilationResult.isSuccessful()) {
                        statusBar.notifyProgressByBalloon(MessageType.INFO, text + " is successful");
                    } else {
                        statusBar.notifyProgressByBalloon(MessageType.ERROR, text + " has failed, check the error messages");
                    }

                    // Report errors and warnings
                    for (ColtCompilerMessage coltCompilerMessage : coltCompilationResult.getErrorMessages()) {
                        ColtConsole.getInstance(ideaProject).append(coltCompilerMessage.getFullMessage(), ConsoleViewContentType.ERROR_OUTPUT);
                    }
                    for (ColtCompilerMessage coltCompilerMessage : coltCompilationResult.getWarningMessages()) {
                        ColtConsole.getInstance(ideaProject).append(coltCompilerMessage.getFullMessage(), ConsoleViewContentType.NORMAL_OUTPUT);
                    }
                } catch (ColtRemoteTransferableException e) {
                    // TODO: handle nicely
                    e.printStackTrace();
                }
            }
        }.queue();
    }

    protected abstract ColtCompilationResult doRunCompilation(AnActionEvent event) throws ColtRemoteTransferableException;

}
