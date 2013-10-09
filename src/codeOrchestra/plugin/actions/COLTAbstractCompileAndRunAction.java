package codeOrchestra.plugin.actions;

import codeOrchestra.colt.as.rpc.model.ColtCompilationResult;
import codeOrchestra.colt.core.rpc.ColtRemoteTransferableException;
import codeOrchestra.utils.EventUtils;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Eliseyev
 */
public abstract class ColtAbstractCompileAndRunAction extends ColtAbstractCompileAction {

    public ColtAbstractCompileAndRunAction(@Nullable String text) {
        super(text);
    }

    @Override
    protected final ColtCompilationResult doRunCompilation(final AnActionEvent event) throws ColtRemoteTransferableException {
        ColtCompilationResult coltCompilationResult = doRunCompilationWithoutRun();

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

    protected abstract ColtCompilationResult doRunCompilationWithoutRun() throws ColtRemoteTransferableException;

}
