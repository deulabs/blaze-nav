package org.jabby.blazenav

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys

class MoveCaretToOffsetAction(val offset: Int, vararg val cleanupFns: () -> Unit): ActionBase() {
    public override fun actionPerformed(e: AnActionEvent?) {
        cleanupFns.forEach { it() }

        val editor = e!!.getData(PlatformDataKeys.EDITOR)
        if (editor != null) {
            moveCaretTo(editor, offset);
            clearStatusText(editor);
        }
    }
}
