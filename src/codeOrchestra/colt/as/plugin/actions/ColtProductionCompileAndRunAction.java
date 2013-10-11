package codeOrchestra.colt.as.plugin.actions;

import codeOrchestra.colt.as.rpc.model.ColtCompilationResult;
import codeOrchestra.colt.core.rpc.ColtRemoteTransferableException;
import codeOrchestra.colt.as.plugin.ColtSettings;

/**
 * @author Alexander Eliseyev
 */
public class ColtProductionCompileAndRunAction extends ColtAbstractCompileAndRunAction {

    public ColtProductionCompileAndRunAction() {
        super("Production Build and Exec Run");
    }

    @Override
    protected ColtCompilationResult doRunCompilationWithoutRun() throws ColtRemoteTransferableException {
        return getColtRemoteService().runProductionCompilation(ColtSettings.getInstance().getSecurityToken(), false);
    }

}
