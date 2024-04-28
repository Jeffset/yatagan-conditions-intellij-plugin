import com.yandex.yatagan.Condition;
import com.yandex.yatagan.ConditionExpression;

@Condition(value = Hello.class, condition = "<error descr="Legacy @Condition only supports a single unqualified boolean variable.">Hello::sIsEnabled</error>")
@interface UnsupportedLegacyCondition {}

@ConditionExpression(value = "<error descr="Unresolved feature reference: @Feature">@Feature</error> & " +
        "<error descr="Unresolved qualifier: Goo">Goo</error>::<error descr="Unresolved method/field: isEnabled">isEnabled</error> | " +
        "Hello::sIsEnabled.<error descr="Unresolved method/field: nothing">nothing</error>",
        imports = {Hello.class})
@interface WithUnresolvedThings {}

class Hello {
    // language="YataganConditionExpression"
    static final String DANGLING_EXPRESSION = "<warning descr="Unable to find an enclosing Yatagan annotation (@Condition/@ConditionExpression). This language is only supported while injected into such an annotation, no semantic highlighting is currently possible.">!Hello::foo | @Foo</warning>";

    static boolean sIsEnabled = false;
}