package org.jabby.blazenav;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.util.Range;
import com.intellij.util.containers.MultiMap;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class NavToOffsetAction extends NavActionBase {
    @Override void actionPerformed(PsiFile psiFile, Editor editor) {
        Runnable removeHighlightMaskFn = disableHighlighting(editor, psiFile.getTextLength());
        Runnable restoreFoldsFn = unfold(editor);

        Range<Integer> range = getVisibleLinesRange(editor);
        MultiMap<Character, Integer> charOffsets = getCharsOffsets(psiFile.getText(), range.getFrom(), range.getTo());
        Set<Character> chars = charOffsets.keySet();

        List<AnAction> nextActions = new ArrayList<AnAction>(chars.size());
        JComponent editorComponent = editor.getContentComponent();
        Runnable unregisterShortcutsFn = createUnregisterShortcutsFn(editorComponent, nextActions);

        for (Character c : chars) {
            ChooseLetterAction next =
                    new ChooseLetterAction(c, charOffsets.get(c),
                            unregisterShortcutsFn, removeHighlightMaskFn, restoreFoldsFn);
            next.registerCustomShortcutSet(new CustomShortcutSet(createKeyStroke(c)), editorComponent);
            nextActions.add(next);
        }

        setStatusText(editor, getStatusPromptKey(), getCandidates(chars));
        setupCancellation(
                editorComponent,
                unregisterShortcutsFn, removeHighlightMaskFn, restoreFoldsFn, createClearStatusFn(editor));
    }

    abstract String getStatusPromptKey();

    MultiMap<Character, Integer> getCharsOffsets(String text, int fromLine, int toLine) {
        int fromOffset = getFromOffset(text, fromLine);
        int toOffset = getToOffset(text, toLine);

        Pattern pattern = Pattern.compile(getPattern());
        Matcher matcher = pattern.matcher(text.substring(fromOffset, toOffset));

        MultiMap<Character, Integer> mmap = MultiMap.create();
        while (matcher.find()) {
            mmap.putValue(
                    Character.toLowerCase(matcher.group().charAt(0)), matcher.start() + fromOffset);
        }
        return mmap;
    }

    abstract String getPattern();

    String getCandidates(Set<Character> chars) {
        ArrayList<Character> cs = Lists.newArrayList(chars);
        Collections.sort(cs);
        return Joiner.on("").join(cs);
    }
}
