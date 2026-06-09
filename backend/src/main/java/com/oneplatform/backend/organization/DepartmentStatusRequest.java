package com.oneplatform.backend.organization;

import jakarta.validation.constraints.NotNull;

public record DepartmentStatusRequest(
        @NotNull Integer status
) {
}
