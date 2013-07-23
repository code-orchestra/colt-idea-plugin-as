package codeOrchestra.plugin.actions;

import codeOrchestra.lcs.rpc.COLTRemoteTransferableException;
import codeOrchestra.lcs.rpc.model.COLTCompilationResult;
import codeOrchestra.plugin.COLTSettings;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * @author Alexander Eliseyev
 */
public class COLTLiveBaseCompileAction extends COLTAbstractCompileAction {

    public COLTLiveBaseCompileAction() {
        super("Live Build");
    }

    @Override
    protected COLTCompilationResult doRunCompilation(AnActionEvent event) throws COLTRemoteTransferableException {
        return coltRemoteService.runBaseCompilation(COLTSettings.getInstance().getSecurityToken());
    }

}
