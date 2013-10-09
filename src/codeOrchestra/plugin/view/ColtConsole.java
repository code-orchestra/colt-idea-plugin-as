package codeOrchestra.plugin.view;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;

import javax.swing.*;

/**
 * @author Alexander Eliseyev
 */
public class ColtConsole extends AbstractProjectComponent implements ProjectComponent {

    public static final String TOOL_WINDOW_NAME = "Colt";

    public static ColtConsole getInstance(Project project) {
        return project.getComponent(ColtConsole.class);
    }

    private ToolWindow toolWindow;
    private ConsoleView consoleView;

    public ColtConsole(Project project) {
        super(project);
    }

    public void append(final String str, final ConsoleViewContentType consoleViewContentType) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                toolWindow.activate(null);
                consoleView.print(str + "\n", consoleViewContentType);
            }
        });
    }

    public void clear() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                consoleView.clear();
            }
        });
    }

    @Override
    public void projectOpened() {
        JComponent toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, new DefaultActionGroup(), false).getComponent();
        ToolWindowManager.getInstance(myProject).registerToolWindow(TOOL_WINDOW_NAME, toolbar, ToolWindowAnchor.BOTTOM);

        toolWindow = ToolWindowManager.getInstance(myProject).getToolWindow(TOOL_WINDOW_NAME);

        consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(myProject).getConsole();
        Content content = toolWindow.getContentManager().getFactory().createContent(consoleView.getComponent(), "Messages", true);
        toolWindow.getContentManager().addContent(content);
        toolWindow.getContentManager().removeContent(toolWindow.getContentManager().getContent(0), true);
    }

    @Override
    public void projectClosed() {
        consoleView.dispose();
        ToolWindowManager.getInstance(myProject).unregisterToolWindow(TOOL_WINDOW_NAME);
    }
}
