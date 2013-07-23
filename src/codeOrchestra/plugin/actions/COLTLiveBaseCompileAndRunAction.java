package codeOrchestra.plugin.actions;

import codeOrchestra.lcs.rpc.COLTRemoteTransferableException;
import codeOrchestra.lcs.rpc.model.COLTCompilationResult;
import codeOrchestra.plugin.COLTSettings;

/**
 * @author Alexander Eliseyev
 */
public class COLTLiveBaseCompileAndRunAction extends COLTAbstractCompileAndRunAction {

    public COLTLiveBaseCompileAndRunAction() {
        super("Live Build and Exec Run");
    }

    @Override
    protected COLTCompilationResult doRunCompilationWithoutRun() throws COLTRemoteTransferableException {
        return coltRemoteService.runBaseCompilation(COLTSettings.getInstance().getSecurityToken(), false);
    }
}
