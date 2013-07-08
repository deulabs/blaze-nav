package org.jabby.blazenav

import com.intellij.openapi.editor.Editor
import org.jabby.blazenav.NavActionBase.Bounds
import java.util.regex.Pattern
import java.util.LinkedHashMap
import java.util.HashMap
import java.util.ArrayList
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.CustomShortcutSet

public class BlazeNavToLineAction: NavActionBase() {
    protected override fun actionPerformed(editor: Editor) {
        val text = editor.getDocument().getText()!!

        val unFadeFn = fade(editor, text.length())
        val unUnfoldFn = unfold(editor)

        val bounds = getVisibleBounds(editor)
        val lineBeginningsOffsets = getLineBeginningsOffsets(text, bounds)
        val keyToOffsetsMappings = getKeyToOffsetsMappings(lineBeginningsOffsets)

        val unHighlightCharsFn = highlightChars(editor, keyToOffsetsMappings)

        val nextActions: MutableList<AnAction> = ArrayList(lineBeginningsOffsets.size)
        val editorComponent = editor.getContentComponent()
        val unRegisterShortcutsFn = createUnRegisterShortcutsFn(editorComponent, nextActions)

        for ((char, offset) in keyToOffsetsMappings) {
            val next =
                    MoveCaretToOffsetAction(keyToOffsetsMappings.get(char)!!,
                            unFadeFn, unUnfoldFn, unRegisterShortcutsFn, unHighlightCharsFn)
            next.registerCustomShortcutSet(CustomShortcutSet(createKeyStroke(char)), editorComponent)
            nextActions.add(next)
        }

        setupCancellation(
                editorComponent,
                unFadeFn, unUnfoldFn, unRegisterShortcutsFn, unHighlightCharsFn, createClearStatusFn(editor)
        )
    }

    private fun getLineBeginningsOffsets(text: String, bounds: Bounds): Map<Int, Int> {
        val fromOffset = getFromOffset(text, bounds.fromLine)
        val toOffset = getToOffset(text, bounds.toLine)

        val visibleText = text.subSequence(fromOffset, toOffset)
        val matcher = Pattern.compile("\\n").matcher(visibleText)

        val map: MutableMap<Int, Int> = LinkedHashMap(bounds.toLine - bounds.fromLine)
        map.put(bounds.fromLine, fromOffset)

        var i = 1
        while (matcher.find()) map.put(i++ + bounds.fromLine, matcher.start() + fromOffset + 1)
        return map
    }

    private fun getKeyToOffsetsMappings(lineBeginningsOffsets: Map<Int, Int>): Map<Char, Int> {
        val lines =
                lineBeginningsOffsets.keySet().take(
                        Math.min(lineBeginningsOffsets.size, SUBSTITUTIONS.size))
        val mappings: MutableMap<Char, Int> = HashMap(lines.size)
        var i = 0
        lines.forEach { mappings.put(SUBSTITUTIONS[i++], lineBeginningsOffsets.get(it)!!) }
        return mappings
    }
}
