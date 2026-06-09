package com.oneplatform.backend.organization;

import java.time.LocalDateTime;

public record UserRecord(
        Long id,
        String username,
        String realName,
        String nickname,
        String email,
        String phone,
        Long departmentId,
        String departmentName,
        String avatar,
        String positionName,
        Integer status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime lastLoginTime,
        String lastLoginIp,
        String remark
) {
}
