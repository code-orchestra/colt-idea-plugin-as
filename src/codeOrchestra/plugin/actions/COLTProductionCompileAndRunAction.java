package codeOrchestra.plugin.actions;

import codeOrchestra.lcs.rpc.COLTRemoteTransferableException;
import codeOrchestra.lcs.rpc.model.COLTCompilationResult;
import codeOrchestra.plugin.COLTSettings;

/**
 * @author Alexander Eliseyev
 */
public class COLTProductionCompileAndRunAction extends COLTAbstractCompileAndRunAction {

    public COLTProductionCompileAndRunAction() {
        super("Production Build and Exec Run");
    }

    @Override
    protected COLTCompilationResult doRunCompilationWithoutRun() throws COLTRemoteTransferableException {
        return coltRemoteService.runProductionCompilation(COLTSettings.getInstance().getSecurityToken(), false);
    }

}
