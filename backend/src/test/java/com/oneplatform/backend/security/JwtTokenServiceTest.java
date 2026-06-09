package com.oneplatform.backend.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;

import org.junit.jupiter.api.Test;

class JwtTokenServiceTest {

    @Test
    void generatedTokenCanBeParsed() {
        JwtTokenService tokenService = new JwtTokenService("0123456789abcdef0123456789abcdef", Duration.ofMinutes(5));

        String token = tokenService.generate(12L, "admin");
        JwtUser jwtUser = tokenService.parse(token);

        assertThat(jwtUser.userId()).isEqualTo(12L);
        assertThat(jwtUser.username()).isEqualTo("admin");
    }

    @Test
    void tamperedTokenIsRejected() {
        JwtTokenService tokenService = new JwtTokenService("0123456789abcdef0123456789abcdef", Duration.ofMinutes(5));

        String token = tokenService.generate(12L, "admin") + "x";

        assertThatThrownBy(() -> tokenService.parse(token))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid token");
    }
}
