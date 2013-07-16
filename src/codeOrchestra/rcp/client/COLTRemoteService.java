package codeOrchestra.rcp.client;

import codeOrchestra.lcs.rpc.model.COLTCompilationResult;
import codeOrchestra.lcs.rpc.model.COLTRemoteProject;
import codeOrchestra.lcs.rpc.model.COLTState;
import codeOrchestra.rpc.COLTRemoteTransferableException;
import codeOrchestra.rpc.security.InvalidShortCodeException;
import codeOrchestra.rpc.security.TooManyFailedCodeTypeAttemptsException;

/**
 * @author Alexander Eliseyev
 */
public interface COLTRemoteService {

    // Authorization methods

    void requestShortCode(String requestor) throws COLTRemoteTransferableException;

    String obtainAuthToken(String shortCode) throws TooManyFailedCodeTypeAttemptsException, InvalidShortCodeException;

    // Secured methods

    COLTState getState(String securityToken) throws COLTRemoteTransferableException;

    COLTCompilationResult runBaseCompilation(String securityToken) throws COLTRemoteTransferableException;

    void createProject(String securityToken, COLTRemoteProject project) throws COLTRemoteTransferableException;

    void loadProject(String securityToken, String path) throws COLTRemoteTransferableException;

    int ping();

}