package org.jabby.blazenav

import java.lang.ref.Reference
import java.util.ResourceBundle
import java.lang.ref.SoftReference
import com.intellij.CommonBundle

object BlazeNavBundle {
    private val BUNDLE = "org.jabby.blazenav.BlazeNavBundle"
    private var ourBundle: Reference<ResourceBundle>? = null

    public fun message(key: String, vararg params: Any?): String {
        return CommonBundle.message(getBundle(), key, *params)
    }

    private fun getBundle(): ResourceBundle {
        var bundle = ourBundle?.get()
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(BUNDLE)
            ourBundle = SoftReference(bundle)
        }
        return bundle!!
    }
}
