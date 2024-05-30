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
        "<warning descr="Condition Provider qualifier is redundant with a single import.">Hello</warning>::sIsEnabled.<error descr="Unresolved method/field: `nothing`.">nothing</error>",
        imports = {Hello.class})
@interface WithUnresolvedThings {}

@ConditionExpression(value = "Hello::<error descr="The end of the condition access path `sByte` does not evaluate to boolean. Got `byte` instead.">sByte</error> | " +
        "Foo::bar.<error descr="The end of the condition access path `getByte` does not evaluate to boolean. Got `byte` instead."><error descr="The method/field should be public (or internal): `getByte`.">getByte</error></error> & " +
        "Foo::bar.<error descr="The end of the condition access path `bbb` does not evaluate to boolean. Got `java.lang.String` instead.">bbb</error>",
        imports = {Hello.class, Foo.class})
@interface WithNonBoolean {}

class Foo {
    public static final Bar bar = new Bar();
}

class Bar {
    public String bbb = "";
    byte getByte() { return 0; }
}

class Hello {
    // language="YataganConditionExpression"
    static final String DANGLING_EXPRESSION = "<warning descr="Unable to find an enclosing Yatagan annotation (@Condition/@ConditionExpression). This language is only supported while injected into such an annotation, no semantic highlighting is currently possible.">!Hello::foo | @Foo</warning>";

    public static boolean sIsEnabled = false;
    public static boolean sIsEnabledB = false;
    public static byte sByte = 0;
}