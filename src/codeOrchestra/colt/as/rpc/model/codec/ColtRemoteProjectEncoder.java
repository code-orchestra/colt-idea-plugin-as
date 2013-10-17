package codeOrchestra.colt.as.rpc.model.codec;

import codeOrchestra.colt.as.rpc.model.ColtRemoteProject;
import codeOrchestra.utils.StringUtils;
import codeOrchestra.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

/**
 * @author Alexander Eliseyev
 */
public class ColtRemoteProjectEncoder {

    private Document projectDocument = XMLUtils.createDocument();

    private ColtRemoteProject project;

    public ColtRemoteProjectEncoder(ColtRemoteProject project) {
        this.project = project;
    }

    public Document toDocument() {
        // Root
        Element rootElement = projectDocument.createElement("xml");
        rootElement.setAttribute("projectName", project.getName());
        rootElement.setAttribute("projectType", "AS");
        projectDocument.appendChild(rootElement);

        // Paths
        Element pathsElement = projectDocument.createElement("paths");
        {
            createElement("sources-set", createFileSetFromPathArray(project.getSources()), pathsElement);
            createElement("libraries-set", createFileSetFromPathArray(project.getLibraries()), pathsElement);
            createElement("assets-set", createFileSetFromPathArray(project.getAssets()), pathsElement);
            createElement("html-template", "", pathsElement);
        }
        rootElement.appendChild(pathsElement);

        // Build
        Element buildElement = createElement("build");
        {
            // Sdk
            Element sdkElement = createElement("sdk");
            {
                createElement("sdk-path", safe(project.getFlexSDKPath(), "${colt_home}/flex_sdk"), sdkElement);
                createElement("use-flex", "true", sdkElement);
                createElement("use-custom", String.valueOf(StringUtils.isNotEmpty(project.getCustomConfigPath())), sdkElement);
                createElement("custom-config", safe(project.getCustomConfigPath()), sdkElement);
            }
            buildElement.appendChild(sdkElement);

            // Build
            Element innerBuildElement = createElement("build");
            {
                createElement("main-class", safe(project.getMainClass()), innerBuildElement);
                createElement("output-name", safe(project.getOutputFileName()), innerBuildElement);
                createElement("output-path", safe(project.getOutputPath()), innerBuildElement);
                createElement("use-max-version", "true", innerBuildElement);
                createElement("player-version", safe(project.getTargetPlayerVersion()), innerBuildElement);
                createElement("is-rsl", "false", innerBuildElement);
                createElement("locale", "", innerBuildElement);
                createElement("is-exclude", "false", innerBuildElement);
                createElement("is-interrupt", "false", innerBuildElement);
                createElement("interrupt-value", "30", innerBuildElement);
                createElement("compiler-options", safe(project.getCompilerOptions()), innerBuildElement);
            }
            buildElement.appendChild(innerBuildElement);

            // Production
            Element productionElement = createElement("production");
            {
                createElement("output-path", safe(project.getOutputPath()), productionElement);
                createElement("compress", "false", productionElement);
                createElement("optimize", "false", productionElement);
            }
            buildElement.appendChild(productionElement);

            // Run target
            Element runTargetElement = createElement("run-target");
            {
                createElement("run-target", "SWF", runTargetElement);
            }
            buildElement.appendChild(runTargetElement);
        }
        rootElement.appendChild(buildElement);

        // Live
        Element liveElement = createElement("live");
        {
            // Settings
            Element settingsElement = createElement("settings");
            {
                createElement("clear-log", "false", settingsElement);
                createElement("disconnect", "true", settingsElement);
            }
            liveElement.appendChild(settingsElement);

            // Launch
            Element launchElement = createElement("launch");
            {
                createElement("launcher", "DEFAULT", launchElement);
                createElement("player-path", "", launchElement);
            }
            liveElement.appendChild(launchElement);

            // Live
            Element innerLiveElement = createElement("live");
            {
                createElement("live-type", "annotated", innerLiveElement);
                createElement("paused", "false", innerLiveElement);
                createElement("make-gs-live", "false", innerLiveElement);
                createElement("max-loop", "10000", innerLiveElement);
            }
            liveElement.appendChild(innerLiveElement);
        }
        rootElement.appendChild(liveElement);

        return projectDocument;
    }

    private Element createElement(String name) {
        return createElement(name, null);
    }

    private Element createElement(String name, String textValue) {
        return createElement(name, textValue, null);
    }

    private Element createElement(String name, String textValue, Element parentElement) {
        Element element = projectDocument.createElement(name);
        if (textValue != null) {
            element.setTextContent(textValue);
        }
        if (parentElement != null) {
            parentElement.appendChild(element);
        }
        return element;
    }

    private String safe(String str) {
        return safe(str, "");
    }

    private String safe(String str, String defaultValue) {
        return StringUtils.isEmpty(str) ? defaultValue : str;
    }

    private String createFileSetFromPathArray(String[] paths) {
        if (paths == null || paths.length == 0) {
            return "";
        }

        File baseDir = new File(project.getPath()).getParentFile();

        StringBuilder sb = new StringBuilder();
        Iterator<String> pathIterator = Arrays.asList(paths).iterator();
        while (pathIterator.hasNext()) {
            String path = pathIterator.next();
            File file = new File(path);
            if (file.getPath().startsWith(baseDir.getPath())) {
                String relativePath = file.getPath().replace(baseDir.getPath(), "");
                if (relativePath.startsWith("/") || relativePath.startsWith("\\")) {
                    relativePath = relativePath.substring(1, relativePath.length());
                }

                sb.append(relativePath);
            } else {
                sb.append(file.getPath());
            }

            if (pathIterator.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }


}
