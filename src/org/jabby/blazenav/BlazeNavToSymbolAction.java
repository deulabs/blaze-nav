package org.jabby.blazenav;

public class BlazeNavToSymbolAction extends NavToOffsetAction {
    @Override String getStatusPromptKey() { return "blaze.nav.to.symbol"; }

    @Override String getPattern() { return "\\S"; }
}
