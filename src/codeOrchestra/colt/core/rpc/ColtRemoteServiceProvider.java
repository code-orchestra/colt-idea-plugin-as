package codeOrchestra.colt.core.rpc;

import codeOrchestra.colt.as.plugin.actions.AsGenericColtRemoteAction;
import codeOrchestra.colt.core.plugin.ColtSettings;
import codeOrchestra.colt.core.plugin.launch.ColtLauncher;
import codeOrchestra.colt.core.plugin.launch.ColtPathNotConfiguredException;
import codeOrchestra.colt.core.rpc.discovery.ColtServiceLocator;
import codeOrchestra.colt.core.rpc.security.InvalidShortCodeException;
import codeOrchestra.colt.core.rpc.security.TooManyFailedCodeTypeAttemptsException;
import codeOrchestra.colt.core.workset.Workset;
import com.intellij.execution.ExecutionException;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public class ColtRemoteServiceProvider extends AbstractProjectComponent implements ProjectComponent {

    public ColtRemoteServiceProvider(Project project) {
        super(project);
        connectionFinisherThread = new ConnectionFinisherThread();
    }

    private ColtRemoteService coltRemoteService;
    private List<ColtRemoteServiceListener> listeners = new ArrayList<ColtRemoteServiceListener>();
    private ConnectionFinisherThread connectionFinisherThread;

    public <S extends ColtRemoteService> void initAndConnect(Class<S> serviceClass, String projectPath, String projectName) throws ColtPathNotConfiguredException, ExecutionException, IOException, ProcessCanceledException {
        // 1 - try to connect to existing COLT instance
        ProgressManager.progress("Trying to connect to existing COLT instance");
        ColtServiceLocator serviceLocator = myProject.getComponent(ColtServiceLocator.class);
        S service = serviceLocator.locateService(serviceClass, projectPath, projectName);
        if (service != null) {
            setColtRemoteService(service);
            return;
        }

        // 2 - if it fails, start the COLT
        ProgressManager.progress("Starting new COLT instance");
        Workset.addProjectPath(projectPath, true);
        ColtLauncher.launch();

        // 3 - and connect to it
        ProgressManager.progress("Trying to connect to COLT");
        setColtRemoteService(serviceLocator.waitForService(serviceClass, projectPath, projectName));
    }

    public synchronized void addListener(ColtRemoteServiceListener listener) {
        listeners.add(listener);
    }

    public synchronized void removeListener(ColtRemoteServiceListener listener) {
        listeners.remove(listener);
    }

    private synchronized void setColtRemoteService(ColtRemoteService coltRemoteService) {
        this.coltRemoteService = coltRemoteService;

        if (coltRemoteService != null) {
            for (ColtRemoteServiceListener listener : listeners) {
                listener.onConnected();
            }
        } else {
            for (ColtRemoteServiceListener listener : listeners) {
                listener.onDisconnected();
            }
        }
    }

    public boolean isConnected() {
        return coltRemoteService != null;
    }

    public void disconnect() {
        setColtRemoteService(null);
    }

    public <S extends ColtRemoteService> S getService() {
        return (S) coltRemoteService;
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
                coltRemoteService.requestShortCode("IDEA Plugin");
            } catch (ColtRemoteTransferableException e) {
                Messages.showErrorDialog("Can't request an authorization key from COLT.\nMake sure COLT is active and running", AsGenericColtRemoteAction.COLT_TITLE);
                return false;
            }
        }

        String shortCode = Messages.showInputDialog("Enter the short key displayed in COLT", AsGenericColtRemoteAction.COLT_TITLE, Messages.getQuestionIcon());
        if (StringUtils.isNotEmpty(shortCode)) {
            String token;
            try {
                token = coltRemoteService.obtainAuthToken(shortCode);
            } catch (TooManyFailedCodeTypeAttemptsException e) {
                Messages.showErrorDialog("Too many failed code input attempts, try again later", AsGenericColtRemoteAction.COLT_TITLE);
                return false;
            } catch (InvalidShortCodeException e) {
                int result = Messages.showDialog("Invalid short code entered", AsGenericColtRemoteAction.COLT_TITLE, new String[] {
                        "Try again", "Cancel"
                }, 0, Messages.getWarningIcon());

                if (result == 0) {
                    return makeNewSecurityToken(false);
                }

                return false;
            }

            ColtSettings.getInstance().setSecurityToken(token);
            Messages.showInfoMessage("Successfully connected to COLT", AsGenericColtRemoteAction.COLT_TITLE);

            return true;
        } else {
            int result = Messages.showDialog("Empty short code entered", AsGenericColtRemoteAction.COLT_TITLE, new String[] {
                    "Try again", "Cancel"
            }, 0, Messages.getWarningIcon());

            if (result == 0) {
                return makeNewSecurityToken(false);
            }
        }

        return false;
    }

    @Override
    public void initComponent() {
        connectionFinisherThread.start();
    }

    @Override
    public void disposeComponent() {
        connectionFinisherThread.stopRightTHere();
        listeners.clear();
    }

    private class ConnectionFinisherThread extends Thread {

        private boolean mustStop;

        public void stopRightTHere() {
            mustStop = true;
        }

        @Override
        public void run() {
            mustStop = false;
            while (!mustStop) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // ignore
                }

                if (coltRemoteService != null) {
                    try {
                        coltRemoteService.ping();
                    } catch (Throwable t) {
                        setColtRemoteService(null);
                    }
                }
            }
        }
    }

}
