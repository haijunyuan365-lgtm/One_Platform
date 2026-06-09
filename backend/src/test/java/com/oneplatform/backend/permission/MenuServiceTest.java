package com.oneplatform.backend.permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oneplatform.backend.common.BusinessException;

import org.junit.jupiter.api.Test;

class MenuServiceTest {

    @Test
    void listBuildsTreeAndKeepsAncestorsWhenChildMatches() {
        InMemoryMenuRepository repository = new InMemoryMenuRepository();
        MenuService service = new MenuService(repository);

        List<MenuResponse> tree = service.list(new MenuQuery("用户", null));

        assertThat(tree).singleElement().satisfies(root -> {
            assertThat(root.title()).isEqualTo("系统管理");
            assertThat(root.children()).singleElement()
                    .satisfies(child -> assertThat(child.title()).isEqualTo("用户管理"));
        });
    }

    @Test
    void createRejectsDuplicatePermissionAndInvalidParent() {
        MenuService service = new MenuService(new InMemoryMenuRepository());

        assertThatThrownBy(() -> service.create(newMenu("organization:user:view", 1L)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("权限标识已存在");
        assertThatThrownBy(() -> service.create(newMenu("audit:log:view", 999L)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("上级菜单不存在");
    }

    @Test
    void createUpdateAndStatusReturnLatestTreeItem() {
        InMemoryMenuRepository repository = new InMemoryMenuRepository();
        MenuService service = new MenuService(repository);

        MenuResponse created = service.create(newMenu("audit:log:view", 4L));
        MenuResponse updated = service.update(created.id(), new MenuSaveRequest(
                4L,
                "AuditLog",
                "审计日志",
                "menu",
                "/audit/log",
                "@/views/audit/log/index.vue",
                "audit:log:view",
                "List",
                42,
                0
        ));
        service.updateStatus(created.id(), new MenuStatusRequest(1));

        assertThat(updated.title()).isEqualTo("审计日志");
        assertThat(repository.menus.get(created.id()).status()).isEqualTo(1);
    }

    @Test
    void deleteRejectsMenuWithChildrenOrRoleReference() {
        InMemoryMenuRepository repository = new InMemoryMenuRepository();
        MenuService service = new MenuService(repository);

        assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("存在子菜单，不能删除");
        assertThatThrownBy(() -> service.delete(21L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("菜单已分配给角色，不能删除");

        service.delete(12L);

        assertThat(repository.deletedMenuIds).containsExactly(12L);
    }

    private static MenuSaveRequest newMenu(String permission, Long parentId) {
        return new MenuSaveRequest(
                parentId,
                "AuditLog",
                "审计日志",
                "menu",
                "/audit/log",
                "@/views/audit/log/index.vue",
                permission,
                "List",
                42,
                1
        );
    }

    static final class InMemoryMenuRepository implements MenuRepository {

        private final Map<Long, MenuRecord> menus = new HashMap<>();
        private final List<Long> deletedMenuIds = new ArrayList<>();
        private long nextMenuId = 100L;

        InMemoryMenuRepository() {
            LocalDateTime now = LocalDateTime.of(2026, 6, 5, 9, 0);
            menus.put(1L, new MenuRecord(1L, null, "SystemManagement", "系统管理", "directory", "/organization", null, "system:view", "Setting", 1, 1, now, now));
            menus.put(11L, new MenuRecord(11L, 1L, "UserManagement", "用户管理", "menu", "/organization/user", "@/views/organization/user/index.vue", "organization:user:view", "User", 1, 1, now, now));
            menus.put(12L, new MenuRecord(12L, 1L, "DepartmentManagement", "部门管理", "menu", "/organization/department", "@/views/organization/department/index.vue", "organization:department:view", "OfficeBuilding", 2, 1, now, now));
            menus.put(4L, new MenuRecord(4L, null, "AuditManagement", "权限与审计", "directory", "/audit", null, "audit:view", "Operation", 4, 1, now, now));
            menus.put(21L, new MenuRecord(21L, 4L, "ProjectPermission", "项目权限配置", "menu", "/audit/project-permission", "@/views/audit/project-permission/index.vue", "audit:project-permission:view", "Lock", 1, 1, now, now));
        }

        @Override
        public List<MenuRecord> findMenus() {
            return menus.entrySet().stream()
                    .filter(entry -> !deletedMenuIds.contains(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .toList();
        }

        @Override
        public MenuRecord findMenuById(Long id) {
            if (deletedMenuIds.contains(id)) {
                return null;
            }
            return menus.get(id);
        }

        @Override
        public boolean existsMenuPermission(String permission, Long excludeId) {
            return menus.values().stream()
                    .anyMatch(menu -> permission.equals(menu.permission())
                            && !menu.id().equals(excludeId)
                            && !deletedMenuIds.contains(menu.id()));
        }

        @Override
        public MenuRecord createMenu(MenuMutation mutation) {
            Long id = nextMenuId++;
            MenuRecord record = toRecord(id, mutation);
            menus.put(id, record);
            return record;
        }

        @Override
        public MenuRecord updateMenu(Long id, MenuMutation mutation) {
            MenuRecord record = toRecord(id, mutation);
            menus.put(id, record);
            return record;
        }

        @Override
        public void softDeleteMenu(Long id) {
            deletedMenuIds.add(id);
        }

        @Override
        public void updateMenuStatus(Long id, Integer status) {
            MenuRecord old = menus.get(id);
            menus.put(id, new MenuRecord(old.id(), old.parentId(), old.name(), old.title(), old.type(), old.path(), old.component(), old.permission(), old.icon(), old.sort(), status, old.createdAt(), old.updatedAt()));
        }

        @Override
        public long countChildren(Long id) {
            return menus.values().stream().filter(menu -> id.equals(menu.parentId())).count();
        }

        @Override
        public long countRoleReferences(Long id) {
            return id.equals(21L) ? 1L : 0L;
        }

        private MenuRecord toRecord(Long id, MenuMutation mutation) {
            LocalDateTime now = LocalDateTime.of(2026, 6, 5, 10, 0);
            return new MenuRecord(id, mutation.parentId(), mutation.name(), mutation.title(), mutation.type(), mutation.path(), mutation.component(), mutation.permission(), mutation.icon(), mutation.sort(), mutation.status(), now, now);
        }
    }
}
