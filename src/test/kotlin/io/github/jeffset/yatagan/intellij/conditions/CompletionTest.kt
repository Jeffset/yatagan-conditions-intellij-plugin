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
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4
import org.junit.Test

class CompletionTest : LightJavaCodeInsightFixtureTestCase4(
    projectDescriptor = MyLightTestProjectDescriptor,
) {
    @Test
    fun testPathCompletionSimple() = with(fixture) {
        configureByText(
            "SimpleCompletion.java",
            // language=Java
            """
                import com.yandex.yatagan.ConditionExpression;
                
                @ConditionExpression(value = "Hello::<caret>", imports = Hello.class)
                @interface TestAnnotation {}
                
                class Hello {
                    static void hello() { }
                    static boolean sIsEnabled = false;
                    static boolean sFooBar = false;
                    static int sInt = 0;
                    static boolean sHooBar = false;
                    static long getLong() { return 0L; }
                }
            """.trimIndent()
        )
        complete(CompletionType.BASIC)

        assertSameElements(
            checkNotNull(lookupElementStrings),
            "sIsEnabled",
            "sFooBar",
            "sHooBar",
        )
    }

    @Test
    fun testPathCascadeCompletion() = with(fixture) {
        configureByText(
            "CascadeCompletion.java",
            // language=Java
            """
                import com.yandex.yatagan.ConditionExpression;

                @ConditionExpression(value = "Foo::getHello.<caret>", imports = Foo.class)
                @interface TestAnnotation {}

                class Hello {
                    boolean mIsEnabled = false;
                    boolean value1() { return false; }
                    Bar bar() { return new Bar(); }
                }

                class Bar {}

                class Foo {
                    static Hello getHello() { return new Hello(); }
                }
            """.trimIndent()
        )
        complete(CompletionType.BASIC)

        assertSameElements(
            checkNotNull(lookupElementStrings),
            "mIsEnabled",
            "value1",
            "bar",
        )
    }
}
