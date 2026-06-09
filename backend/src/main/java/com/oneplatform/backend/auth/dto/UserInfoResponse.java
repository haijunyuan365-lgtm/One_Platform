package com.oneplatform.backend.auth.dto;

import java.time.LocalDateTime;
import java.util.List;

public record UserInfoResponse(
        Long id,
        LocalDateTime createTime,
        LocalDateTime updateTime,
        Long createBy,
        Long updateBy,
        String username,
        String nickname,
        String email,
        String phone,
        String avatar,
        Integer status,
        LocalDateTime lastLoginTime,
        String lastLoginIp,
        String remark,
        Long departmentId,
        DepartmentInfo department,
        List<RoleInfo> roles,
        List<String> buttons
) {

    public record DepartmentInfo(
            Long id,
            LocalDateTime createTime,
            LocalDateTime updateTime,
            Long createBy,
            Long updateBy,
            String departmentName,
            String departmentCode,
            Long parentId,
            String type,
            Long companyId,
            Integer status,
            Integer sort,
            Long managerId,
            String managerName,
            String phone,
            String email
    ) {
    }

    public record RoleInfo(
            Long id,
            LocalDateTime createTime,
            LocalDateTime updateTime,
            Long createBy,
            Long updateBy,
            String name,
            String code,
            String description,
            String permissions,
            Integer status,
            Integer sort,
            Integer isSystem
    ) {
    }
}
