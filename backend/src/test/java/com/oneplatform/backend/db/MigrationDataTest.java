package com.oneplatform.backend.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class MigrationDataTest {

    @Test
    void statusConfigSeedUsesDetectionAddressUrlInsteadOfProjectName() throws IOException {
        String sql = readMigration("db/migration/V2__init_data.sql");

        assertThat(sql).doesNotContain("SELECT id, 1, 'HTTP/HTTPS', name");
        assertThat(sql).contains("op_project_address");
        assertThat(sql).contains("a.url");
    }

    @Test
    void credentialSeedDoesNotUseCipherPlaceholder() throws IOException {
        String sql = readMigration("db/migration/V2__init_data.sql");

        assertThat(sql).doesNotContain("DEV_CIPHER_PLACEHOLDER");
        assertThat(sql).doesNotContain("plain:");
        assertThat(sql).contains("v1:");
    }

    private static String readMigration(String path) throws IOException {
        return new String(new ClassPathResource(path).getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}
