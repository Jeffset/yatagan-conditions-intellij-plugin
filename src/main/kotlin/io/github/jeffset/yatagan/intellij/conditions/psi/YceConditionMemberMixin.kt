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

package io.github.jeffset.yatagan.intellij.conditions.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMember
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.findParentOfType
import io.github.jeffset.yatagan.intellij.conditions.ResolvedMember
import io.github.jeffset.yatagan.intellij.conditions.resolve

abstract class YceConditionMemberMixin(node: ASTNode) : ASTWrapperPsiElement(node), YceConditionMember {
    override fun getReference(): PsiReference? {
        return ReferenceImpl(this)
    }

    private class ReferenceImpl(
        element: YceConditionMember,
    ) : PsiReferenceBase<YceConditionMember>(element) {
        override fun getVariants(): Array<Any> {
            val clazz = doResolve().resolvedOn ?: return emptyArray()
            return buildList<PsiMember> {
                addAll(clazz.allMethods)
                addAll(clazz.allFields)
            }.filter {
                it.containingClass?.qualifiedName != "java.lang.Object"
            }.distinctBy { it.name }
                .toTypedArray()
        }

        override fun resolve(): PsiElement? {
            return doResolve().resolvedMember
        }

        private fun doResolve(): ResolvedMember {
            return checkNotNull(element.findParentOfType<YceConditionVariable>()).resolve().let { resolved ->
                resolved.path.first { it.element === element }
            }
        }
    }
}