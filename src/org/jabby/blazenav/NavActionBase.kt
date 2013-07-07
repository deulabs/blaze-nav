package org.jabby.blazenav

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.ui.JBColor
import java.awt.Font
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import org.apache.commons.lang.StringUtils

abstract class NavActionBase: ActionBase() {
    data class Bounds(val fromLine: Int, val toLine: Int)

    protected abstract fun actionPerformed(editor: Editor)

    public override final fun update(e: AnActionEvent?) {
        val editor = e!!.getData(PlatformDataKeys.EDITOR)
        e.getPresentation().setEnabled(editor != null)
    }

    public override final fun actionPerformed(e: AnActionEvent?) {
        val editor = e!!.getData(PlatformDataKeys.EDITOR)
        if (editor != null) actionPerformed(editor)
    }

    protected fun fade(editor: Editor, textLength: Int): (() -> Unit) {
        val ta = TextAttributes(JBColor.GRAY, null, JBColor.GRAY, null, Font.PLAIN);
        ta.setErrorStripeColor(null);

        val fader = editor.getMarkupModel().addRangeHighlighter(
                0, textLength, HighlighterLayer.SELECTION, ta, HighlighterTargetArea.EXACT_RANGE)

        return {() -> editor.getMarkupModel().removeHighlighter(fader) }
    }

    protected fun unfold(editor: Editor): (() -> Unit) {
        val folds = editor.getFoldingModel().getAllFoldRegions()
        val collapsed = folds.filter { !it.isExpanded() }

        editor.getFoldingModel().runBatchFoldingOperation { collapsed.forEach { it.setExpanded(true) } }

        return {
            () ->
            editor.getFoldingModel().runBatchFoldingOperation { collapsed.forEach { it.setExpanded(false) } }
        }
    }

    protected fun getVisibleBounds(editor: Editor): Bounds {
        val area = editor.getScrollingModel().getVisibleArea()
        val lineHeight = editor.getLineHeight()

        fun getLineNo(topOffset: Int) = Math.ceil((area.y + topOffset) / lineHeight.toDouble()).toInt()

        return Bounds(getLineNo(0) + 1, getLineNo(area.height) - 1)
    }

    protected fun getFromOffset(text: String, fromLine: Int): Int =
            if (fromLine > 1) StringUtils.ordinalIndexOf(text, "\n", fromLine - 1) + 1 else 0

    protected fun getToOffset(text: String, toLine: Int): Int {
        val toOffset = StringUtils.ordinalIndexOf(text, "\n", toLine)
        return if (toOffset == -1) text.length() else toOffset
    }
}
