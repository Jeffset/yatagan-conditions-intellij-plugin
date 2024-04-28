package com.yandex.yatagan;

public @interface Condition {
    Class<?> value();

    String condition();
}