package codeOrchestra.plugin.launch;

import codeOrchestra.plugin.COLTSettings;

/**
 * @author Alexander Eliseyev
 */
public final class COLTLauncher {

    public static void launch() throws COLTPathNotConfiguredException {
        if (!COLTSettings.getInstance().isCOLTPathValid()) {
            throw new COLTPathNotConfiguredException();
        }


    }

}
