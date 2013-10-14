package codeOrchestra.colt.as.plugin.actions;

import codeOrchestra.colt.as.plugin.controller.AsColtPluginController;

/**
 * @author Alexander Eliseyev
 */
public class ColtLiveBaseCompileAndRunAction extends ColtAbstractCompileAction {

    public ColtLiveBaseCompileAndRunAction() {
        super("Live Build and Exec Run");
    }

    @Override
    protected AsColtPluginController.CompilationAction getCompilationAction() {
        return AsColtPluginController.BASE_LIVE_AND_RUN;
    }
}
