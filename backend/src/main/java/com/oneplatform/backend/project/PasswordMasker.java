package com.oneplatform.backend.project;

public final class PasswordMasker {

    private static final String MASK = "************";

    private PasswordMasker() {
    }

    public static String mask(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            return "";
        }
        return MASK;
    }
}
