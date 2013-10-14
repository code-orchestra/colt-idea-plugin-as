package codeOrchestra.colt.as.plugin.actions;

import codeOrchestra.colt.as.plugin.controller.AsColtPluginController;

/**
 * @author Alexander Eliseyev
 */
public class ColtLiveBaseCompileAction extends ColtAbstractCompileAction {

    public ColtLiveBaseCompileAction() {
        super("Live Build");
    }

    @Override
    protected AsColtPluginController.CompilationAction getCompilationAction() {
        return AsColtPluginController.BASE_LIVE;
    }
}
