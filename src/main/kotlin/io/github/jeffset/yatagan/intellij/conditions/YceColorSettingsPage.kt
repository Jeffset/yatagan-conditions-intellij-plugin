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
import javax.swing.Icon

class YceColorSettingsPage : ColorSettingsPage {
    override fun getAttributeDescriptors(): Array<AttributesDescriptor> {
        return arrayOf(
            AttributesDescriptor("Logical operators",
                YceTextAttributeKeys.Operator
            ),
            AttributesDescriptor("Access operator",
                YceTextAttributeKeys.AccessOperator
            ),
            AttributesDescriptor("Dot",
                YceTextAttributeKeys.Dot
            ),
            AttributesDescriptor("Parenthesis",
                YceTextAttributeKeys.Parentheses
            ),

            AttributesDescriptor("Condition provider",
                YceTextAttributeKeys.ConditionQualifier
            ),
            AttributesDescriptor("Condition path member",
                YceTextAttributeKeys.PathMember
            ),
            AttributesDescriptor("Feature reference",
                YceTextAttributeKeys.FeatureReference
            ),
            AttributesDescriptor("Unresolved",
                YceTextAttributeKeys.Unresolved
            ),

            AttributesDescriptor("Bad character",
                YceTextAttributeKeys.BadCharacter
            ),
        )
    }

    override fun getColorDescriptors(): Array<ColorDescriptor> {
        return ColorDescriptor.EMPTY_ARRAY
    }

    override fun getDisplayName(): String {
        return "Yatagan Conditions"
    }

    override fun getIcon(): Icon? {
        return null
    }

    override fun getHighlighter(): SyntaxHighlighter {
        return YceSyntaxHighlighter()
    }

    override fun getDemoText(): String {
        return """
            |(<cp>Features</cp>::<m>MY_FEATURE</m>.<m>isEnabled</m> | !<cp>Helper</cp>::<m>isDebug</m>) & 
            |    !<f>@IsDebug</f> & <u>UnknownType</u>::<u>foo</u>
        """.trimMargin()
    }

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey> {
        return mapOf(
            "cp" to YceTextAttributeKeys.ConditionQualifier,
            "f" to YceTextAttributeKeys.FeatureReference,
            "m" to YceTextAttributeKeys.PathMember,
            "u" to YceTextAttributeKeys.Unresolved,
        )
    }
}