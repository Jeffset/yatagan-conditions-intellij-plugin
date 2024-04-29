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

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey

internal object YceTextAttributeKeys {
    val Operator = createTextAttributesKey("YCE_OPERATOR",
        DefaultLanguageHighlighterColors.OPERATION_SIGN)

    val Dot = createTextAttributesKey("YCE_DOT",
        DefaultLanguageHighlighterColors.DOT)

    val Parentheses = createTextAttributesKey("YCE_PARENTHESES",
        DefaultLanguageHighlighterColors.PARENTHESES)

    val AccessOperator = createTextAttributesKey("YCE_ACCESS_OPERATOR",
        DefaultLanguageHighlighterColors.DOT)

    val Identifier = createTextAttributesKey("YCE_IDENTIFIER",
        DefaultLanguageHighlighterColors.IDENTIFIER)

    val BadCharacter = createTextAttributesKey("YCE_BAD_CHARACTER",
        HighlighterColors.BAD_CHARACTER)

    val ConditionQualifier = createTextAttributesKey("YCE_CONDITION_QUALIFIER",
        DefaultLanguageHighlighterColors.CLASS_NAME)

    val FeatureReference = createTextAttributesKey("YCE_FEATURE_REFERENCE",
        DefaultLanguageHighlighterColors.METADATA)

    val PathMemberMethod = createTextAttributesKey("YCE_PATH_MEMBER_METHOD",
        DefaultLanguageHighlighterColors.FUNCTION_CALL)

    val PathMemberField = createTextAttributesKey("YCE_PATH_MEMBER_FIELD",
        DefaultLanguageHighlighterColors.STATIC_FIELD)

    val Unresolved = createTextAttributesKey("YCE_UNRESOLVED",
        CodeInsightColors.WRONG_REFERENCES_ATTRIBUTES)

    val OPERATOR_KEYS = arrayOf(Operator)
    val ACCESS_OPERATOR_KEYS = arrayOf(AccessOperator)
    val DOT_KEYS = arrayOf(Dot)
    val PARENTHESES_KEYS = arrayOf(Parentheses)
    val IDENTIFIER_KEYS = arrayOf(Identifier)
    val BAD_CHAR_KEYS = arrayOf(BadCharacter)
}
