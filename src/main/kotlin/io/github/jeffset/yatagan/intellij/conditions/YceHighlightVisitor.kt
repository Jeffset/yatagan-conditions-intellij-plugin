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

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import io.github.jeffset.yatagan.intellij.conditions.psi.YceConditionQualifier
import io.github.jeffset.yatagan.intellij.conditions.psi.YceConditionVariable
import io.github.jeffset.yatagan.intellij.conditions.psi.YceExpression
import io.github.jeffset.yatagan.intellij.conditions.psi.YceFeatureReferenceVariable
import io.github.jeffset.yatagan.intellij.conditions.psi.YceFile
import io.github.jeffset.yatagan.intellij.conditions.psi.YceNotExpression
import io.github.jeffset.yatagan.intellij.conditions.psi.YceVariableExpression
import io.github.jeffset.yatagan.intellij.conditions.psi.YceVisitor

class YceHighlightVisitor : HighlightVisitor, YceVisitor<Unit>() {
    private var holder: HighlightInfoHolder? = null

    override fun suitableForFile(file: PsiFile): Boolean {
        return file is YceFile
    }

    override fun visit(element: PsiElement) {
        element.accept(this)
    }

    override fun analyze(
        file: PsiFile,
        updateWholeFile: Boolean,
        holder: HighlightInfoHolder,
        action: Runnable,
    ): Boolean {
        try {
            // If the context can't be resolved, then no need to even try highlighting
            val context = (file as YceFile).context ?: run {
                HighlightInfo.newHighlightInfo(HighlightInfoType.WARNING)
                    .descriptionAndTooltip(
                        "Unable to find an enclosing Yatagan annotation (@Condition/@ConditionExpression). " +
                                "This language is only supported while injected into such an annotation, " +
                                "no semantic highlighting is currently possible."
                    )
                    .range(file)
                    .create()
                    .let(holder::add)
                return false
            }
            this.holder = holder

            if (context.isLegacy && !validateForLegacyCondition(file.firstChild)) {
                HighlightInfo.newHighlightInfo(HighlightInfoType.ERROR)
                    .descriptionAndTooltip("Legacy @Condition only supports a single unqualified boolean variable.")
                    .range(file)
                    .create()
                    .let(holder()::add)
                return true
            }

            action.run()
        } finally {
            this.holder = null
        }
        return true
    }

    private fun validateForLegacyCondition(element: PsiElement?): Boolean {
        if (element !is YceExpression)
            return false
        val expression = element.unwrapNot()
        if (expression !is YceVariableExpression)
            return false
        val variable = expression.variable
        if (variable !is YceConditionVariable)
            return false
        if (variable.conditionQualifier != null) {
            return false
        }
        return true
    }

    override fun clone() = YceHighlightVisitor()

    override fun visitConditionQualifier(element: YceConditionQualifier) {
        when (element.reference?.resolve()) {
            null -> HighlightInfo.newHighlightInfo(UNRESOLVED)
                .descriptionAndTooltip("Unresolved qualifier: ${element.text}")
            else -> HighlightInfo.newHighlightInfo(CONDITION_QUALIFIER)
        }.range(element).create().let(holder()::add)
    }

    override fun visitFeatureReferenceVariable(element: YceFeatureReferenceVariable) {
        when (element.reference?.resolve()) {
            null -> HighlightInfo.newHighlightInfo(UNRESOLVED)
                .descriptionAndTooltip("Unresolved feature reference: ${element.text}")
            else -> HighlightInfo.newHighlightInfo(FEATURE_REFERENCE)
        }.range(element).create().let(holder()::add)
    }

    override fun visitConditionVariable(element: YceConditionVariable) {
        for ((member, _, resolvedMember) in element.resolve().path) {
            if (resolvedMember != null) {
                HighlightInfo.newHighlightInfo(MEMBER_QUALIFIER)
            } else {
                HighlightInfo.newHighlightInfo(UNRESOLVED)
                    .descriptionAndTooltip("Unresolved method/field: ${member.text}")
            }.range(member).create().let(holder()::add)
        }
    }

    private fun holder() = checkNotNull(holder)

    private fun YceExpression.unwrapNot(): YceExpression? = when(this) {
        is YceNotExpression -> expression
        else -> this
    }
}

private val UNRESOLVED = HighlightInfoType.HighlightInfoTypeImpl(
    HighlightSeverity.ERROR,
    YceTextAttributeKeys.Unresolved,
)

private val CONDITION_QUALIFIER = HighlightInfoType.HighlightInfoTypeImpl(
    HighlightInfoType.SYMBOL_TYPE_SEVERITY,
    YceTextAttributeKeys.ConditionQualifier,
)

private val FEATURE_REFERENCE = HighlightInfoType.HighlightInfoTypeImpl(
    HighlightInfoType.SYMBOL_TYPE_SEVERITY,
    YceTextAttributeKeys.FeatureReference,
)

private val MEMBER_QUALIFIER = HighlightInfoType.HighlightInfoTypeImpl(
    HighlightInfoType.SYMBOL_TYPE_SEVERITY,
    YceTextAttributeKeys.PathMember,
)