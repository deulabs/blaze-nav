package org.jabby.blazenav

public class BlazeNavToSymbolAction: NavToOffsetAction() {
    override fun getStatusPromptKey(): String = "blaze.nav.to.symbol"
    override fun getPattern(): String = "\\S"
}
