package codeOrchestra.colt.as.plugin.actions;

import codeOrchestra.colt.as.rpc.model.ColtCompilationResult;
import codeOrchestra.colt.core.rpc.ColtRemoteTransferableException;
import codeOrchestra.colt.core.plugin.ColtSettings;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * @author Alexander Eliseyev
 */
public class ColtLiveBaseCompileAction extends ColtAbstractCompileAction {

    public ColtLiveBaseCompileAction() {
        super("Live Build");
    }

    @Override
    protected ColtCompilationResult doRunCompilation(AnActionEvent event) throws ColtRemoteTransferableException {
        return getColtRemoteService().runBaseCompilation(ColtSettings.getInstance().getSecurityToken());
    }

}
