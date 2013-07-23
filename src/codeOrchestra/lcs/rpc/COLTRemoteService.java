package codeOrchestra.lcs.rpc;

import codeOrchestra.lcs.rpc.model.COLTCompilationResult;
import codeOrchestra.lcs.rpc.model.COLTRemoteProject;
import codeOrchestra.lcs.rpc.model.COLTState;
import codeOrchestra.lcs.rpc.COLTRemoteTransferableException;
import codeOrchestra.lcs.rpc.security.InvalidAuthTokenException;
import codeOrchestra.lcs.rpc.security.InvalidShortCodeException;
import codeOrchestra.lcs.rpc.security.TooManyFailedCodeTypeAttemptsException;

/**
 * @author Alexander Eliseyev
 */
public interface COLTRemoteService {

    // Authorization methods

    void requestShortCode(String requestor) throws COLTRemoteTransferableException;

    String obtainAuthToken(String shortCode) throws TooManyFailedCodeTypeAttemptsException, InvalidShortCodeException;

    void checkAuth(String securityToken) throws InvalidAuthTokenException;

    // Secured methods

    COLTState getState(String securityToken) throws COLTRemoteTransferableException;

    COLTCompilationResult runBaseCompilation(String securityToken) throws COLTRemoteTransferableException;

    COLTCompilationResult runProductionCompilation(String securityToken, boolean run) throws COLTRemoteTransferableException;

    void createProject(String securityToken, COLTRemoteProject project) throws COLTRemoteTransferableException;

    void loadProject(String securityToken, String path) throws COLTRemoteTransferableException;

    int ping();

}