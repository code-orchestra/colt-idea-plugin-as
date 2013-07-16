package codeOrchestra.plugin;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.*;
import org.jetbrains.annotations.Nullable;

/**
 * Created with IntelliJ IDEA.
 * User: dimakruk
 * Date: 7/15/13
 * Time: 12:37 PM
 * To change this template use File | Settings | File Templates.
 */
@State(
    name = "COLTSettings",
    storages = {@Storage(
        file = StoragePathMacros.APP_CONFIG + "/colt_settings.xml")}
)
public class COLTSettings implements PersistentStateComponent<COLTSettings.State> {

    private State myState = new State();

    @Nullable
    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(State state) {
        myState = state;
    }

    public static class State {
        public String SECURITY_TOKEN = "";
    }

    public static COLTSettings getInstance() {
        return ServiceManager.getService(COLTSettings.class);
    }

    public String getSecurityToken() {
        return myState.SECURITY_TOKEN;
    }

    public void setSecurityToken(String token) {
        myState.SECURITY_TOKEN = token;
    }
}
