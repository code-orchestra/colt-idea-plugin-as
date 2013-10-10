package codeOrchestra.plugin.rpc;

import codeOrchestra.colt.core.rpc.ColtRemoteService;
import codeOrchestra.colt.core.rpc.ColtRemoteTransferableException;
import codeOrchestra.colt.core.rpc.security.InvalidShortCodeException;
import codeOrchestra.colt.core.rpc.security.TooManyFailedCodeTypeAttemptsException;
import codeOrchestra.plugin.ColtSettings;
import codeOrchestra.plugin.actions.GenericColtRemoteAction;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.lang.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Alexander Eliseyev
 */
public final class ColtRemoteServiceProvider {

    private static final ColtRemoteServiceProvider instance = new ColtRemoteServiceProvider();

    private static final String SERVICE_URL = "http://localhost:8092/rpc/coltService";

    private static final int MAX_ATTEMPT_COUNT = 5;

    public static ColtRemoteServiceProvider get() {
        return instance;
    }

    private ColtRemoteService coltRemoteService;

    private ColtRemoteServiceProvider() {
    }

    public synchronized ColtRemoteService getService() throws ColtRemoteServiceUnavailableException {
        return getService(0);
    }

    public boolean authorize() {
        ColtSettings coltSettings = ColtSettings.getInstance();

        if (coltSettings.isEmpty()) {
            return makeNewSecurityToken(true);
        }

        return true;
    }

    private boolean makeNewSecurityToken(boolean newRequest) {
        if (newRequest) {
            try {
                coltRemoteService.requestShortCode("COLT IntelliJ IDEA Plugin");
            } catch (ColtRemoteTransferableException e) {
                Messages.showErrorDialog("Can't request an authorization key from COLT.\nMake sure COLT is active and running", GenericColtRemoteAction.COLT_TITLE);
                return false;
            }
        }

        String shortCode = Messages.showInputDialog("Enter the short key displayed in COLT", GenericColtRemoteAction.COLT_TITLE, Messages.getQuestionIcon());
        if (StringUtils.isNotEmpty(shortCode)) {
            String token;
            try {
                token = coltRemoteService.obtainAuthToken(shortCode);
            } catch (TooManyFailedCodeTypeAttemptsException e) {
                Messages.showErrorDialog("Too many failed code input attempts, try again later", GenericColtRemoteAction.COLT_TITLE);
                return false;
            } catch (InvalidShortCodeException e) {
                int result = Messages.showDialog("Invalid short code entered", GenericColtRemoteAction.COLT_TITLE, new String[] {
                        "Try again", "Cancel"
                }, 0, Messages.getWarningIcon());

                if (result == 0) {
                    return makeNewSecurityToken(false);
                }

                return false;
            }

            ColtSettings.getInstance().setSecurityToken(token);
            Messages.showInfoMessage("Successfully connected to COLT", GenericColtRemoteAction.COLT_TITLE);

            return true;
        } else {
            int result = Messages.showDialog("Empty short code entered", GenericColtRemoteAction.COLT_TITLE, new String[] {
                    "Try again", "Cancel"
            }, 0, Messages.getWarningIcon());

            if (result == 0) {
                return makeNewSecurityToken(false);
            }
        }

        return false;
    }

    private ColtRemoteService getService(int tryCount) throws ColtRemoteServiceUnavailableException {
        if (tryCount > MAX_ATTEMPT_COUNT) {
            throw new ColtRemoteServiceUnavailableException();
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

    private void initService() throws ColtRemoteServiceUnavailableException {
        if (coltRemoteService == null) {
            try {
                JsonRpcHttpClient client = null;
                try {
                    client = new JsonRpcHttpClient(new URL(SERVICE_URL));
                } catch (MalformedURLException e) {
                    // should not happen
                }
                coltRemoteService = ProxyUtil.createClientProxy(getClass().getClassLoader(), ColtRemoteService.class, client);
            } catch (Throwable t) {
                throw new ColtRemoteServiceUnavailableException(t);
            }
        }

        if (coltRemoteService == null) {
            throw new ColtRemoteServiceUnavailableException();
        }
    }

}
