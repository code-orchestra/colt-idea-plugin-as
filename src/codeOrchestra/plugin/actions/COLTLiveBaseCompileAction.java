package codeOrchestra.plugin.actions;

import codeOrchestra.lcs.rpc.security.InvalidAuthTokenException;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * @author Alexander Eliseyev
 */
public class COLTLiveBaseCompileAction extends COLTRemoteAction {

    public COLTLiveBaseCompileAction() {
        super("Run Base Live Compilation");
    }

    @Override
    protected void doRemoteAction(AnActionEvent e) throws InvalidAuthTokenException {
        // TODO: implement
    }
}
