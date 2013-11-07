package codeOrchestra.colt.as.rpc.model;

import codeOrchestra.colt.core.rpc.model.ColtMessage;

/**
 * @author Alexander Eliseyev
 */
public class ColtCompilationResult {

    private boolean successful;
    private ColtMessage[] errorMessages;
    private ColtMessage[] warningMessages;

    public ColtCompilationResult() {
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public ColtMessage[] getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(ColtMessage[] errorMessages) {
        this.errorMessages = errorMessages;
    }

    public ColtMessage[] getWarningMessages() {
        return warningMessages;
    }

    public void setWarningMessages(ColtMessage[] warningMessages) {
        this.warningMessages = warningMessages;
    }


}
