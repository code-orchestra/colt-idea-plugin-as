package codeOrchestra.colt.core.rpc.model;

/**
 * @author Alexander Eliseyev
 */
public class ColtState {

    private boolean projectLoaded;
    private String projectName;
    private String handlerId;

    public boolean isProjectLoaded() {
        return projectLoaded;
    }

    public void setProjectLoaded(boolean projectLoaded) {
        this.projectLoaded = projectLoaded;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(String handlerId) {
        this.handlerId = handlerId;
    }
}
