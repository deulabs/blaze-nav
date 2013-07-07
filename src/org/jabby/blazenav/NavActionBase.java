package org.jabby.blazenav;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiFile;
import com.intellij.ui.JBColor;
import com.intellij.util.Range;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

abstract class NavActionBase extends ActionBase {
    @Override public final void update(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        e.getPresentation().setEnabled(psiFile != null && editor != null);
    }

    @Override public final void actionPerformed(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);

        if (psiFile != null && editor != null) actionPerformed(psiFile, editor);
    }

    abstract void actionPerformed(PsiFile psiFile, Editor editor);

    Runnable unfold(final Editor editor) {
        FoldRegion[] folds = editor.getFoldingModel().getAllFoldRegions();
        final List<FoldRegion> collapsed = new ArrayList<FoldRegion>(folds.length);
        for (FoldRegion fold : folds) { if (!fold.isExpanded()) collapsed.add(fold); }

        editor.getFoldingModel().runBatchFoldingOperation(new Runnable() {
            @Override public void run() {
                for (FoldRegion fold : collapsed) fold.setExpanded(true);
            }
        });

        return new RunnableOnce() {
            @Override protected void runOnce() {
                editor.getFoldingModel().runBatchFoldingOperation(new Runnable() {
                    @Override public void run() {
                        for (FoldRegion fold : collapsed) fold.setExpanded(false);
                    }
                });
            }
        };
    }

    Runnable disableHighlighting(final Editor editor, int textLength) {
        TextAttributes ta = new TextAttributes();
        ta.setForegroundColor(JBColor.GRAY);
        ta.setEffectColor(JBColor.GRAY);
        ta.setErrorStripeColor(null);
        ta.setEffectType(null);

        final RangeHighlighter highlighter = highlight(editor, 0, textLength, ta);

        return new RunnableOnce() {
            @Override protected void runOnce() { editor.getMarkupModel().removeHighlighter(highlighter); }
        };
    }

    private RangeHighlighter highlight(Editor editor, int startOffset, int endOffset, TextAttributes ta) {
        return editor.getMarkupModel()
                .addRangeHighlighter(
                        startOffset, endOffset, HighlighterLayer.SELECTION, ta, HighlighterTargetArea.EXACT_RANGE);
    }

    Range<Integer> getVisibleLinesRange(Editor editor) {
        Rectangle visibleArea = editor.getScrollingModel().getVisibleArea();
        int lineHeight = editor.getLineHeight();

        int fromLine = getLineNo(visibleArea.y, lineHeight) + 1;
        int toLine = getLineNo(visibleArea.y + visibleArea.height, lineHeight) - 1;

        return new Range<Integer>(fromLine, toLine);
    }

    private int getLineNo(int topOffset, int lineHeight) {
        int ratio = topOffset / lineHeight;
        return topOffset % lineHeight == 0 ? ratio : ratio + 1;
    }

    int getFromOffset(String text, int fromLine) {
        return fromLine > 1 ? StringUtils.ordinalIndexOf(text, "\n", fromLine - 1) + 1 : 0;
    }

    int getToOffset(String text, int toLine) {
        int toOffset = StringUtils.ordinalIndexOf(text, "\n", toLine);
        if (toOffset == -1) toOffset = text.length();
        return toOffset;
    }
}
