package com.oneplatform.backend.project;

public record ProjectQuery(
        String keyword,
        String category,
        String maintainer,
        Integer enabled,
        String status,
        long page,
        long pageSize
) {
}
