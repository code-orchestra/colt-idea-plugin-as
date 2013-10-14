package codeOrchestra.colt.core.rpc.discovery;

import codeOrchestra.colt.core.rpc.ColtRemoteService;
import com.intellij.openapi.progress.ProcessCanceledException;

/**
 * @author Alexander Eliseyev
 */
public interface ColtServiceLocator {

    <S extends ColtRemoteService> S waitForService(Class<S> serviceClass, String projectPath, String name) throws ProcessCanceledException;

    <S extends ColtRemoteService> S locateService(Class<S> serviceClass, String projectPath, String name);

}
