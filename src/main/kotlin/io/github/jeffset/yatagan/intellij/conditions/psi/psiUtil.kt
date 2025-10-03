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

import com.intellij.psi.CommonClassNames
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMember
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifier
import com.intellij.psi.PsiModifierListOwner
import com.intellij.psi.PsiType
import com.intellij.psi.PsiTypeVisitor
import com.intellij.psi.PsiTypes
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.uast.UElement

internal object PsiTypeAsClass : PsiTypeVisitor<PsiClass?>() {
    override fun visitType(type: PsiType) = null
    override fun visitClassType(classType: PsiClassType) = classType.resolve()
}

internal inline fun <reified T : PsiElement> PsiElement?.findChildOfType(): T? {
    return PsiTreeUtil.findChildOfType(this, T::class.java)
}

internal fun UElement.enclosingSourcePsi(): PsiElement {
    var current: UElement? = this
    while (current != null) {
        current.sourcePsi?.let {
            return it
        }
        current = current.uastParent
    }
    throw IllegalStateException("Unreached")
}

internal fun PsiType.isBoolean(context: PsiElement): Boolean {
    return this == PsiTypes.booleanType() || run {
        val boxed =  PsiClassType.getTypeByName(
            CommonClassNames.JAVA_LANG_BOOLEAN, context.manager.project, context.resolveScope)
        boxed == this
    }
}

internal fun PsiModifierListOwner.isStatic(): Boolean = when {
    hasModifierProperty(PsiModifier.STATIC) -> true
    else -> false
}

internal fun PsiMember.fieldOrMethodReturnType(): PsiType? = when(this) {
    is PsiMethod -> returnType
    is PsiField -> type
    else -> null
}

internal fun PsiClass.isKotlinObject(): Boolean {
    // Can't use Kotlin classes here, so use the usual heuristics

    if (getAnnotation("kotlin.Metadata") == null &&  // For binary classes
        // For source "light" impls
        !javaClass.canonicalName.startsWith("org.jetbrains.kotlin")) {
        return false
    }

    val instance = findFieldByName("INSTANCE", /* checkBases = */ false)
        ?: return false

    if (!instance.hasModifierProperty(PsiModifier.STATIC) ||
        !instance.hasModifierProperty(PsiModifier.PUBLIC) ||
        !instance.hasModifierProperty(PsiModifier.FINAL)) {
        return false
    }

    val isOfSameType = instance.type.accept(object : PsiTypeVisitor<Boolean>() {
        override fun visitType(type: PsiType) = false
        override fun visitClassType(classType: PsiClassType) = classType.resolve() == this@isKotlinObject
    })
    return isOfSameType
}