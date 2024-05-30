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
import com.intellij.psi.PsiClass
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import io.github.jeffset.yatagan.intellij.conditions.ResolvedConditionVariable
import io.github.jeffset.yatagan.intellij.conditions.ResolvedMember
import io.github.jeffset.yatagan.intellij.conditions.YceContext

abstract class YceConditionVariableMixin(node: ASTNode) : ASTWrapperPsiElement(node), YceConditionVariable {
    private val resolved: CachedValue<ResolvedConditionVariable> by lazy {
        CachedValuesManager.getManager(project).createCachedValue {
            val file = containingFile as YceFile
            val contextValue = file.cachedContext
            val resolved = doResolve(contextValue.value)
            CachedValueProvider.Result.create(resolved, *resolved.dependencies())
        }
    }

    internal fun resolve(): ResolvedConditionVariable {
        return resolved.value
    }

    private fun doResolve(
        context: YceContext?,
    ): ResolvedConditionVariable {
        val qualifier = conditionQualifier

        val psiClass: PsiClass? = if (qualifier != null) {
            qualifier.reference?.resolve() as? PsiClass
        } else {
            context?.imports?.singleOrNull()?.accept(PsiTypeAsClass)
        }

        var currentClass: PsiClass? = psiClass
        return ResolvedConditionVariable(
            path = members.mapIndexed { index, member ->
                val resolvedOn = currentClass
                val resolved = currentClass?.let { clazz ->
                    val name = member.text
                    (clazz.findMethodsByName(name, true).let { methods ->
                        // Try to resolve parameterless method first
                        methods.firstOrNull {
                            it.parameterList.isEmpty
                        } ?: methods.firstOrNull()  // fallback to any method in order to display an error
                    }?.let { method ->
                        currentClass = method.returnType?.accept(PsiTypeAsClass)
                        method
                    } ?: clazz.findFieldByName(name, true)?.let { field ->
                        currentClass = field.type.accept(PsiTypeAsClass)
                        field
                    })?.takeIf {
                        (index == 0) || !it.isStatic()
                    }
                }
                ResolvedMember(
                    element = member,
                    resolvedOn = resolvedOn,
                    resolvedMember = resolved,
                    index = index,
                )
            },
            context = context,
        )
    }

    private fun ResolvedConditionVariable?.dependencies(): Array<Any> {
        if (this == null || path.any { it.resolvedOn == null || it.resolvedMember == null }) {
            // If something is unresolved, track everything.
            return arrayOf(PsiModificationTracker.MODIFICATION_COUNT)
        }

        // If everything is resolved, track only the resolved things.
        return ArrayList<Any>().apply {
            for ((member, resolvedOn, resolvedMember) in path) {
                add(member)
                add(resolvedOn!!)
                add(resolvedMember!!)
            }
        }.toArray()
    }
}