package codeOrchestra.colt.core.rpc.discovery;

import codeOrchestra.colt.core.rpc.ColtRemoteService;

/**
 * @author Alexander Eliseyev
 */
public class ColtServiceLocator {

    public static <S extends ColtRemoteService> S locateService(Class<S> serviceClass, String projectPath) {
        // TODO: implement
        return null;
    }

}
