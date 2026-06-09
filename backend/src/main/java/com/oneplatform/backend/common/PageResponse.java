package com.oneplatform.backend.common;

import java.util.List;

public record PageResponse<T>(
        List<T> list,
        long total,
        long page,
        long pageSize
) {
}
