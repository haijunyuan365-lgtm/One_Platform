package com.oneplatform.backend.organization;

import java.util.List;

public record AdminUserResponse(
        Long id,
        String username,
        String realName,
        String nickname,
        String avatar,
        String email,
        String phone,
        Long departmentId,
        String departmentName,
        Long positionId,
        String positionName,
        Integer status,
        String createTime,
        String updateTime,
        String lastLoginTime,
        String lastLoginIp,
        String remark,
        List<RoleBrief> roles
) {

    public record RoleBrief(
            Long id,
            String name,
            String code
    ) {
    }
}
