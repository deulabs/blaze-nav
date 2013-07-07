package org.jabby.blazenav;

import com.google.common.collect.Lists;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.util.Range;

import javax.swing.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlazeNavToLineAction extends NavActionBase {
    @Override void actionPerformed(PsiFile psiFile, Editor editor) {
        Runnable removeHighlightMaskFn = disableHighlighting(editor, psiFile.getTextLength());
        Runnable restoreFoldsFn = unfold(editor);

        Range<Integer> range = getVisibleLinesRange(editor);
        Map<Integer, Integer> linesOffsets = getLinesOffsets(psiFile.getText(), range.getFrom(), range.getTo());
        Map<Character, Integer> mappings = mapKeysToOffsets(linesOffsets);

        Runnable unhighlightCharsFn = highlightChars(editor, mappings);

        List<AnAction> nextActions = new ArrayList<AnAction>(linesOffsets.size());
        JComponent editorComponent = editor.getContentComponent();
        Runnable unregisterShortcutsFn = createUnregisterShortcutsFn(editorComponent, nextActions);

        for (Character c : mappings.keySet()) {
            MoveCaretToOffsetAction next =
                    new MoveCaretToOffsetAction(mappings.get(c),
                            removeHighlightMaskFn, restoreFoldsFn, unregisterShortcutsFn, unhighlightCharsFn);
            next.registerCustomShortcutSet(new CustomShortcutSet(createKeyStroke(c)), editorComponent);
            nextActions.add(next);
        }

        setupCancellation(
                editorComponent,
                unregisterShortcutsFn, removeHighlightMaskFn, restoreFoldsFn,
                unhighlightCharsFn, createClearStatusFn(editor));
    }

    Map<Integer, Integer> getLinesOffsets(String text, int fromLine, int toLine) {
        int fromOffset = getFromOffset(text, fromLine);
        int toOffset = getToOffset(text, toLine);

        Pattern pattern = Pattern.compile("\\n");
        Matcher matcher = pattern.matcher(text.substring(fromOffset, toOffset));

        Map<Integer, Integer> map = new HashMap<Integer, Integer>(toLine - fromLine);
        map.put(fromLine, fromOffset);
        int i = 1;
        while (matcher.find()) {
            map.put(i++ + fromLine, matcher.start() + fromOffset + 1);
        }
        return map;
    }

    Map<Character, Integer> mapKeysToOffsets(Map<Integer, Integer> linesOffsets) {
        List<Integer> affectedLines =
                Lists.newArrayList(linesOffsets.keySet())
                        .subList(0, Math.min(linesOffsets.size(), SUBSTITUTIONS.length));
        Collections.sort(affectedLines);
        Map<Character, Integer> mappings = new HashMap<Character, Integer>(affectedLines.size());
        int i = 0;
        for (int line : affectedLines) { mappings.put(SUBSTITUTIONS[i++], linesOffsets.get(line)); }
        return mappings;
    }
}
