/*
 * Copyright 2024 Fedor Ihnatkevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.jeffset.yatagan.intellij.conditions

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

class YceColorSettingsPage : ColorSettingsPage {
    override fun getAttributeDescriptors(): Array<AttributesDescriptor> {
        return arrayOf(
            AttributesDescriptor(
                YceBundle.message("settings.yatagan.conditions.color.operators"),
                YceTextAttributeKeys.Operator,
            ),
            AttributesDescriptor(
                YceBundle.message("settings.yatagan.conditions.color.access"),
                YceTextAttributeKeys.AccessOperator,
            ),
            AttributesDescriptor(
                YceBundle.message("settings.yatagan.conditions.color.dot"),
                YceTextAttributeKeys.Dot,
            ),
            AttributesDescriptor(
                YceBundle.message("settings.yatagan.conditions.color.parentheses"),
                YceTextAttributeKeys.Parentheses,
            ),

            AttributesDescriptor(
                YceBundle.message("settings.yatagan.conditions.color.qualifier"),
                YceTextAttributeKeys.ConditionQualifier,
            ),
            AttributesDescriptor(
                YceBundle.message("settings.yatagan.conditions.color.method"),
                YceTextAttributeKeys.PathMemberMethod,
            ),
            AttributesDescriptor(
                YceBundle.message("settings.yatagan.conditions.color.field"),
                YceTextAttributeKeys.PathMemberMethod,
            ),
            AttributesDescriptor(
                YceBundle.message("settings.yatagan.conditions.color.reference"),
                YceTextAttributeKeys.FeatureReference,
            ),
            AttributesDescriptor(
                YceBundle.message("settings.yatagan.conditions.color.unresolved"),
                YceTextAttributeKeys.Unresolved,
            ),

            AttributesDescriptor(
                YceBundle.message("settings.yatagan.conditions.color.bad"),
                YceTextAttributeKeys.BadCharacter,
            ),
        )
    }

    override fun getColorDescriptors(): Array<ColorDescriptor> {
        return ColorDescriptor.EMPTY_ARRAY
    }

    override fun getDisplayName(): String {
        return YceBundle.message("settings.yatagan.conditions.name")
    }

    override fun getIcon(): Icon? {
        return null
    }

    override fun getHighlighter(): SyntaxHighlighter {
        return YceSyntaxHighlighter()
    }

    override fun getDemoText(): String {
        return """
            |(<cp>Features</cp>::<f>MY_FEATURE</f>.<m>isEnabled</m> | !<cp>Helper</cp>::<m>isDebug</m>) & 
            |    !<r>@IsDebug</r> & <u>UnknownType</u>::<u>foo</u>
        """.trimMargin()
    }

    @NonNls
    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey> {
        return mapOf(
            "cp" to YceTextAttributeKeys.ConditionQualifier,
            "r" to YceTextAttributeKeys.FeatureReference,
            "m" to YceTextAttributeKeys.PathMemberMethod,
            "f" to YceTextAttributeKeys.PathMemberField,
            "u" to YceTextAttributeKeys.Unresolved,
        )
    }
}