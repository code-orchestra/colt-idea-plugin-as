package codeOrchestra.colt.as.plugin.actions;

import codeOrchestra.colt.as.plugin.controller.AsColtPluginController;
import codeOrchestra.colt.as.rpc.ColtAsRemoteService;
import codeOrchestra.colt.core.rpc.security.InvalidAuthTokenException;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Eliseyev
 */
public abstract class ColtAbstractCompileAction extends AsGenericColtRemoteAction {

    protected ColtAbstractCompileAction(@Nullable String text) {
        super(text);
    }

    @Override
    protected void doRemoteAction(final AnActionEvent event, final ColtAsRemoteService coltRemoteService) throws InvalidAuthTokenException {
        AsColtPluginController.runCompilationAction(coltRemoteService, ideaProject, getCompilationAction(), event);
    }

    protected abstract AsColtPluginController.CompilationAction getCompilationAction();

}
