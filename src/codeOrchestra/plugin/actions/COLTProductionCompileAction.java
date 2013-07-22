package codeOrchestra.plugin.actions;

import codeOrchestra.lcs.rpc.security.InvalidAuthTokenException;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * @author Alexander Eliseyev
 */
public class COLTProductionCompileAction extends COLTRemoteAction {

    public COLTProductionCompileAction() {
        super("Run Production Compilation");
    }

    @Override
    protected void doRemoteAction(AnActionEvent e) throws InvalidAuthTokenException {
        // TODO: implement
    }

}
