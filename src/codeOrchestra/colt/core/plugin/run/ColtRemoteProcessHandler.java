package codeOrchestra.colt.core.plugin.run;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;

/**
 * @author Alexander Eliseyev
 */
public class ColtRemoteProcessHandler extends ProcessHandler {

    private Project project;

    public ColtRemoteProcessHandler(Project project) {
        this.project = project;
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
        // TODO: detach
        notifyProcessDetached();
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
}
