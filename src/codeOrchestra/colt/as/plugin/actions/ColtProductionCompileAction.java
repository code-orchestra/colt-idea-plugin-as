package codeOrchestra.colt.as.plugin.actions;

import codeOrchestra.colt.as.rpc.model.ColtCompilationResult;
import codeOrchestra.colt.core.rpc.ColtRemoteTransferableException;
import codeOrchestra.colt.core.plugin.ColtSettings;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * @author Alexander Eliseyev
 */
public class ColtProductionCompileAction extends ColtAbstractCompileAction {

    public ColtProductionCompileAction() {
        super("Production Build");
    }

    @Override
    protected ColtCompilationResult doRunCompilation(AnActionEvent event) throws ColtRemoteTransferableException {
        return getColtRemoteService().runProductionCompilation(ColtSettings.getInstance().getSecurityToken(), true);
    }

}
