package codeOrchestra.plugin.actions;

import codeOrchestra.lcs.rpc.COLTRemoteTransferableException;
import codeOrchestra.lcs.rpc.model.COLTCompilationResult;
import codeOrchestra.plugin.COLTSettings;

/**
 * @author Alexander Eliseyev
 */
public class COLTProductionCompileAction extends COLTAbstractCompileAction {

    public COLTProductionCompileAction() {
        super("Production Build");
    }

    @Override
    protected COLTCompilationResult doRunCompilation() throws COLTRemoteTransferableException {
        return coltRemoteService.runProductionCompilation(COLTSettings.getInstance().getSecurityToken(), true);
    }

}
