package codeOrchestra.colt.as.plugin.actions;

import codeOrchestra.colt.as.rpc.ColtAsRemoteService;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Eliseyev
 */
public abstract class AsColeRemoteAction extends GenericColtRemoteAction {

    protected AsColeRemoteAction(@Nullable String text) {
        super(text);
    }

    protected ColtAsRemoteService getColtRemoteService() {
        return (ColtAsRemoteService) super.getColtRemoteService();
    }

}
