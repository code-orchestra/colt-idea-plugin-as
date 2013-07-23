package codeOrchestra.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Alexander Eliseyev
 */
public final class EventUtils {

    public static AnActionEvent cloneEvent(AnActionEvent event) {
        Presentation presentation = event.getPresentation().clone();
        InputEvent inputEvent = new KeyEvent(event.getInputEvent().getComponent(), 0, System.currentTimeMillis(), 0, KeyEvent.VK_0, '0');
        return new AnActionEvent(inputEvent, event.getDataContext(), event.getPlace(), presentation, event.getActionManager(), 0);
    }

}
