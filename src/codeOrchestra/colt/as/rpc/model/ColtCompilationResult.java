package codeOrchestra.colt.as.rpc.model;

/**
 * @author Alexander Eliseyev
 */
public class ColtCompilationResult {

    private boolean successful;
    private ColtCompilerMessage[] errorMessages;
    private ColtCompilerMessage[] warningMessages;

    public ColtCompilationResult() {
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public ColtCompilerMessage[] getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(ColtCompilerMessage[] errorMessages) {
        this.errorMessages = errorMessages;
    }

    public ColtCompilerMessage[] getWarningMessages() {
        return warningMessages;
    }

    public void setWarningMessages(ColtCompilerMessage[] warningMessages) {
        this.warningMessages = warningMessages;
    }


}
