package com.oneplatform.backend.organization;

import java.util.List;

public record UserMutation(
        String username,
        String realName,
        String nickname,
        String avatar,
        String email,
        String phone,
        Long departmentId,
        String positionName,
        Integer status,
        String remark,
        List<Long> roleIds
) {
}
