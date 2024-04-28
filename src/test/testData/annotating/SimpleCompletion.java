import com.yandex.yatagan.ConditionExpression;

@ConditionExpression(value = "Hello::<caret>", imports = Hello.class)
@interface TestAnnotation {}

class Hello {
    static boolean sIsEnabled = false;
    static boolean sFooBar = false;
    static boolean sHooBar = false;
}