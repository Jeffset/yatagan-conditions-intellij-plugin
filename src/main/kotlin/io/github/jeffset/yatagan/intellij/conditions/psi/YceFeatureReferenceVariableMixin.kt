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
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.PsiType
import com.intellij.psi.PsiTypeVisitor

abstract class YceFeatureReferenceVariableMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), YceFeatureReferenceVariable {
    override fun getReference(): PsiReference? {
        return ReferenceImpl(this)
    }

    private class ReferenceImpl(
        element: YceFeatureReferenceVariable,
    ) : PsiReferenceBase<YceFeatureReferenceVariable>(element) {
        override fun resolve(): PsiElement? {
            val file = element.containingFile as? YceFile ?: return null
            val context = file.context ?: return null

            val simpleName = element.identifier.text
            val type = context.imports.asSequence().mapNotNull {
                it.accept(object : PsiTypeVisitor<PsiClass?>() {
                    override fun visitType(type: PsiType) = null
                    override fun visitClassType(classType: PsiClassType): PsiClass? {
                        return if (classType.className == simpleName)
                            classType.resolve() else null
                    }
                })
            }.firstOrNull()

            if (type != null) {
                return type
            }

            val namedImport = context.namedImports.find { it.second == simpleName } ?: return null
            return namedImport.first.accept(object : PsiTypeVisitor<PsiClass?>() {
                override fun visitType(type: PsiType) = null
                override fun visitClassType(classType: PsiClassType): PsiClass? {
                    return classType.resolve()
                }
            })
        }
    }
}
