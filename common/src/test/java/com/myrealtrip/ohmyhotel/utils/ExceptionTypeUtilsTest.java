package com.myrealtrip.ohmyhotel.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionTypeUtilsTest {

    @Test
    void hasType() {
        IllegalStateException e1 = new IllegalStateException();
        IllegalStateException e2 = new IllegalStateException("", new NullPointerException());

        assertThat(ExceptionTypeUtils.hasType(e1, IllegalStateException.class)).isTrue();
        assertThat(ExceptionTypeUtils.hasType(e1, NullPointerException.class)).isFalse();

        assertThat(ExceptionTypeUtils.hasType(e2, IllegalStateException.class)).isTrue();
        assertThat(ExceptionTypeUtils.hasType(e2, NullPointerException.class)).isTrue();
        assertThat(ExceptionTypeUtils.hasType(e2, RuntimeException.class)).isTrue();
        assertThat(ExceptionTypeUtils.hasType(e2, IllegalArgumentException.class)).isFalse();
    }
}