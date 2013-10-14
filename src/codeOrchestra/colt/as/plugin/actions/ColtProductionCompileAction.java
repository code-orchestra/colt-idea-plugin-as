package codeOrchestra.colt.as.plugin.actions;

import codeOrchestra.colt.as.plugin.controller.AsColtPluginController;

/**
 * @author Alexander Eliseyev
 */
public class ColtProductionCompileAction extends ColtAbstractCompileAction {

    public ColtProductionCompileAction() {
        super("Production Build");
    }

    @Override
    protected AsColtPluginController.CompilationAction getCompilationAction() {
        return AsColtPluginController.PRODUCTION;
    }
}
