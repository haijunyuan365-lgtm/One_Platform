package com.oneplatform.backend.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ApiResponseTest {

    @Test
    void successWrapsPayloadWithStandardCodeAndTimestamp() {
        ApiResponse<String> response = ApiResponse.success("ok");

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.success()).isTrue();
        assertThat(response.message()).isEqualTo("success");
        assertThat(response.data()).isEqualTo("ok");
        assertThat(response.timestamp()).isPositive();
    }

    @Test
    void failureWrapsMessageWithoutPayload() {
        ApiResponse<Void> response = ApiResponse.failure(403, "forbidden");

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo("forbidden");
        assertThat(response.data()).isNull();
        assertThat(response.timestamp()).isPositive();
    }
}
