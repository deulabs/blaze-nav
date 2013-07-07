package org.jabby.blazenav

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.AbstractPainter
import java.awt.Component
import java.awt.Graphics2D
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.ui.JBColor

class HighlightsPainter(val editor: Editor, val mappings: Map<Char, Int>): AbstractPainter() {
    public override fun executePaint(component: Component?, g: Graphics2D?) {
        val cs = editor.getColorsScheme()
        val bgColor = cs.getDefaultBackground()
        val font = cs.getFont(EditorFontType.BOLD)
        val fm = g!!.getFontMetrics(font)
        val lineHeight = editor.getLineHeight()
        val fontHeight = fm!!.getHeight()

        fun applyLineSpacing(y: Int) = y + (lineHeight - fontHeight)

        g.setFont(font)

        for ((char, offset) in mappings) {
            val c = char.toString()
            val pos = editor.offsetToLogicalPosition(offset)
            val xy = editor.logicalPositionToXY(pos)

            g.setColor(bgColor); g.fillRect(xy.x, xy.y, fm.stringWidth(c), applyLineSpacing(fontHeight))
            g.setColor(JBColor.RED); g.drawString(c, xy.x, xy.y + applyLineSpacing(fm.getLeading() + fm.getAscent()))
        }
    }

    public override fun needsRepaint() = true
}
