package codeOrchestra.colt.core.plugin.view;

import codeOrchestra.colt.as.rpc.model.ColtCompilerMessage;
import codeOrchestra.colt.core.plugin.icons.Icons;
import codeOrchestra.colt.core.rpc.ColtRemoteServiceListener;
import codeOrchestra.colt.core.rpc.ColtRemoteServiceProvider;
import codeOrchestra.utils.EventUtils;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.wm.CustomStatusBarWidget;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

/**
 * @author Alexander Eliseyev
 */
public class ColtStatusWidget extends JButton implements CustomStatusBarWidget, ColtRemoteServiceListener {

    public static final String ID = "COLTStatus";

    private Project project;

    public ColtStatusWidget(Project project, ColtRemoteServiceProvider remoteServiceProvider) {
        this.project = project;

        setOpaque(false);
        setFocusable(false);

        setBorder(StatusBarWidget.WidgetBorder.INSTANCE);

        updateUI();

        remoteServiceProvider.addListener(this);

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (!(actionListener instanceof InputEvent)) {
                    return;
                }

                setIcon(Icons.LIVE_SWITCHING);

                AnAction liveRunAction = ActionManager.getInstance().getAction("RunClass");
                AnActionEvent actionEvent = AnActionEvent.createFromInputEvent(liveRunAction, EventUtils.createFakeInputEvent(event), "");
                liveRunAction.update(actionEvent);
                liveRunAction.actionPerformed(actionEvent);
            }
        });

        // Initial state
        onDisconnected();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setFont(SystemInfo.isMac ? UIUtil.getLabelFont().deriveFont(11.0f) : UIUtil.getLabelFont());
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @NotNull
    @Override
    public String ID() {
        return ID;
    }

    @Nullable
    @Override
    public WidgetPresentation getPresentation(@NotNull PlatformType platformType) {
        return null;
    }

    @Override
    public void install(@NotNull StatusBar statusBar) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void onConnected() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setIcon(Icons.LIVE_ON);
                setEnabled(true);

            }
        });
    }

    @Override
    public void onMessage(ColtCompilerMessage coltCompilerMessage) {
    }

    @Override
    public void onDisconnected() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setIcon(Icons.LIVE_OFF);
                setEnabled(false);
            }
        });
    }

}
