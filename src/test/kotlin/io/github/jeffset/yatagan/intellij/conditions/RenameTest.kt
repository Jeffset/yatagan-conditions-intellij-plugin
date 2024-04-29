package io.github.jeffset.yatagan.intellij.conditions

import com.intellij.openapi.application.ApplicationManager
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4
import org.junit.Test

class RenameTest : LightJavaCodeInsightFixtureTestCase4(
    projectDescriptor = MyLightTestProjectDescriptor,
) {
    @Test
    fun testRenameMember() = with(fixture) {
        configureByText(
            "ForRename.java",
            //language=Java
            """
            import com.yandex.yatagan.ConditionExpression;
            @ConditionExpression(value = "Foo::getHello.bar<caret>", imports = Foo.class)
            @interface TestAnnotation {}
            class Hello { boolean bar() { return false; } }
            class Foo { static Hello getHello() { return new Hello(); } }
            """.trimIndent()
        )
        ApplicationManager.getApplication().invokeAndWait {
            renameElementAtCaret("karte")
        }
        //language=Java
        checkResult("""
            import com.yandex.yatagan.ConditionExpression;
            @ConditionExpression(value = "Foo::getHello.karte", imports = Foo.class)
            @interface TestAnnotation {}
            class Hello { boolean karte() { return false; } }
            class Foo { static Hello getHello() { return new Hello(); } }
        """.trimIndent())
    }

    @Test
    fun testRenameQualifier() = with(fixture) {
        configureByText(
            "ForRename.java",
            //language=Java
            """
            import com.yandex.yatagan.ConditionExpression;
            @ConditionExpression(value = "Foo<caret>::getHello.bar & Foo::getHello", imports = Foo.class)
            @interface TestAnnotation {}
            class Hello { boolean bar() { return false; } }
            class Foo { static Hello getHello() { return new Hello(); } }
            """.trimIndent()
        )
        ApplicationManager.getApplication().invokeAndWait {
            renameElementAtCaret("Quu")
        }
        //language=Java
        checkResult("""
            import com.yandex.yatagan.ConditionExpression;
            @ConditionExpression(value = "Quu::getHello.bar & Quu::getHello", imports = Quu.class)
            @interface TestAnnotation {}
            class Hello { boolean bar() { return false; } }
            class Quu { static Hello getHello() { return new Hello(); } }
        """.trimIndent())
    }

    @Test
    fun testRenameFeatureReference() = with(fixture) {
        configureByText(
            "ForRename.java",
            //language=Java
            """
            import com.yandex.yatagan.ConditionExpression;
            @ConditionExpression(value = "@Another & !@Another<caret>", imports = Another.class)
            @interface TestAnnotation {}
            @interface Another {}
            """.trimIndent()
        )
        ApplicationManager.getApplication().invokeAndWait {
            renameElementAtCaret("Renamed")
        }
        //language=Java
        checkResult("""
            import com.yandex.yatagan.ConditionExpression;
            @ConditionExpression(value = "@Renamed & !@Renamed", imports = Renamed.class)
            @interface TestAnnotation {}
            @interface Renamed {}
        """.trimIndent())
    }
}