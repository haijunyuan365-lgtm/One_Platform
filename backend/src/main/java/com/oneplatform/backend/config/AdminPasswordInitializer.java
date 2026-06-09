package com.oneplatform.backend.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(InitProperties.class)
public class AdminPasswordInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final InitProperties properties;

    public AdminPasswordInitializer(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder, InitProperties properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        String password = properties.adminPassword();
        if (password == null || password.isBlank()) {
            return;
        }
        String hash = passwordEncoder.encode(password);
        jdbcTemplate.update("UPDATE sys_user SET password_hash = ? WHERE password_hash = 'INIT_HASH_PENDING'", hash);
    }
}
