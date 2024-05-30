@file:OptIn(ConditionsApi::class)
import com.yandex.yatagan.Condition
import com.yandex.yatagan.AnyConditions
import com.yandex.yatagan.AnyCondition
import com.yandex.yatagan.ConditionExpression
import com.yandex.yatagan.ConditionsApi

@Condition(Hello::class, condition = "<error descr="Legacy @Condition only supports a single unqualified boolean variable.">Hello::sIsEnabled</error>")
annotation class UnsupportedLegacyCondition

@AnyConditions(
    AnyCondition(
        Condition(Hello::class, "!<error descr="Unresolved method/field: `what`.">what</error>"),
    Condition(Hello::class, "<error descr="Legacy @Condition only supports a single unqualified boolean variable.">@Foo</error>"),
),
)
annotation class InvalidNestedLegacyConditions

@AnyConditions(
    AnyCondition(
        Condition(Hello::class, "!getSIsEnabled"),
        Condition(Hello::class, "INSTANCE.getSIsEnabledB"),
    ),
)
annotation class ValidNestedLegacyConditions

@ConditionExpression(
    "<error descr="Unresolved feature reference: `@Feature`.">@Feature</error> & " +
            "<error descr="Unresolved qualifier: `Goo`.">Goo</error>::<error descr="Unresolved method/field: `isEnabled`.">isEnabled</error> | " +
"<warning descr="Condition Provider qualifier is redundant with a single import.">Hello</warning>::getSIsEnabled.<error descr="Unresolved method/field: `nothing`.">nothing</error> & " +
"<error descr="The method/field should be public (or internal): `foo`.">foo</error> & " +
"<warning descr="Condition Provider qualifier is redundant with a single import."><warning descr="It seems that Kotlin object `Hello` is being used as a non-static condition provider here. That is very likely an error. \"INSTANCE\" should be added as a first member of the condition access path to use the object from the static context, and not try to resolve its instance from in the graph.">Hello</warning></warning>::getSIsEnabledB & " +
"INSTANCE.<error descr="A method with parameters can not be used in the condition path: `withParams`.">withParams</error>" +
"", Hello::class,
)
annotation class WithUnresolvedThings

internal object Hello {
    // language="YataganConditionExpression"
    const val DANGLING_EXPRESSION: String = "<warning descr="Unable to find an enclosing Yatagan annotation (@Condition/@ConditionExpression). This language is only supported while injected into such an annotation, no semantic highlighting is currently possible.">!Hello::foo | @Foo</warning>"

    @JvmStatic
    var sIsEnabled: Boolean = false
    var sIsEnabledB: Boolean = false

    fun withParams(i: Boolean) = i

    private val foo = false
}