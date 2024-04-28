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

package io.github.jeffset.yatagan.intellij.conditions.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;

import io.github.jeffset.yatagan.intellij.psi.YceTypes;

%%

%class YceLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{ return;
%eof}

%%

<YYINITIAL> {
    [\s\n\r]+               { return TokenType.WHITE_SPACE; }
    "!"                     { return YceTypes.NOT_OP; }
    "&"                     { return YceTypes.AND_OP; }
    "|"                     { return YceTypes.OR_OP; }
    "::"                    { return YceTypes.ACCESS; }
    "."                     { return YceTypes.DOT; }
    "("                     { return YceTypes.L_PAREN; }
    ")"                     { return YceTypes.R_PAREN; }
    "@"                     { return YceTypes.AT; }
    [a-zA-Z_][a-zA-Z0-9_]*  { return YceTypes.IDENTIFIER; }
}

[^]                     { return TokenType.BAD_CHARACTER; }
