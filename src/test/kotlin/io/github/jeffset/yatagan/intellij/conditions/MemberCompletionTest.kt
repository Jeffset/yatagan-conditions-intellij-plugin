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

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.testFramework.UsefulTestCase.assertSameElements
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase.JAVA_8
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4
import org.junit.Test

class MemberCompletionTest : LightJavaCodeInsightFixtureTestCase4(
    JAVA_8,
    "src/test/testData/annotating"
) {
    @Test
    fun testPathCompletionSimple() {
        fixture.configureByFiles(
            "SimpleCompletion.java",
            "com/yandex/yatagan/ConditionExpression.java",
        )
        fixture.complete(CompletionType.BASIC);

        assertSameElements(
            checkNotNull(fixture.lookupElementStrings),
            "sIsEnabled",
            "sFooBar",
            "sHooBar",
        );
    }

    @Test
    fun testPathCascadeCompletion() {
        fixture.configureByFiles(
            "CascadeCompletion.java",
            "com/yandex/yatagan/ConditionExpression.java",
        )
        fixture.complete(CompletionType.BASIC);

        assertSameElements(
            checkNotNull(fixture.lookupElementStrings),
            "mIsEnabled",
            "value1",
            "bar",
        );
    }
}
