package codeOrchestra.colt.core.rpc;

import codeOrchestra.colt.as.rpc.model.ColtCompilerMessage;

/**
 * @author Alexander Eliseyev
 */
public interface ColtRemoteServiceListener {

    void onConnected();

    void onMessage(ColtCompilerMessage coltCompilerMessage);

    void onDisconnected();

}
