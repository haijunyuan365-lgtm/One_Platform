package com.oneplatform.backend.organization;

import java.util.List;

public interface UserRepository {

    List<UserRecord> findAll();

    List<AdminUserResponse.RoleBrief> findRoles(Long userId);

    UserRecord findById(Long id);

    boolean existsByUsername(String username, Long excludeId);

    boolean existsEnabledDepartment(Long departmentId);

    List<Long> findAssignableRoleIds(List<Long> roleIds);

    UserRecord create(UserMutation mutation, String passwordHash);

    UserRecord update(Long id, UserMutation mutation);

    void softDelete(List<Long> ids);

    void updateStatus(Long id, Integer status);

    void updatePasswordHash(Long id, String passwordHash);

    void updatePositionName(List<Long> ids, String positionName);

    void replaceRoles(List<Long> userIds, List<Long> roleIds);
}
