package codeOrchestra.colt.as.plugin.actions;

import codeOrchestra.colt.as.rpc.model.ColtCompilationResult;
import codeOrchestra.colt.core.rpc.ColtRemoteTransferableException;
import codeOrchestra.colt.as.plugin.ColtSettings;

/**
 * @author Alexander Eliseyev
 */
public class ColtLiveBaseCompileAndRunAction extends ColtAbstractCompileAndRunAction {

    public ColtLiveBaseCompileAndRunAction() {
        super("Live Build and Exec Run");
    }

    @Override
    protected ColtCompilationResult doRunCompilationWithoutRun() throws ColtRemoteTransferableException {
        return getColtRemoteService().runBaseCompilation(ColtSettings.getInstance().getSecurityToken(), false);
    }
}
