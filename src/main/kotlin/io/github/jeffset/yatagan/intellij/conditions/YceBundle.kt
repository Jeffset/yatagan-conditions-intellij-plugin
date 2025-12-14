package io.github.jeffset.yatagan.intellij.conditions

import com.intellij.DynamicBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey

@NonNls
private const val BUNDLE = "messages.YceBundle"

internal object YceBundle {
    private val instance = DynamicBundle(YceBundle::class.java, BUNDLE)

    @Nls
    fun message(
        @PropertyKey(resourceBundle = BUNDLE) key: String,
        vararg params: Any
    ): String {
        return instance.getMessage(key, *params)
    }
}