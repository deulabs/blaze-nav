package org.jabby.blazenav

import com.intellij.openapi.editor.Editor
import com.intellij.util.containers.MultiMap
import java.util.regex.Pattern
import java.util.ArrayList
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.CustomShortcutSet
import java.util.Collections
import com.google.common.base.Joiner

abstract class NavToOffsetAction: NavActionBase() {
    protected abstract fun getStatusPromptKey(): String
    protected abstract fun getPattern(): String

    protected override fun actionPerformed(editor: Editor) {
        val text = editor.getDocument().getText()!!

        val unFadeFn = fade(editor, text.length())
        val unUnfoldFn = unfold(editor)

        val bounds = getVisibleBounds(editor)
        val charsOffsets = getCharsOffsets(text, bounds.fromLine, bounds.toLine)
        val chars = charsOffsets.keySet()!!

        val nextActions: MutableList<AnAction> = ArrayList(chars.size)
        val editorComponent = editor.getContentComponent()
        val unRegisterShortcutsFn = createUnRegisterShortcutsFn(editorComponent, nextActions)

        chars.forEach {
            val next = ChooseLetterAction(it, charsOffsets.get(it), unRegisterShortcutsFn, unFadeFn, unUnfoldFn)
            next.registerCustomShortcutSet(CustomShortcutSet(createKeyStroke(it)), editorComponent)
            nextActions.add(next)
        }

        setStatusText(editor, getStatusPromptKey(), getCandidates(chars))
        setupCancellation(
                editorComponent,
                unRegisterShortcutsFn, unFadeFn, unUnfoldFn, createClearStatusFn(editor)
        )
    }

    private fun getCharsOffsets(text: String, fromLine: Int, toLine: Int): MultiMap<Char, Int> {
        val fromOffset = getFromOffset(text, fromLine)
        val toOffset = getToOffset(text, toLine);

        val visibleText = text.subSequence(fromOffset, toOffset)
        val matcher = Pattern.compile(getPattern()).matcher(visibleText)

        val mmap: MultiMap<Char, Int> = MultiMap.create()
        while (matcher.find())
            mmap.putValue(Character.toLowerCase(matcher.group().charAt(0)), matcher.start() + fromOffset)
        return mmap
    }

    private fun getCandidates(chars: Collection<Char>): String {
        val cs = ArrayList(chars)
        Collections.sort(cs)
        return Joiner.on("")?.join(cs)!!
    }
}
