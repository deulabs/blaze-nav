package org.jabby.blazenav;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Painter;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.IdeGlassPaneUtil;
import com.intellij.openapi.wm.impl.status.StatusBarUtil;

import javax.swing.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

abstract class ActionBase extends DumbAwareAction {
    static final char[] SUBSTITUTIONS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
    static final char[] NO_SHIFT_DOWN_CHARS = "-=[];'\\,./".toCharArray();
    static final Set<Character> NO_SHIFT_DOWN_CHARS_SET = new HashSet<Character>(NO_SHIFT_DOWN_CHARS.length);

    static { for (char c : NO_SHIFT_DOWN_CHARS) NO_SHIFT_DOWN_CHARS_SET.add(c); }

    KeyStroke createKeyStroke(Character c) {
        return NO_SHIFT_DOWN_CHARS_SET.contains(c) || Character.isLowerCase(c) || Character.isDigit(c)
                ? KeyStroke.getKeyStroke(c)
                : KeyStroke.getKeyStroke(c, InputEvent.SHIFT_DOWN_MASK);
    }

    void setStatusText(Editor editor, String key, Object... args) {
        Project project = editor.getProject();
        if (project != null) StatusBarUtil.setStatusBarInfo(project, BlazeNavBundle.message(key, (Object[]) args));
    }

    void clearStatusText(Editor editor) {
        Project project = editor.getProject();
        if (project != null) StatusBarUtil.setStatusBarInfo(project, "");
    }

    Runnable createClearStatusFn(final Editor editor) {
        return new RunnableOnce() {
            @Override protected void runOnce() { clearStatusText(editor); }
        };
    }

    Runnable createUnregisterShortcutsFn(final JComponent editorComponent, final List<AnAction> nextActions) {
        return new RunnableOnce() {
            @Override public void runOnce() {
                for (AnAction next : nextActions) { next.unregisterCustomShortcutSet(editorComponent); }
            }
        };
    }

    void setupCancellation(JComponent component, final Runnable... fns) {
        component.addKeyListener(new KeyAdapter() {
            @Override public void keyTyped(KeyEvent e) {
                runAll(fns);
                e.getComponent().removeKeyListener(this);
            }
        });
        component.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                runAll(fns);
                e.getComponent().removeFocusListener(this);
            }
        });
        component.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                runAll(fns);
                e.getComponent().removeMouseListener(this);
            }
        });
    }

    Runnable highlightChars(Editor editor, Map<Character, Integer> mappings) {
        final Painter p = new HighlightPainter(editor, mappings);
        final JComponent editorComponent = editor.getContentComponent();
        IdeGlassPaneUtil.find(editorComponent).addPainter(editorComponent, p, Disposer.newDisposable());
        return new RunnableOnce() {
            @Override protected void runOnce() {
                IdeGlassPaneUtil.find(editorComponent).removePainter(p);
            }
        };
    }

    void runAll(Runnable... fns) { for (Runnable fn : fns) fn.run(); }

    void moveCaretTo(Editor editor, int offset) { editor.getCaretModel().moveToOffset(offset); }
}
