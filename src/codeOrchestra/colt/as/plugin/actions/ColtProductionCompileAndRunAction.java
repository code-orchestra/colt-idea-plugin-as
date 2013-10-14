package codeOrchestra.colt.as.plugin.actions;

import codeOrchestra.colt.as.plugin.controller.AsColtPluginController;

/**
 * @author Alexander Eliseyev
 */
public class ColtProductionCompileAndRunAction extends ColtAbstractCompileAction {

    public ColtProductionCompileAndRunAction() {
        super("Production Build and Exec Run");
    }

    @Override
    protected AsColtPluginController.CompilationAction getCompilationAction() {
        return AsColtPluginController.PRODUCTION_AND_RUN;
    }
}
