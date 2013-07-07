package org.jabby.blazenav

public class BlazeNavToWordAction: NavToOffsetAction() {
    override fun getStatusPromptKey(): String = "blaze.nav.to.word"
    override fun getPattern(): String = "\\w+"
}
