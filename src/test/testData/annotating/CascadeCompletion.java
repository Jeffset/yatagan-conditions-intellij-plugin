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