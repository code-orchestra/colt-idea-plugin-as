package codeOrchestra.colt.core.rpc;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;

/**
 * @author Alexander Eliseyev
 */
public class ColtRemoteServiceProvider extends AbstractProjectComponent implements ProjectComponent {

    public ColtRemoteServiceProvider(Project project) {
        super(project);
    }

    private ColtRemoteService coltRemoteService;

    public <S extends ColtRemoteService> void initAndConnect(Class<S> serviceClass, String projectPath, String projectName) {
        // 1 - try to connect to existing COLT instance
        // TODO: implement

        // 2 - if it fails, start the COLT
        // TODO: implement

        // 3 - and connect to it
        // TODO: implement
    }


}
