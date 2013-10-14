package codeOrchestra.colt.core.rpc;

/**
 * @author Alexander Eliseyev
 */
public interface ColtRemoteServiceListener {

    void onConnected();

    void onDisconnected();

}
