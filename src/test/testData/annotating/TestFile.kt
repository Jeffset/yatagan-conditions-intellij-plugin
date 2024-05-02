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
        Condition(Hello::class, "!sIsEnabled"), // TODO: error?
        Condition(Hello::class, "getSIsEnabledB"),
    ),
)
annotation class ValidNestedLegacyConditions

@ConditionExpression(
    "<error descr="Unresolved feature reference: `@Feature`.">@Feature</error> & " +
            "<error descr="Unresolved qualifier: `Goo`.">Goo</error>::<error descr="Unresolved method/field: `isEnabled`.">isEnabled</error> | " +
"Hello::sIsEnabled.<error descr="Unresolved method/field: `nothing`.">nothing</error>", Hello::class,
)
annotation class WithUnresolvedThings

internal object Hello {
    // language="YataganConditionExpression"
    const val DANGLING_EXPRESSION: String = "<warning descr="Unable to find an enclosing Yatagan annotation (@Condition/@ConditionExpression). This language is only supported while injected into such an annotation, no semantic highlighting is currently possible.">!Hello::foo | @Foo</warning>"

    var sIsEnabled: Boolean = false
    var sIsEnabledB: Boolean = false
}