package codeOrchestra.colt.core.plugin.run;

import codeOrchestra.colt.core.rpc.ColtRemoteServiceListener;
import codeOrchestra.colt.core.rpc.ColtRemoteServiceProvider;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;

/**
 * @author Alexander Eliseyev
 */
public class ColtRemoteProcessHandler extends ProcessHandler implements ColtRemoteServiceListener {

    private final ColtRemoteServiceProvider remoteServiceProvider;

    private Project project;

    public ColtRemoteProcessHandler(Project project) {
        this.project = project;

        remoteServiceProvider = project.getComponent(ColtRemoteServiceProvider.class);
        remoteServiceProvider.addListener(this);
    }

    @Override
    protected void destroyProcessImpl() {
        doDetach();
    }

    @Override
    protected void detachProcessImpl() {
        doDetach();
    }

    private void doDetach() {
        remoteServiceProvider.disconnect();
    }

    @Override
    public boolean detachIsDefault() {
        return true;
    }

    @Nullable
    @Override
    public OutputStream getProcessInput() {
        return null;
    }

    @Override
    public void onConnected() {
    }

    @Override
    public void onDisconnected() {
        notifyProcessDetached();
    }
}
