package codeOrchestra.plugin.rpc;

import codeOrchestra.lcs.rpc.COLTRemoteService;
import codeOrchestra.lcs.rpc.COLTRemoteTransferableException;
import codeOrchestra.lcs.rpc.security.InvalidShortCodeException;
import codeOrchestra.lcs.rpc.security.TooManyFailedCodeTypeAttemptsException;
import codeOrchestra.plugin.COLTSettings;
import codeOrchestra.plugin.actions.COLTRemoteAction;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.lang.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Alexander Eliseyev
 */
public final class COLTRemoteServiceProvider {

    private static final COLTRemoteServiceProvider instance = new COLTRemoteServiceProvider();

    private static final String SERVICE_URL = "http://localhost:8092/rpc/coltService";
    private static final int MAX_ATTEMPT_COUNT = 5;

    public static COLTRemoteServiceProvider get() {
        return instance;
    }

    private COLTRemoteService coltRemoteService;

    private COLTRemoteServiceProvider() {
    }

    public synchronized COLTRemoteService getService() throws COLTRemoteServiceUnavailableException {
        return getService(0);
    }

    public boolean authorize() {
        COLTSettings coltSettings = COLTSettings.getInstance();

        if (coltSettings.isEmpty()) {
            return makeNewSecurityToken(true);
        }

        return true;
    }

    private boolean makeNewSecurityToken(boolean newRequest) {
        if (newRequest) {
            try {
                coltRemoteService.requestShortCode("COLT IntelliJ IDEA Plugin");
            } catch (COLTRemoteTransferableException e) {
                Messages.showErrorDialog("Can't request an authorization key from COLT.\nMake sure COLT is active and running", COLTRemoteAction.COLT_TITLE);
                return false;
            }
        }

        String shortCode = Messages.showInputDialog("Enter the short key displayed in COLT", COLTRemoteAction.COLT_TITLE, Messages.getQuestionIcon());
        if (StringUtils.isNotEmpty(shortCode)) {
            String token;
            try {
                token = coltRemoteService.obtainAuthToken(shortCode);
            } catch (TooManyFailedCodeTypeAttemptsException e) {
                Messages.showErrorDialog("Too many failed code input attempts, try again later", COLTRemoteAction.COLT_TITLE);
                return false;
            } catch (InvalidShortCodeException e) {
                int result = Messages.showDialog("Invalid short code entered", COLTRemoteAction.COLT_TITLE, new String[] {
                        "Try again", "Cancel"
                }, 0, Messages.getWarningIcon());

                if (result == 0) {
                    return makeNewSecurityToken(false);
                }

                return false;
            }

            COLTSettings.getInstance().setSecurityToken(token);
            Messages.showInfoMessage("Successfully connected to COLT", COLTRemoteAction.COLT_TITLE);

            return true;
        } else {
            int result = Messages.showDialog("Empty short code entered", COLTRemoteAction.COLT_TITLE, new String[] {
                    "Try again", "Cancel"
            }, 0, Messages.getWarningIcon());

            if (result == 0) {
                return makeNewSecurityToken(false);
            }
        }

        return false;
    }

    private COLTRemoteService getService(int tryCount) throws COLTRemoteServiceUnavailableException {
        if (tryCount > MAX_ATTEMPT_COUNT) {
            throw new COLTRemoteServiceUnavailableException();
        }

        initService();

        try {
            coltRemoteService.ping();
        } catch (Throwable t) {
            initService();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // ignore
            }

            return getService(tryCount + 1);
        }

        return coltRemoteService;
    }

    private void initService() throws COLTRemoteServiceUnavailableException {
        if (coltRemoteService == null) {
            try {
                JsonRpcHttpClient client = null;
                try {
                    client = new JsonRpcHttpClient(new URL(SERVICE_URL));
                } catch (MalformedURLException e) {
                    // should not happen
                }
                coltRemoteService = ProxyUtil.createClientProxy(getClass().getClassLoader(), COLTRemoteService.class, client);
            } catch (Throwable t) {
                throw new COLTRemoteServiceUnavailableException(t);
            }
        }

        if (coltRemoteService == null) {
            throw new COLTRemoteServiceUnavailableException();
        }
    }

}
