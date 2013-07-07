package org.jabby.blazenav;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.ui.AbstractPainter;
import com.intellij.ui.JBColor;

import java.awt.*;
import java.util.Map;

class HighlightPainter extends AbstractPainter {
    final Editor editor;
    final Map<Character, Integer> mappings;

    public HighlightPainter(Editor editor, Map<Character, Integer> mappings) {
        this.editor = editor;
        this.mappings = mappings;
    }

    @Override public void executePaint(Component component, Graphics2D g) {
        EditorColorsScheme cs = editor.getColorsScheme();
        Color bgColor = cs.getDefaultBackground();
        Color currentLineColor = cs.getColor(EditorColors.CARET_ROW_COLOR);

        Font font = cs.getFont(EditorFontType.BOLD);
        int lineHeight = editor.getLineHeight();

        int currentLine = editor.getCaretModel().getLogicalPosition().line;

        for (Map.Entry<Character, Integer> entry : mappings.entrySet()) {
            String c = entry.getKey().toString();
            Integer offset = entry.getValue();
            LogicalPosition pos = editor.offsetToLogicalPosition(offset);
            Point point = editor.logicalPositionToXY(pos);

            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            int height = fm.getHeight();

            g.setColor(pos.line == currentLine ? currentLineColor : bgColor);
            g.fillRect(point.x, point.y, fm.stringWidth(c), applyLineSpacing(height, lineHeight, height));
            g.setColor(JBColor.RED);
            g.drawString(c, point.x, point.y + applyLineSpacing(fm.getLeading() + fm.getAscent(), lineHeight, height));
        }
    }

    int applyLineSpacing(int y, int lineHeight, int fontHeight) { return y + (lineHeight - fontHeight); }

    @Override public boolean needsRepaint() { return true; }
}
