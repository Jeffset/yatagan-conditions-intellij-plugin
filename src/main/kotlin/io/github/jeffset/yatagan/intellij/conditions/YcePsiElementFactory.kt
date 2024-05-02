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

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import io.github.jeffset.yatagan.intellij.conditions.psi.YceConditionMember
import io.github.jeffset.yatagan.intellij.conditions.psi.YceConditionQualifier
import io.github.jeffset.yatagan.intellij.conditions.psi.YceFeatureReferenceVariable
import io.github.jeffset.yatagan.intellij.conditions.psi.findChildOfType
import org.jetbrains.annotations.NonNls

@NonNls
@Service(Service.Level.PROJECT)
class YcePsiElementFactory(
    private val project: Project,
) {
    fun createConditionMember(text: String): YceConditionMember {
        return createElement<YceConditionMember>("T::$text")
    }

    fun createConditionQualifier(text: String): YceConditionQualifier {
        return createElement<YceConditionQualifier>("$text::t")
    }

    fun createFeatureReferenceVariable(text: String): YceFeatureReferenceVariable {
        return createElement<YceFeatureReferenceVariable>("@$text")
    }

    private inline fun <reified T : PsiElement> createElement(text: String): T {
        return checkNotNull(
            PsiFileFactory.getInstance(project).createFileFromText("", YceFileType, text)
                .findChildOfType<T>()
        )
    }

    companion object {
        @JvmStatic
        fun getInstance(project: Project): YcePsiElementFactory {
            return project.getService(YcePsiElementFactory::class.java)
        }
    }
}