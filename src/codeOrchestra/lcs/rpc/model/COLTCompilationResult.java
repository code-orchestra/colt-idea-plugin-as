package codeOrchestra.lcs.rpc.model;

import java.util.Arrays;

/**
 * @author Alexander Eliseyev
 */
public class COLTCompilationResult {

    private boolean successful;
    private COLTCompilerMessage[] errorMessages;
    private COLTCompilerMessage[] warningMessages;

    public COLTCompilationResult() {
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public COLTCompilerMessage[] getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(COLTCompilerMessage[] errorMessages) {
        this.errorMessages = errorMessages;
    }

    public COLTCompilerMessage[] getWarningMessages() {
        return warningMessages;
    }

    public void setWarningMessages(COLTCompilerMessage[] warningMessages) {
        this.warningMessages = warningMessages;
    }

    @Override
    public String toString() {
        return "COLTCompilationResult{" +
                "successful=" + successful +
                ", errorMessages=" + Arrays.toString(errorMessages) +
                ", warningMessages=" + Arrays.toString(warningMessages) +
                '}';
    }
}
