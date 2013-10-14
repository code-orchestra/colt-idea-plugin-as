package codeOrchestra.colt.core.plugin.run;

import codeOrchestra.colt.as.rpc.model.ColtCompilerMessage;
import codeOrchestra.colt.core.rpc.ColtRemoteServiceListener;
import codeOrchestra.colt.core.rpc.ColtRemoteServiceProvider;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.ui.ConsoleViewContentType;
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
    public void startNotify() {
        super.startNotify();
        notifyTextAvailable("Established a connection with COLT running project " + remoteServiceProvider.getService().getState().getProjectName() + "\n", ProcessOutputTypes.SYSTEM);
    }

    @Override
    public void onMessage(ColtCompilerMessage coltCompilerMessage) {
        notifyTextAvailable(coltCompilerMessage.getFullMessage(), coltCompilerMessage.getType().equals("Error") ? ProcessOutputTypes.STDERR : ProcessOutputTypes.STDOUT);
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
    public void onDisconnected() {
        notifyTextAvailable("\nDisconnected", ProcessOutputTypes.SYSTEM);
        notifyProcessDetached();
    }

    @Override
    public void onConnected() {
    }

}
