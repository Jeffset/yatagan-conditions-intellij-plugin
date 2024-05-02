import com.yandex.yatagan.AnyConditions;
import com.yandex.yatagan.AnyCondition;
import com.yandex.yatagan.Condition;
import com.yandex.yatagan.ConditionExpression;

@Condition(value = Hello.class, condition = "<error descr="Legacy @Condition only supports a single unqualified boolean variable.">Hello::sIsEnabled</error>")
@interface UnsupportedLegacyCondition {}

@AnyConditions(
        @AnyCondition({
                @Condition(value = Hello.class, condition = "!<error descr="Unresolved method/field: `what`.">what</error>"),
                @Condition(value = Hello.class, condition = "<error descr="Legacy @Condition only supports a single unqualified boolean variable.">@Foo</error>"),
        })
)
@interface InvalidNestedLegacyConditions {}

@AnyConditions(
        @AnyCondition({
                @Condition(value = Hello.class, condition = "!sIsEnabled"),
                @Condition(value = Hello.class, condition = "sIsEnabledB")
        })
)
@interface ValidNestedLegacyConditions {}

@ConditionExpression(value = "<error descr="Unresolved feature reference: `@Feature`.">@Feature</error> & " +
        "<error descr="Unresolved qualifier: `Goo`.">Goo</error>::<error descr="Unresolved method/field: `isEnabled`.">isEnabled</error> | " +
        "Hello::sIsEnabled.<error descr="Unresolved method/field: `nothing`.">nothing</error>",
        imports = {Hello.class})
@interface WithUnresolvedThings {}

class Hello {
    // language="YataganConditionExpression"
    static final String DANGLING_EXPRESSION = "<warning descr="Unable to find an enclosing Yatagan annotation (@Condition/@ConditionExpression). This language is only supported while injected into such an annotation, no semantic highlighting is currently possible.">!Hello::foo | @Foo</warning>";

    static boolean sIsEnabled = false;
    static boolean sIsEnabledB = false;
}