package codeOrchestra.colt.core.rpc;

import codeOrchestra.colt.core.plugin.launch.ColtLauncher;
import codeOrchestra.colt.core.plugin.launch.ColtPathNotConfiguredException;
import codeOrchestra.colt.core.rpc.discovery.ColtServiceLocator;
import codeOrchestra.colt.core.workset.Workset;
import com.intellij.execution.ExecutionException;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public class ColtRemoteServiceProvider extends AbstractProjectComponent implements ProjectComponent {

    public ColtRemoteServiceProvider(Project project) {
        super(project);
    }

    private ColtRemoteService coltRemoteService;

    private List<ColtRemoteServiceListener> listeners = new ArrayList<ColtRemoteServiceListener>();

    public <S extends ColtRemoteService> void initAndConnect(Class<S> serviceClass, String projectPath, String projectName) throws ColtPathNotConfiguredException, ExecutionException, IOException {
        // 1 - try to connect to existing COLT instance
        ColtServiceLocator serviceLocator = myProject.getComponent(ColtServiceLocator.class);
        S service = serviceLocator.locateService(serviceClass, projectPath, projectName);
        if (service != null) {
            setColtRemoteService(service);
            return;
        }

        // 2 - if it fails, start the COLT
        Workset.addProjectPath(projectPath, true);
        ColtLauncher.launch();

        // 3 - and connect to it
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
        coltRemoteService = null;
    }

    public <S extends ColtRemoteService> S getService() {
        return (S) coltRemoteService;
    }

    @Override
    public void disposeComponent() {
        listeners.clear();
    }
}
