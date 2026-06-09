package com.oneplatform.backend.permission;

import java.util.List;

public interface RoleRepository {

    List<RoleRecord> findRoles();

    RoleRecord findRoleById(Long id);

    boolean existsRoleCode(String code, Long excludeId);

    RoleRecord createRole(RoleMutation mutation);

    RoleRecord updateRole(Long id, RoleMutation mutation);

    void softDeleteRole(Long id);

    void updateRoleStatus(Long id, Integer status);

    List<Long> findRoleMenuIds(Long roleId);

    void replaceRoleMenus(Long roleId, List<Long> menuIds);

    long countRoleUsers(Long roleId);

    List<Long> findExistingMenuIds(List<Long> menuIds);
}
