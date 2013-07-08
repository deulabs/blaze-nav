package org.jabby.blazenav

import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.editor.Editor
import javax.swing.KeyStroke
import java.awt.event.InputEvent
import com.intellij.openapi.wm.impl.status.StatusBarUtil
import javax.swing.JComponent
import com.intellij.openapi.actionSystem.AnAction
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import com.intellij.openapi.wm.IdeGlassPaneUtil
import com.intellij.openapi.util.Disposer

abstract class ActionBase: DumbAwareAction() {
    protected fun createKeyStroke(c: Char): KeyStroke {
        val ks =
                if (NO_SHIFT_DOWN_CHARS.contains(c) || Character.isLowerCase(c) || Character.isDigit(c))
                    KeyStroke.getKeyStroke(c)
                else
                    KeyStroke.getKeyStroke(c, InputEvent.SHIFT_DOWN_MASK)
        return ks!!
    }

    protected fun setStatusText(editor: Editor, key: String, vararg args: Any?) {
        val project = editor.getProject()
        if (project != null) StatusBarUtil.setStatusBarInfo(project, BlazeNavBundle.message(key, *args))
    }

    protected fun clearStatusText(editor: Editor) {
        val project = editor.getProject()
        if (project != null) StatusBarUtil.setStatusBarInfo(project, "")
    }

    protected fun createClearStatusFn(editor: Editor): () -> Unit = { clearStatusText(editor) }

    protected fun createUnRegisterShortcutsFn(editorComponent: JComponent, actions: List<AnAction>): () -> Unit {
        return { actions.forEach { it.unregisterCustomShortcutSet(editorComponent) } }
    }

    protected fun setupCancellation(component: JComponent, vararg fns: () -> Unit) {
        component.addKeyListener(object : KeyAdapter() {
            public override fun keyTyped(p0: KeyEvent) {
                runAll(*fns); component.removeKeyListener(this)
            }
        })
        component.addFocusListener(object : FocusAdapter() {
            public override fun focusLost(p0: FocusEvent) {
                runAll(*fns); component.removeFocusListener(this)
            }
        })
        component.addMouseListener(object : MouseAdapter() {
            public override fun mouseClicked(p0: MouseEvent) {
                runAll(*fns); component.removeMouseListener(this)
            }
        })
    }

    protected fun runAll(vararg fns: () -> Unit): Unit = fns.forEach { it() }

    protected fun highlightChars(editor: Editor, keyToOffsetsMappings: Map<Char, Int>): () -> Unit {
        val painter = HighlightsPainter(editor, keyToOffsetsMappings)
        val editorComponent = editor.getContentComponent()
        IdeGlassPaneUtil.find(editorComponent)!!.addPainter(editorComponent, painter, Disposer.newDisposable())
        return { IdeGlassPaneUtil.find(editorComponent)!!.removePainter(painter) }
    }

    protected fun moveCaretTo(editor: Editor, offset: Int): Unit = editor.getCaretModel().moveToOffset(offset)
}
