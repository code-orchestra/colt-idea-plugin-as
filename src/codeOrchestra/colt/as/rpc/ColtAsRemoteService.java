package codeOrchestra.colt.as.rpc;

import codeOrchestra.colt.as.rpc.model.ColtCompilationResult;
import codeOrchestra.colt.as.rpc.model.ColtRemoteProject;
import codeOrchestra.colt.core.rpc.ColtRemoteService;
import codeOrchestra.colt.core.rpc.ColtRemoteTransferableException;

/**
 * @author Alexander Eliseyev
 */
public interface ColtAsRemoteService extends ColtRemoteService {

    // Secured methods

    ColtCompilationResult runBaseCompilation(String securityToken) throws ColtRemoteTransferableException;

    ColtCompilationResult runBaseCompilation(String securityToken, boolean run) throws ColtRemoteTransferableException;

    ColtCompilationResult runProductionCompilation(String securityToken, boolean run) throws ColtRemoteTransferableException;

}
