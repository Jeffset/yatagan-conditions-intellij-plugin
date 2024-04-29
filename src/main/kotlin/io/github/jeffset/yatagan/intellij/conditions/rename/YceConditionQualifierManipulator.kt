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

package io.github.jeffset.yatagan.intellij.conditions.rename

import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import io.github.jeffset.yatagan.intellij.conditions.YcePsiElementFactory
import io.github.jeffset.yatagan.intellij.conditions.psi.YceConditionQualifier

class YceConditionQualifierManipulator : AbstractElementManipulator<YceConditionQualifier>() {
    override fun handleContentChange(
        element: YceConditionQualifier,
        range: TextRange,
        newContent: String
    ): YceConditionQualifier {
        return YcePsiElementFactory.getInstance(element.project).createConditionQualifier(newContent)
            .also { element.replace(it) }
    }
}