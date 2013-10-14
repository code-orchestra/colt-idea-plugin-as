package codeOrchestra.colt.as.plugin.actions;

import codeOrchestra.colt.as.rpc.ColtAsRemoteService;
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
    protected ColtCompilationResult doRunCompilation(AnActionEvent event, ColtAsRemoteService coltRemoteService) throws ColtRemoteTransferableException {
        return coltRemoteService.runProductionCompilation(ColtSettings.getInstance().getSecurityToken(), true);
    }

}
