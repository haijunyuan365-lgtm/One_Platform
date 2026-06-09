package com.oneplatform.backend.permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oneplatform.backend.common.BusinessException;
import com.oneplatform.backend.common.PageResponse;

import org.junit.jupiter.api.Test;

class RoleServiceTest {

    @Test
    void listFiltersByNameAndReturnsMenuIdsAndUserCount() {
        InMemoryPermissionRepository repository = new InMemoryPermissionRepository();
        RoleService service = new RoleService(repository);

        PageResponse<RoleResponse> response = service.list(new RoleQuery("项目", 1, 1, 10));

        assertThat(response.total()).isEqualTo(1);
        assertThat(response.list()).singleElement().satisfies(role -> {
            assertThat(role.code()).isEqualTo("project_admin");
            assertThat(role.menuIds()).containsExactly(2L, 21L);
            assertThat(role.userCount()).isEqualTo(2L);
        });
    }

    @Test
    void createRejectsDuplicateCodeAndAssignsMenus() {
        InMemoryPermissionRepository repository = new InMemoryPermissionRepository();
        RoleService service = new RoleService(repository);

        assertThatThrownBy(() -> service.create(newRole("project_admin")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("角色代码已存在");

        RoleResponse created = service.create(newRole("auditor"));

        assertThat(created.id()).isEqualTo(6L);
        assertThat(created.code()).isEqualTo("auditor");
        assertThat(created.menuIds()).containsExactly(4L, 41L);
        assertThat(repository.roleMenuIds.get(6L)).containsExactly(4L, 41L);
    }

    @Test
    void updateChangesEditableFieldsAndReplacesMenus() {
        InMemoryPermissionRepository repository = new InMemoryPermissionRepository();
        RoleService service = new RoleService(repository);

        RoleResponse updated = service.update(2L, new RoleSaveRequest(
                "project_admin",
                "项目管理员-更新",
                "更新描述",
                20,
                0,
                List.of(3L, 31L)
        ));

        assertThat(updated.name()).isEqualTo("项目管理员-更新");
        assertThat(updated.status()).isEqualTo(0);
        assertThat(updated.menuIds()).containsExactly(3L, 31L);
    }

    @Test
    void deleteRejectsSystemRoleAndSoftDeletesNormalRole() {
        InMemoryPermissionRepository repository = new InMemoryPermissionRepository();
        RoleService service = new RoleService(repository);

        assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("内置角色不可删除");

        RoleResponse created = service.create(newRole("auditor"));
        service.delete(created.id());

        assertThat(repository.deletedRoleIds).containsExactly(created.id());
    }

    @Test
    void updateStatusAndMenusValidateExistingData() {
        InMemoryPermissionRepository repository = new InMemoryPermissionRepository();
        RoleService service = new RoleService(repository);

        service.updateStatus(2L, new RoleStatusRequest(0));
        service.updateMenus(2L, new RoleMenusRequest(List.of(3L, 31L)));

        assertThat(repository.roles.get(2L).status()).isEqualTo(0);
        assertThat(service.getMenus(2L)).containsExactly(3L, 31L);
        assertThatThrownBy(() -> service.updateMenus(2L, new RoleMenusRequest(List.of(999L))))
                .isInstanceOf(BusinessException.class)
                .hasMessage("存在不可用的菜单权限");
    }

    private static RoleSaveRequest newRole(String code) {
        return new RoleSaveRequest(code, "审计员", "审计查看", 9, 1, List.of(4L, 41L));
    }

    static final class InMemoryPermissionRepository implements RoleRepository {

        private final Map<Long, RoleRecord> roles = new HashMap<>();
        private final Map<Long, List<Long>> roleMenuIds = new HashMap<>();
        private final Map<Long, Long> roleUserCounts = new HashMap<>();
        private final List<Long> deletedRoleIds = new ArrayList<>();
        private long nextRoleId = 6L;

        InMemoryPermissionRepository() {
            LocalDateTime now = LocalDateTime.of(2026, 6, 5, 9, 0);
            roles.put(1L, new RoleRecord(1L, "super_admin", "超级管理员", "平台最高权限角色", 1, 1, 1, now, now));
            roles.put(2L, new RoleRecord(2L, "project_admin", "项目管理员", "负责项目维护", 2, 1, 1, now, now));
            roles.put(3L, new RoleRecord(3L, "dept_admin", "部门管理员", "负责部门协调", 3, 1, 1, now, now));
            roleMenuIds.put(1L, List.of(1L, 11L, 12L, 2L, 21L));
            roleMenuIds.put(2L, List.of(2L, 21L));
            roleMenuIds.put(3L, List.of(3L, 31L));
            roleUserCounts.put(1L, 1L);
            roleUserCounts.put(2L, 2L);
            roleUserCounts.put(3L, 0L);
        }

        @Override
        public List<RoleRecord> findRoles() {
            return roles.entrySet().stream()
                    .filter(entry -> !deletedRoleIds.contains(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .toList();
        }

        @Override
        public RoleRecord findRoleById(Long id) {
            if (deletedRoleIds.contains(id)) {
                return null;
            }
            return roles.get(id);
        }

        @Override
        public boolean existsRoleCode(String code, Long excludeId) {
            return roles.values().stream()
                    .anyMatch(role -> role.code().equals(code)
                            && !role.id().equals(excludeId)
                            && !deletedRoleIds.contains(role.id()));
        }

        @Override
        public RoleRecord createRole(RoleMutation mutation) {
            Long id = nextRoleId++;
            RoleRecord record = toRecord(id, mutation, 0);
            roles.put(id, record);
            replaceRoleMenus(id, mutation.menuIds());
            return record;
        }

        @Override
        public RoleRecord updateRole(Long id, RoleMutation mutation) {
            RoleRecord old = roles.get(id);
            RoleRecord record = toRecord(id, mutation, old.isSystem());
            roles.put(id, record);
            replaceRoleMenus(id, mutation.menuIds());
            return record;
        }

        @Override
        public void softDeleteRole(Long id) {
            deletedRoleIds.add(id);
        }

        @Override
        public void updateRoleStatus(Long id, Integer status) {
            RoleRecord old = roles.get(id);
            roles.put(id, new RoleRecord(old.id(), old.code(), old.name(), old.description(), old.sort(), status, old.isSystem(), old.createdAt(), old.updatedAt()));
        }

        @Override
        public List<Long> findRoleMenuIds(Long roleId) {
            return roleMenuIds.getOrDefault(roleId, List.of());
        }

        @Override
        public void replaceRoleMenus(Long roleId, List<Long> menuIds) {
            roleMenuIds.put(roleId, menuIds);
        }

        @Override
        public long countRoleUsers(Long roleId) {
            return roleUserCounts.getOrDefault(roleId, 0L);
        }

        @Override
        public List<Long> findExistingMenuIds(List<Long> menuIds) {
            return menuIds.stream().filter(List.of(1L, 2L, 3L, 4L, 11L, 12L, 21L, 31L, 41L)::contains).toList();
        }

        private RoleRecord toRecord(Long id, RoleMutation mutation, Integer isSystem) {
            LocalDateTime now = LocalDateTime.of(2026, 6, 5, 10, 0);
            return new RoleRecord(id, mutation.code(), mutation.name(), mutation.description(), mutation.sort(), mutation.status(), isSystem, now, now);
        }
    }
}
