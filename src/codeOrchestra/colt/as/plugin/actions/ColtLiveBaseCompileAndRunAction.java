package codeOrchestra.colt.as.plugin.actions;

import codeOrchestra.colt.as.rpc.ColtAsRemoteService;
import codeOrchestra.colt.as.rpc.model.ColtCompilationResult;
import codeOrchestra.colt.core.rpc.ColtRemoteTransferableException;
import codeOrchestra.colt.core.plugin.ColtSettings;

/**
 * @author Alexander Eliseyev
 */
public class ColtLiveBaseCompileAndRunAction extends ColtAbstractCompileAndRunAction {

    public ColtLiveBaseCompileAndRunAction() {
        super("Live Build and Exec Run");
    }

    @Override
    protected ColtCompilationResult doRunCompilationWithoutRun(ColtAsRemoteService coltRemoteService) throws ColtRemoteTransferableException {
        return coltRemoteService.runBaseCompilation(ColtSettings.getInstance().getSecurityToken(), false);
    }
}
