package codeOrchestra.colt.core.rpc.discovery;

import codeOrchestra.colt.core.rpc.ColtRemoteService;

/**
 * @author Alexander Eliseyev
 */
public interface ColtServiceLocator {

    <S extends ColtRemoteService> S waitForService(Class<S> serviceClass, String projectPath, String name);

    <S extends ColtRemoteService> S locateService(Class<S> serviceClass, String projectPath, String name);

}
