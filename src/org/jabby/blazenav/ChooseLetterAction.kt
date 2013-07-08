package org.jabby.blazenav

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.editor.Editor
import java.util.HashMap
import java.util.ArrayList
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.CustomShortcutSet

class ChooseLetterAction(val key: Char,
                         val offsets: Collection<Int>,
                         val unRegisterShortcutsFn: () -> Unit,
                         vararg val cleanupFns: () -> Unit): ActionBase() {

    public override fun actionPerformed(e: AnActionEvent?) {
        unRegisterShortcutsFn()

        val editor = e!!.getData(PlatformDataKeys.EDITOR)
        if (editor != null) actionPerformed(editor)
    }

    private fun actionPerformed(editor: Editor) {
        val keyToOffsetsMappings = getKeyToOffsetsMappings()

        if (keyToOffsetsMappings.size > 1) {
            val unHighlightCharsFn = highlightChars(editor, keyToOffsetsMappings)

            val nextActions: MutableList<AnAction> = ArrayList(keyToOffsetsMappings.size)
            val editorComponent = editor.getContentComponent()
            val unRegisterShortcutsFn = createUnRegisterShortcutsFn(editorComponent, nextActions)

            for ((char, offset) in keyToOffsetsMappings) {
                val cleanup = { cleanupFns.forEach { it() }; unRegisterShortcutsFn(); unHighlightCharsFn() }
                val next = MoveCaretToOffsetAction(keyToOffsetsMappings.get(char)!!, cleanup);
                next.registerCustomShortcutSet(CustomShortcutSet(createKeyStroke(char)), editorComponent)
                nextActions.add(next)
            }

            setStatusText(editor, "n.candidates.found", keyToOffsetsMappings.size)
            setupCancellation(
                    editorComponent,
                    *cleanupFns, unRegisterShortcutsFn, unHighlightCharsFn, createClearStatusFn(editor));
        } else {
            assert(keyToOffsetsMappings.size == 1)
            setStatusText(editor, "one.candidate");
            moveCaretTo(editor, keyToOffsetsMappings.values().first());
            runAll(*cleanupFns)
            unRegisterShortcutsFn()
        }
    }

    private fun getKeyToOffsetsMappings(): Map<Char, Int> {
        val affectedOffsets = offsets.take(Math.min(offsets.size, SUBSTITUTIONS.size))
        val mappings: MutableMap<Char, Int> = HashMap(offsets.size)
        var i = 0
        affectedOffsets.forEach { mappings.put(SUBSTITUTIONS[i++], it) }
        return mappings
    }
}
