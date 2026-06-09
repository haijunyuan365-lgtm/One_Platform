package com.oneplatform.backend.project;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PasswordMaskerTest {

    @Test
    void masksAnyNonBlankPasswordAsTwelveStars() {
        assertThat(PasswordMasker.mask("AdminPortal2026")).isEqualTo("************");
    }

    @Test
    void masksBlankPasswordAsEmptyString() {
        assertThat(PasswordMasker.mask(" ")).isEmpty();
    }
}
