package org.jabby.blazenav;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;

class MoveCaretToOffsetAction extends ActionBase {
    final int offset;
    final Runnable[] cleanupFns;

    MoveCaretToOffsetAction(int offset, Runnable... cleanupFns) {
        this.offset = offset;
        this.cleanupFns = cleanupFns;
    }

    @Override public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(PlatformDataKeys.EDITOR);

        runAll(cleanupFns);

        if (editor != null) {
            moveCaretTo(editor, offset);
            clearStatusText(editor);
        }
    }
}
