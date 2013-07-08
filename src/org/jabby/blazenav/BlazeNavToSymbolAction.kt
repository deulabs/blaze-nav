package org.jabby.blazenav

public class BlazeNavToSymbolAction: NavToOffsetAction() {
    protected override fun getStatusPromptKey(): String = "blaze.nav.to.symbol"
    protected override fun getPattern(): String = "\\S"
}
