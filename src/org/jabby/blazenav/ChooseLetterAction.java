package org.jabby.blazenav;

import com.google.common.collect.Lists;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;

import javax.swing.*;
import java.util.*;

class ChooseLetterAction extends ActionBase {
    final Character key;
    final Collection<Integer> offsets;
    final Runnable unregisterShortcutsFn;
    final Runnable[] cleanupFns;

    ChooseLetterAction(Character key, Collection<Integer> offsets,
                       Runnable unregisterShortcutsFn, Runnable... cleanupFns) {
        this.key = key;
        this.offsets = offsets;
        this.unregisterShortcutsFn = unregisterShortcutsFn;
        this.cleanupFns = cleanupFns;
    }

    @Override public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(PlatformDataKeys.EDITOR);

        unregisterShortcutsFn.run();

        if (editor != null) actionPerformed(editor);
    }

    void actionPerformed(Editor editor) {
        Map<Character, Integer> mappings = mapKeysToOffsets(offsets);
        JComponent editorComponent = editor.getContentComponent();

        if (mappings.size() > 1) {
            Runnable unhighlightCharsFn = highlightChars(editor, mappings);

            List<AnAction> nextActions = new ArrayList<AnAction>(mappings.size());
            Runnable unregisterShortcutsFn = createUnregisterShortcutsFn(editorComponent, nextActions);

            for (Character c : mappings.keySet()) {
                MoveCaretToOffsetAction next =
                        new MoveCaretToOffsetAction(mappings.get(c),
                                Utils.mergeVargs(cleanupFns, unregisterShortcutsFn, unhighlightCharsFn));
                next.registerCustomShortcutSet(new CustomShortcutSet(createKeyStroke(c)), editorComponent);
                nextActions.add(next);
            }

            setStatusText(editor, "n.candidates.found", mappings.size());
            setupCancellation(editorComponent,
                    Utils.mergeVargs(cleanupFns,
                            unregisterShortcutsFn, unhighlightCharsFn, createClearStatusFn(editor)));
        } else {
            assert mappings.size() == 1;
            setStatusText(editor, "one.candidate");
            moveCaretTo(editor, mappings.values().iterator().next());
            runAll(Utils.mergeVargs(cleanupFns, unregisterShortcutsFn));
        }
    }

    Map<Character, Integer> mapKeysToOffsets(Collection<Integer> offsets) {
        List<Integer> affectedOffsets =
                Lists.newArrayList(offsets).subList(0, Math.min(offsets.size(), SUBSTITUTIONS.length));
        Collections.sort(affectedOffsets);
        Map<Character, Integer> mappings = new HashMap<Character, Integer>(affectedOffsets.size());
        for (int i = 0; i < affectedOffsets.size(); i++) { mappings.put(SUBSTITUTIONS[i], affectedOffsets.get(i)); }
        return mappings;
    }
}
