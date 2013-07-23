package codeOrchestra.plugin.actions;

import codeOrchestra.lcs.rpc.COLTRemoteTransferableException;
import codeOrchestra.lcs.rpc.model.COLTCompilationResult;
import codeOrchestra.utils.EventUtils;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Alexander Eliseyev
 */
public abstract class COLTAbstractCompileAndRunAction extends COLTAbstractCompileAction {

    public COLTAbstractCompileAndRunAction(@Nullable String text) {
        super(text);
    }

    @Override
    protected final COLTCompilationResult doRunCompilation(final AnActionEvent event) throws COLTRemoteTransferableException {
        COLTCompilationResult coltCompilationResult = doRunCompilationWithoutRun();

        if (coltCompilationResult.isSuccessful()) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    ApplicationManager.getApplication().runReadAction(new Runnable() {
                        @Override
                        public void run() {
                            final AnAction runConfiguration = ActionManager.getInstance().getAction("RunClass");
                            final AnActionEvent newEvent = EventUtils.cloneEvent(event);

                            for (int i = 0; i < 5; i++) {
                                runConfiguration.update(newEvent);
                            }
                            runConfiguration.actionPerformed(newEvent);
                        }
                    });
                }
            });
        }

        return coltCompilationResult;
    }

    protected abstract COLTCompilationResult doRunCompilationWithoutRun() throws COLTRemoteTransferableException;

}
