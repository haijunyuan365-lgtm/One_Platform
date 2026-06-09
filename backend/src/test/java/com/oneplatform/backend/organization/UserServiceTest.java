package com.oneplatform.backend.organization;

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
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceTest {

    @Test
    void listFiltersByKeywordAndReturnsDepartmentAndRoles() {
        UserService service = newService(new InMemoryUserRepository());

        PageResponse<AdminUserResponse> response = service.list(new UserQuery("lisi", null, null, 1, 10));

        assertThat(response.total()).isEqualTo(1);
        assertThat(response.list()).singleElement().satisfies(user -> {
            assertThat(user.username()).isEqualTo("lisi");
            assertThat(user.realName()).isEqualTo("李四");
            assertThat(user.departmentName()).isEqualTo("运维部");
            assertThat(user.roles()).extracting(AdminUserResponse.RoleBrief::name).containsExactly("项目管理员");
        });
    }

    @Test
    void listFiltersByDepartmentStatusAndPaginates() {
        UserService service = newService(new InMemoryUserRepository());

        PageResponse<AdminUserResponse> response = service.list(new UserQuery(null, 21L, 1, 1, 1));

        assertThat(response.total()).isEqualTo(2);
        assertThat(response.page()).isEqualTo(1);
        assertThat(response.pageSize()).isEqualTo(1);
        assertThat(response.list()).extracting(AdminUserResponse::username).containsExactly("zhaoliu");
    }

    @Test
    void createUsesDefaultInitialPasswordAndAssignsRoles() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        UserService service = newService(repository);

        AdminUserResponse created = service.create(newUser("newuser"));

        assertThat(created.id()).isEqualTo(6L);
        assertThat(created.username()).isEqualTo("newuser");
        assertThat(created.departmentName()).isEqualTo("研发部");
        assertThat(created.positionName()).isEqualTo("研发工程师");
        assertThat(created.roles()).extracting(AdminUserResponse.RoleBrief::id).containsExactly(4L);
        assertThat(repository.passwordHashes.get(6L)).isEqualTo("{encoded}OnePlatform2026");
    }

    @Test
    void createRejectsDuplicateUsername() {
        UserService service = newService(new InMemoryUserRepository());

        assertThatThrownBy(() -> service.create(newUser("lisi")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("用户账号已存在");
    }

    @Test
    void updateChangesEditableProfileWithoutChangingUsername() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        UserService service = newService(repository);

        AdminUserResponse updated = service.update(2L, new UserSaveRequest(
                "ignored",
                "李四-更新",
                "小李",
                null,
                "new-lisi@one-platform.local",
                "13900139001",
                21L,
                "项目经理",
                1,
                "更新备注",
                null,
                List.of(2L, 4L)
        ));

        assertThat(updated.username()).isEqualTo("lisi");
        assertThat(updated.realName()).isEqualTo("李四-更新");
        assertThat(updated.departmentName()).isEqualTo("研发部");
        assertThat(updated.positionName()).isEqualTo("项目经理");
        assertThat(updated.roles()).extracting(AdminUserResponse.RoleBrief::id).containsExactly(2L, 4L);
    }

    @Test
    void deleteAndBatchDeleteMarkUsersDeleted() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        UserService service = newService(repository);

        service.delete(2L);
        service.batchDelete(new BatchIdsRequest(List.of(3L, 4L)));

        assertThat(repository.deletedUserIds).containsExactlyInAnyOrder(2L, 3L, 4L);
    }

    @Test
    void resetPasswordRejectsWeakPasswordAndStoresHash() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        UserService service = newService(repository);

        assertThatThrownBy(() -> service.resetPassword(2L, new ResetPasswordRequest("weak")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("密码需包含字母和数字，长度不少于8位");

        service.resetPassword(2L, new ResetPasswordRequest("Abcd1234"));

        assertThat(repository.passwordHashes.get(2L)).isEqualTo("{encoded}Abcd1234");
    }

    @Test
    void updateStatusAndBatchPositionPersistEditableFields() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        UserService service = newService(repository);

        service.updateStatus(2L, new UserStatusRequest(0));
        service.batchSetPosition(new BatchSetPositionRequest(List.of(2L, 3L), "交付顾问"));

        assertThat(repository.records.get(2L).status()).isEqualTo(0);
        assertThat(repository.records.get(2L).positionName()).isEqualTo("交付顾问");
        assertThat(repository.records.get(3L).positionName()).isEqualTo("交付顾问");
    }

    @Test
    void batchSetRoleReplacesRoleAssignmentsAndRejectsDisabledRole() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        UserService service = newService(repository);

        service.batchSetRole(new BatchSetRoleRequest(List.of(2L, 3L), List.of(2L, 4L)));

        assertThat(repository.roleAssignments.get(2L)).containsExactly(2L, 4L);
        assertThat(repository.roleAssignments.get(3L)).containsExactly(2L, 4L);
        assertThatThrownBy(() -> service.batchSetRole(new BatchSetRoleRequest(List.of(2L), List.of(99L))))
                .isInstanceOf(BusinessException.class)
                .hasMessage("存在不可分配的角色");
    }

    private static UserService newService(InMemoryUserRepository repository) {
        return new UserService(repository, new RecordingPasswordEncoder());
    }

    private static UserSaveRequest newUser(String username) {
        return new UserSaveRequest(
                username,
                "新用户",
                "新用户",
                null,
                "newuser@one-platform.local",
                "13900139000",
                21L,
                "研发工程师",
                1,
                null,
                null,
                List.of(4L)
        );
    }

    static final class InMemoryUserRepository implements UserRepository {

        private final Map<Long, UserRecord> records = new HashMap<>();
        private final Map<Long, List<Long>> roleAssignments = new HashMap<>();
        private final Map<Long, String> passwordHashes = new HashMap<>();
        private final List<Long> deletedUserIds = new ArrayList<>();
        private long nextId = 6L;

        InMemoryUserRepository() {
            LocalDateTime now = LocalDateTime.of(2026, 6, 5, 9, 0);
            records.put(1L, new UserRecord(1L, "admin", "系统管理员", "管理员", "admin@one-platform.local", "13800138000", 1L, "公司总部", null, "平台负责人", 1, now, now, null, null, null));
            records.put(2L, new UserRecord(2L, "lisi", "李四", "李四", "lisi@one-platform.local", "13800138001", 32L, "运维部", null, "运维负责人", 1, now, now, null, null, null));
            records.put(3L, new UserRecord(3L, "zhaoliu", "赵六", "赵六", "zhaoliu@one-platform.local", "13800138003", 21L, "研发部", null, "研发工程师", 1, now, now, null, null, null));
            records.put(4L, new UserRecord(4L, "wangwu", "王五", "王五", "wangwu@one-platform.local", "13800138002", 21L, "研发部", null, "产品经理", 1, now, now, null, null, null));
            records.put(5L, new UserRecord(5L, "disabled", "停用用户", "停用", "disabled@one-platform.local", "13800138005", 21L, "研发部", null, "员工", 0, now, now, null, null, null));
            roleAssignments.put(1L, List.of(1L));
            roleAssignments.put(2L, List.of(2L));
            roleAssignments.put(3L, List.of(4L));
            roleAssignments.put(4L, List.of(4L));
            roleAssignments.put(5L, List.of(4L));
        }

        @Override
        public List<UserRecord> findAll() {
            return records.entrySet().stream()
                    .filter(entry -> !deletedUserIds.contains(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .toList();
        }

        @Override
        public List<AdminUserResponse.RoleBrief> findRoles(Long userId) {
            return roleAssignments.getOrDefault(userId, List.of()).stream()
                    .map(this::roleBrief)
                    .toList();
        }

        @Override
        public UserRecord findById(Long id) {
            if (deletedUserIds.contains(id)) {
                return null;
            }
            return records.get(id);
        }

        @Override
        public boolean existsByUsername(String username, Long excludeId) {
            return records.values().stream()
                    .anyMatch(user -> user.username().equals(username)
                            && !user.id().equals(excludeId)
                            && !deletedUserIds.contains(user.id()));
        }

        @Override
        public boolean existsEnabledDepartment(Long departmentId) {
            return List.of(1L, 21L, 22L, 31L, 32L).contains(departmentId);
        }

        @Override
        public List<Long> findAssignableRoleIds(List<Long> roleIds) {
            return roleIds.stream().filter(List.of(1L, 2L, 3L, 4L, 5L)::contains).toList();
        }

        @Override
        public UserRecord create(UserMutation mutation, String passwordHash) {
            Long id = nextId++;
            UserRecord record = toRecord(id, mutation, mutation.username(), null);
            records.put(id, record);
            passwordHashes.put(id, passwordHash);
            roleAssignments.put(id, mutation.roleIds());
            return record;
        }

        @Override
        public UserRecord update(Long id, UserMutation mutation) {
            UserRecord old = records.get(id);
            UserRecord record = toRecord(id, mutation, old.username(), old.createdAt());
            records.put(id, record);
            roleAssignments.put(id, mutation.roleIds());
            return record;
        }

        @Override
        public void softDelete(List<Long> ids) {
            deletedUserIds.addAll(ids);
        }

        @Override
        public void updateStatus(Long id, Integer status) {
            UserRecord old = records.get(id);
            records.put(id, new UserRecord(old.id(), old.username(), old.realName(), old.nickname(), old.email(), old.phone(), old.departmentId(), old.departmentName(), old.avatar(), old.positionName(), status, old.createdAt(), old.updatedAt(), old.lastLoginTime(), old.lastLoginIp(), old.remark()));
        }

        @Override
        public void updatePasswordHash(Long id, String passwordHash) {
            passwordHashes.put(id, passwordHash);
        }

        @Override
        public void updatePositionName(List<Long> ids, String positionName) {
            ids.forEach(id -> {
                UserRecord old = records.get(id);
                records.put(id, new UserRecord(old.id(), old.username(), old.realName(), old.nickname(), old.email(), old.phone(), old.departmentId(), old.departmentName(), old.avatar(), positionName, old.status(), old.createdAt(), old.updatedAt(), old.lastLoginTime(), old.lastLoginIp(), old.remark()));
            });
        }

        @Override
        public void replaceRoles(List<Long> userIds, List<Long> roleIds) {
            userIds.forEach(userId -> roleAssignments.put(userId, roleIds));
        }

        private UserRecord toRecord(Long id, UserMutation mutation, String username, LocalDateTime createdAt) {
            LocalDateTime now = LocalDateTime.of(2026, 6, 5, 10, 0);
            return new UserRecord(
                    id,
                    username,
                    mutation.realName(),
                    mutation.nickname(),
                    mutation.email(),
                    mutation.phone(),
                    mutation.departmentId(),
                    departmentName(mutation.departmentId()),
                    mutation.avatar(),
                    mutation.positionName(),
                    mutation.status(),
                    createdAt == null ? now : createdAt,
                    now,
                    null,
                    null,
                    mutation.remark()
            );
        }

        private String departmentName(Long id) {
            return switch (id.intValue()) {
                case 1 -> "公司总部";
                case 21 -> "研发部";
                case 22 -> "产品部";
                case 31 -> "交付部";
                case 32 -> "运维部";
                default -> null;
            };
        }

        private AdminUserResponse.RoleBrief roleBrief(Long roleId) {
            return switch (roleId.intValue()) {
                case 1 -> new AdminUserResponse.RoleBrief(1L, "超级管理员", "super_admin");
                case 2 -> new AdminUserResponse.RoleBrief(2L, "项目管理员", "project_admin");
                case 3 -> new AdminUserResponse.RoleBrief(3L, "部门管理员", "dept_admin");
                case 4 -> new AdminUserResponse.RoleBrief(4L, "普通员工", "employee");
                case 5 -> new AdminUserResponse.RoleBrief(5L, "访客用户", "guest");
                default -> throw new IllegalArgumentException("unknown role");
            };
        }
    }

    static final class RecordingPasswordEncoder implements PasswordEncoder {

        @Override
        public String encode(CharSequence rawPassword) {
            return "{encoded}" + rawPassword;
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return encodedPassword.equals(encode(rawPassword));
        }
    }
}
