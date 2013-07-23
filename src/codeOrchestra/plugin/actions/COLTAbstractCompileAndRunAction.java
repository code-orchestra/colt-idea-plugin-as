package codeOrchestra.plugin.actions;

import codeOrchestra.lcs.rpc.COLTRemoteTransferableException;
import codeOrchestra.lcs.rpc.model.COLTCompilationResult;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Eliseyev
 */
public abstract class COLTAbstractCompileAndRunAction extends COLTAbstractCompileAction {

    public COLTAbstractCompileAndRunAction(@Nullable String text) {
        super(text);
    }

    @Override
    protected final COLTCompilationResult doRunCompilation() throws COLTRemoteTransferableException {
        COLTCompilationResult coltCompilationResult = doRunCompilationWithoutRun();

        // TODO: execute run configuration

        return coltCompilationResult;
    }

    protected abstract COLTCompilationResult doRunCompilationWithoutRun() throws COLTRemoteTransferableException;

}
