package codeOrchestra.colt.core.rpc;

import codeOrchestra.colt.as.rpc.model.ColtCompilerMessage;
import codeOrchestra.colt.core.rpc.model.ColtState;

/**
 * @author Alexander Eliseyev
 */
public interface ColtRemoteServiceListener {

    void onConnected();

    void onStateUpdate(ColtState state);

    void onMessage(ColtCompilerMessage coltCompilerMessage);

    void onDisconnected();

}
