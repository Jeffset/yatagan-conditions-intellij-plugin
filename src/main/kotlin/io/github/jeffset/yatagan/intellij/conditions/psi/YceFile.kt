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

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiType
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider.Result
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import io.github.jeffset.yatagan.intellij.conditions.YceContext
import io.github.jeffset.yatagan.intellij.conditions.YceFileType
import io.github.jeffset.yatagan.intellij.conditions.YceLanguage
import org.jetbrains.annotations.NonNls
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UClassLiteralExpression
import org.jetbrains.uast.ULiteralExpression
import org.jetbrains.uast.getParentOfType
import org.jetbrains.uast.getQualifiedName
import org.jetbrains.uast.toUElement
import org.jetbrains.uast.visitor.AbstractUastVisitor

@NonNls private const val CONDITION_EXPRESSION_FQN = "com.yandex.yatagan.ConditionExpression"
@NonNls private const val LEGACY_CONDITION_FQN = "com.yandex.yatagan.Condition"
@NonNls private const val IMPORTS_AS_FQN = "com.yandex.yatagan.ConditionExpression.ImportAs"

private val CONDITION_FQNS = arrayOf(
    CONDITION_EXPRESSION_FQN,
    LEGACY_CONDITION_FQN,
)

@NonNls private const val IMPORTS_ATTRIBUTE_NAME = "imports"
@NonNls private const val IMPORT_AS_ATTRIBUTE_NAME = "importAs"
@NonNls private const val VALUE_ATTRIBUTE_NAME = "value"

class YceFile(
    viewProvider: FileViewProvider,
) : PsiFileBase(viewProvider, YceLanguage) {
    override fun getFileType() = YceFileType
    override fun toString() = "Yatagan Condition Expression Syntax Root"

    internal val cachedContext: CachedValue<YceContext?> = CachedValuesManager.getManager(project).createCachedValue {
        val manager = InjectedLanguageManager.getInstance(viewProvider.manager.project)
        when(val hostPsi = manager.getInjectionHost(viewProvider) ?: manager.getInjectionHost(this)) {
            null -> Result.create(null, PsiModificationTracker.MODIFICATION_COUNT)
            else -> {
                val dependencies = ArrayList<Any>(2)
                val context = tryResolveContext(hostPsi, dependencies)
                Result.create(context, *dependencies.toArray())
            }
        }
    }

    internal val context: YceContext?
        get() = cachedContext.value

    private fun tryResolveContext(hostPsi: PsiElement, dependencies: MutableList<in Any>): YceContext? {
        val host = hostPsi.toUElement()

        // Try to resolve a parent of annotation
        val annotation = host?.getParentOfType<UCallExpression>()
            ?.takeIf { it.classReference.getQualifiedName() in CONDITION_FQNS }?.let {
                // Reinterpret this as nested annotation
                it.sourcePsi.toUElement(UAnnotation::class.java)
            } ?: host?.getParentOfType<UAnnotation>()

        if (annotation == null) {
            dependencies.add(PsiModificationTracker.MODIFICATION_COUNT)
            return null
        }
        dependencies.add(annotation.enclosingSourcePsi())

        when (annotation.qualifiedName) {
            CONDITION_EXPRESSION_FQN -> {
                val imports = mutableListOf<PsiType>()

                val importsValue = annotation.attributeValues.find { it.name == IMPORTS_ATTRIBUTE_NAME }
                importsValue?.expression?.accept(object : AbstractUastVisitor() {
                    override fun visitClassLiteralExpression(node: UClassLiteralExpression): Boolean {
                        node.type?.let(imports::add)
                        return true
                    }
                })

                val namedImports = mutableListOf<Pair<PsiType, String>>()
                val importsAsValue = annotation.attributeValues.find { it.name == IMPORT_AS_ATTRIBUTE_NAME }
                importsAsValue?.expression?.accept(object : AbstractUastVisitor() {
                    override fun visitCallExpression(node: UCallExpression): Boolean {
                        if (node.valueArgumentCount != 2 ||
                            node.classReference.getQualifiedName() != IMPORTS_AS_FQN
                        ) {
                            return false
                        }

                        var type: PsiType? = null
                        node.valueArguments[0].accept(object : AbstractUastVisitor() {
                            override fun visitClassLiteralExpression(node: UClassLiteralExpression): Boolean {
                                type = node.type
                                return true
                            }
                        })
                        var name: String? = null
                        node.valueArguments[1].accept(object : AbstractUastVisitor() {
                            override fun visitLiteralExpression(node: ULiteralExpression): Boolean {
                                name = node.value as? String
                                return true
                            }
                        })

                        val localType = type ?: return true
                        val localName = name ?: return true
                        namedImports += localType to localName
                        return true
                    }
                })

                return YceContext(
                    imports = imports,
                    namedImports = namedImports,
                    isLegacy = false,
                )
            }

            LEGACY_CONDITION_FQN -> {
                var importedType: PsiType? = null
                annotation.attributeValues.find { it.name == VALUE_ATTRIBUTE_NAME }?.expression?.accept(
                    object : AbstractUastVisitor() {
                        override fun visitClassLiteralExpression(node: UClassLiteralExpression): Boolean {
                            importedType = node.type
                            return true
                        }
                    })
                return YceContext(
                    imports = listOfNotNull(importedType),
                    namedImports = emptyList(),
                    isLegacy = true,
                )
            }

            else -> return null
        }
    }
}