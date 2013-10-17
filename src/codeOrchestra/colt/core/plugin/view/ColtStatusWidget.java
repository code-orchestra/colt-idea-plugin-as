package codeOrchestra.colt.core.plugin.view;

import codeOrchestra.colt.as.plugin.controller.AsColtPluginController;
import codeOrchestra.colt.as.rpc.ColtAsRemoteService;
import codeOrchestra.colt.as.rpc.model.ColtCompilerMessage;
import codeOrchestra.colt.core.plugin.icons.Icons;
import codeOrchestra.colt.core.rpc.ColtRemoteServiceListener;
import codeOrchestra.colt.core.rpc.ColtRemoteServiceProvider;
import codeOrchestra.colt.core.rpc.model.ColtState;
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

/**
 * @author Alexander Eliseyev
 */
public class ColtStatusWidget extends JButton implements CustomStatusBarWidget, ColtRemoteServiceListener {

    public static final String ID = "COLTStatus";

    private Project project;

    public ColtStatusWidget(final Project project, final ColtRemoteServiceProvider remoteServiceProvider) {
        this.project = project;

        setOpaque(false);
        setFocusable(false);

        setBorder(StatusBarWidget.WidgetBorder.INSTANCE);

        updateUI();

        remoteServiceProvider.addListener(this);

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                setIcon(Icons.LIVE_SWITCHING);
                AsColtPluginController.runCompilationAction((ColtAsRemoteService) remoteServiceProvider.getService(), ColtStatusWidget.this.project, AsColtPluginController.BASE_LIVE, null);
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
                setIcon(Icons.LIVE_SWITCHING);
                setEnabled(true);
            }
        });
    }

    @Override
    public void onStateUpdate(final ColtState state) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (state.getActiveConnections() != null && state.getActiveConnections().length > 0) {
                    setIcon(Icons.LIVE_ON);
                    setEnabled(true);
                } else {
                    setIcon(Icons.LIVE_SWITCHING);
                    setEnabled(true);
                }
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
