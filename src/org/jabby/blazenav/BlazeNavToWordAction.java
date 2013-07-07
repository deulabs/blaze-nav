package org.jabby.blazenav;

public class BlazeNavToWordAction extends NavToOffsetAction {
    @Override String getStatusPromptKey() { return "blaze.nav.to.word"; }

    @Override String getPattern() { return "\\w+"; }
}
