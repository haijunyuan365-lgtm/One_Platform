package com.oneplatform.backend.portal;

import java.util.List;

public record PortalUserContext(
        Long userId,
        String username,
        Long departmentId,
        String departmentName,
        List<Long> roleIds
) {
}
