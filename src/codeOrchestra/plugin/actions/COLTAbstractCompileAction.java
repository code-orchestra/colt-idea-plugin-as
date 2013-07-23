package codeOrchestra.plugin.actions;

import codeOrchestra.lcs.rpc.COLTRemoteTransferableException;
import codeOrchestra.lcs.rpc.model.COLTCompilationResult;
import codeOrchestra.lcs.rpc.model.COLTCompilerMessage;
import codeOrchestra.lcs.rpc.security.InvalidAuthTokenException;
import codeOrchestra.plugin.COLTSettings;
import codeOrchestra.plugin.view.COLTConsole;
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
public abstract class COLTAbstractCompileAction extends COLTRemoteAction {

    protected COLTAbstractCompileAction(@Nullable String text) {
        super(text);
    }

    @Override
    protected final void doRemoteAction(final AnActionEvent event) throws InvalidAuthTokenException {
        coltRemoteService.checkAuth(COLTSettings.getInstance().getSecurityToken());

        new Task.Backgroundable(ideaProject, "Live Build", false) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                try {
                    COLTConsole.getInstance(ideaProject).clear();
                    COLTCompilationResult coltCompilationResult = doRunCompilation(event);

                    final IdeFrame ideFrame = WindowManager.getInstance().getIdeFrame(myProject);
                    StatusBarEx statusBar = (StatusBarEx)ideFrame.getStatusBar();

                    if (coltCompilationResult.isSuccessful()) {
                        statusBar.notifyProgressByBalloon(MessageType.INFO, text + " is successful");
                    } else {
                        statusBar.notifyProgressByBalloon(MessageType.ERROR, text + " has failed, check the error messages");
                    }

                    // Report errors and warnings
                    for (COLTCompilerMessage coltCompilerMessage : coltCompilationResult.getErrorMessages()) {
                        COLTConsole.getInstance(ideaProject).append(coltCompilerMessage.getFullMessage(), ConsoleViewContentType.ERROR_OUTPUT);
                    }
                    for (COLTCompilerMessage coltCompilerMessage : coltCompilationResult.getWarningMessages()) {
                        COLTConsole.getInstance(ideaProject).append(coltCompilerMessage.getFullMessage(), ConsoleViewContentType.NORMAL_OUTPUT);
                    }
                } catch (COLTRemoteTransferableException e) {
                    // TODO: handle nicely
                    e.printStackTrace();
                }
            }
        }.queue();
    }

    protected abstract COLTCompilationResult doRunCompilation(AnActionEvent event) throws COLTRemoteTransferableException;

}
