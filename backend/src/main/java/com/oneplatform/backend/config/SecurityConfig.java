package com.oneplatform.backend.config;

import java.time.Duration;

import com.oneplatform.backend.security.JwtTokenService;
import com.oneplatform.backend.security.SecurityProperties;
import com.oneplatform.backend.portal.CredentialCryptoProperties;
import com.oneplatform.backend.portal.PortalProperties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableConfigurationProperties({SecurityProperties.class, PortalProperties.class, CredentialCryptoProperties.class})
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry -> registry.anyRequest().permitAll())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtTokenService jwtTokenService(SecurityProperties properties) {
        return new JwtTokenService(
                properties.jwtSecret(),
                Duration.ofMinutes(properties.accessTokenMinutes())
        );
    }
}
