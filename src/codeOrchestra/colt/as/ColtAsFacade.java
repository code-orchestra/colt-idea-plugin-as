package codeOrchestra.colt.as;

import codeOrchestra.colt.as.plugin.controller.AsColtPluginController;
import codeOrchestra.colt.as.rpc.ColtAsRemoteService;
import codeOrchestra.colt.core.ColtFacade;
import codeOrchestra.colt.core.rpc.ColtRemoteServiceProvider;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;

/**
 * @author Alexander Eliseyev
 */
public class ColtAsFacade extends AbstractProjectComponent implements ColtFacade, ProjectComponent {

    public ColtAsFacade(Project project) {
        super(project);
    }

    @Override
    public void startLive() {

        AsColtPluginController.runCompilationAction((ColtAsRemoteService) myProject.getComponent(ColtRemoteServiceProvider.class).getService(), myProject, AsColtPluginController.BASE_LIVE, null);
    }

}
