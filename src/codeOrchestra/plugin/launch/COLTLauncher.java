package codeOrchestra.plugin.launch;

import codeOrchestra.plugin.COLTSettings;
import com.intellij.execution.ExecutionException;
import com.intellij.openapi.util.SystemInfo;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * @author Alexander Eliseyev
 */
public final class COLTLauncher {

    public static Process launch() throws COLTPathNotConfiguredException, ExecutionException, IOException {
        if (!COLTSettings.getInstance().isCOLTPathValid()) {
            throw new COLTPathNotConfiguredException();
        }

        String coltExecutablePath = completeCOLTPath(completeCOLTPath(COLTSettings.getInstance().getCOLTPath()));
        return new ProcessBuilder(coltExecutablePath).start();
    }

    private static String completeCOLTPath(String coltPath) throws ExecutionException {
        File coltFile = new File(coltPath);
        if (!coltFile.exists()) {
            throw new ExecutionException("Can't locate COLT under " + coltPath);
        }

        String result = coltPath;
        if (SystemInfo.isMac) {
            if (coltFile.isDirectory()) {
                File executableDir = new File(coltFile, "COLT.app/Contents/MacOS");
                if (!(executableDir.exists())) {
                    throw new ExecutionException("Can't locate COLT under " + coltPath);
                }

                File[] files = executableDir.listFiles(new FilenameFilter() {
                    public boolean accept(File file, String fileName) {
                        return fileName.toLowerCase().contains("eclipse");
                    }
                });
                if (files == null || files.length == 0) {
                    throw new ExecutionException("Can't locate COLT under " + coltPath);
                }

                result = files[0].getPath();
            }
        } else if (SystemInfo.isWindows) {
            result = result + "\\" + "COLT.exe";
        } else {
            // Do nothing 
        }

        return protect(result);
    }

    public static String protect(String result) {
        if (result.contains(" ")) {
            return "\"" + result + "\"";
        }
        return result;
    }

}
