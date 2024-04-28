package com.yandex.yatagan;

public @interface ConditionExpression {
    String value();

    Class<?>[] imports();

    ConditionExpression.ImportAs[] importAs() default {};

    @interface ImportAs {
        Class<?> value();
        String alias();
    }
}