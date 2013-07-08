package org.jabby.blazenav

public class BlazeNavToWordAction: NavToOffsetAction() {
    protected override fun getStatusPromptKey(): String = "blaze.nav.to.word"
    protected override fun getPattern(): String = "\\w+"
}
