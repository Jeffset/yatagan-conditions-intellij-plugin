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

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import io.github.jeffset.yatagan.intellij.conditions.lexer.YceLexerAdapter
import io.github.jeffset.yatagan.intellij.psi.YceTypes

class YceSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer {
        return YceLexerAdapter()
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> = with(YceTextAttributeKeys) {
        return when(tokenType) {
            YceTypes.ACCESS -> ACCESS_OPERATOR_KEYS
            YceTypes.AT -> OPERATOR_KEYS
            YceTypes.AND_OP -> OPERATOR_KEYS
            YceTypes.DOT -> DOT_KEYS
            YceTypes.IDENTIFIER -> IDENTIFIER_KEYS
            YceTypes.L_PAREN -> PARENTHESES_KEYS
            YceTypes.NOT_OP -> OPERATOR_KEYS
            YceTypes.OR_OP -> OPERATOR_KEYS
            YceTypes.R_PAREN -> PARENTHESES_KEYS
            TokenType.BAD_CHARACTER -> BAD_CHAR_KEYS
            else -> emptyArray()
        }
    }
}
