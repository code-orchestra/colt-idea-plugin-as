package codeOrchestra.rpc.security;

import codeOrchestra.rpc.COLTRemoteTransferableException;

/**
 * @author Alexander Eliseyev
 */
public class TooManyFailedCodeTypeAttemptsException extends COLTRemoteTransferableException {

  public TooManyFailedCodeTypeAttemptsException() {
    super();
  }

}
