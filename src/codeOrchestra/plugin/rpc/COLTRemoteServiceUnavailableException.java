package codeOrchestra.plugin.rpc;

/**
 * @author Alexander Eliseyev
 */
public class COLTRemoteServiceUnavailableException extends Exception {

    private static final String DEFAULT_MESSAGE = "Can't reach COLT remote service API";

    public COLTRemoteServiceUnavailableException() {
        this(DEFAULT_MESSAGE);
    }

    public COLTRemoteServiceUnavailableException(String s) {
        super(s);
    }

    public COLTRemoteServiceUnavailableException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public COLTRemoteServiceUnavailableException(Throwable throwable) {
        this(DEFAULT_MESSAGE, throwable);
    }
}
