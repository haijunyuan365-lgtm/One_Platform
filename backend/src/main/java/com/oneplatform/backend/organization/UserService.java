package com.oneplatform.backend.organization;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.oneplatform.backend.common.BusinessException;
import com.oneplatform.backend.common.PageResponse;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String DEFAULT_INITIAL_PASSWORD = "OnePlatform2026";
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d).{8,30}$";

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public PageResponse<AdminUserResponse> list(UserQuery query) {
        UserQuery safeQuery = normalize(query);
        List<UserRecord> filtered = repository.findAll().stream()
                .filter(user -> matches(user, safeQuery))
                .sorted(Comparator.comparing(UserRecord::id))
                .toList();
        long from = Math.max(0, (safeQuery.page() - 1) * safeQuery.pageSize());
        long to = Math.min(filtered.size(), from + safeQuery.pageSize());
        List<AdminUserResponse> pageList = from >= filtered.size()
                ? List.of()
                : filtered.subList((int) from, (int) to).stream().map(this::map).toList();
        return new PageResponse<>(pageList, filtered.size(), safeQuery.page(), safeQuery.pageSize());
    }

    public AdminUserResponse create(UserSaveRequest request) {
        UserMutation mutation = normalizeForCreate(request);
        if (repository.existsByUsername(mutation.username(), null)) {
            throw new BusinessException(400, "用户账号已存在");
        }
        validateDepartment(mutation.departmentId());
        validateAssignableRoles(mutation.roleIds());
        String rawPassword = request == null ? null : trimToNull(request.password());
        if (rawPassword == null) {
            rawPassword = DEFAULT_INITIAL_PASSWORD;
        }
        validatePassword(rawPassword);
        return map(repository.create(mutation, passwordEncoder.encode(rawPassword)));
    }

    public AdminUserResponse update(Long id, UserSaveRequest request) {
        UserRecord existing = requireUser(id);
        UserMutation mutation = normalizeForUpdate(existing, request);
        validateDepartment(mutation.departmentId());
        validateAssignableRoles(mutation.roleIds());
        return map(repository.update(id, mutation));
    }

    public void delete(Long id) {
        requireUser(id);
        repository.softDelete(List.of(id));
    }

    public void batchDelete(BatchIdsRequest request) {
        List<Long> ids = normalizeIds(request == null ? null : request.ids());
        ids.forEach(this::requireUser);
        repository.softDelete(ids);
    }

    public void updateStatus(Long id, UserStatusRequest request) {
        requireUser(id);
        Integer status = request == null ? null : request.status();
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(400, "用户状态参数错误");
        }
        repository.updateStatus(id, status);
    }

    public void resetPassword(Long id, ResetPasswordRequest request) {
        requireUser(id);
        String password = request == null ? null : trimToNull(request.password());
        validatePassword(password);
        repository.updatePasswordHash(id, passwordEncoder.encode(password));
    }

    public void batchSetPosition(BatchSetPositionRequest request) {
        List<Long> ids = normalizeIds(request == null ? null : request.ids());
        ids.forEach(this::requireUser);
        String positionName = trimToNull(request == null ? null : request.positionName());
        if (positionName == null) {
            throw new BusinessException(400, "请输入岗位");
        }
        repository.updatePositionName(ids, positionName);
    }

    public void batchSetRole(BatchSetRoleRequest request) {
        List<Long> userIds = normalizeIds(request == null ? null : request.ids());
        List<Long> roleIds = normalizeIds(request == null ? null : request.roleIds());
        userIds.forEach(this::requireUser);
        validateAssignableRoles(roleIds);
        repository.replaceRoles(userIds, roleIds);
    }

    private AdminUserResponse map(UserRecord user) {
        return new AdminUserResponse(
                user.id(),
                user.username(),
                user.realName(),
                user.nickname(),
                user.avatar(),
                user.email(),
                user.phone(),
                user.departmentId(),
                user.departmentName(),
                null,
                user.positionName(),
                user.status(),
                format(user.createdAt()),
                format(user.updatedAt()),
                format(user.lastLoginTime()),
                user.lastLoginIp(),
                user.remark(),
                repository.findRoles(user.id())
        );
    }

    private UserRecord requireUser(Long id) {
        if (id == null) {
            throw new BusinessException(400, "用户不存在");
        }
        UserRecord user = repository.findById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    private static boolean matches(UserRecord user, UserQuery query) {
        boolean keywordMatched = query.keyword() == null
                || query.keyword().isBlank()
                || contains(user.username(), query.keyword())
                || contains(user.realName(), query.keyword())
                || contains(user.nickname(), query.keyword())
                || contains(user.phone(), query.keyword());
        boolean departmentMatched = query.departmentId() == null || user.departmentId().equals(query.departmentId());
        boolean statusMatched = query.status() == null || user.status().equals(query.status());
        return keywordMatched && departmentMatched && statusMatched;
    }

    private UserMutation normalizeForCreate(UserSaveRequest request) {
        if (request == null) {
            throw new BusinessException(400, "参数错误");
        }
        String username = trimToNull(request.username());
        if (username == null) {
            throw new BusinessException(400, "请输入账号");
        }
        return new UserMutation(
                username,
                requiredText(request.realName(), "请输入姓名"),
                trimToNull(request.nickname()),
                trimToNull(request.avatar()),
                trimToNull(request.email()),
                trimToNull(request.phone()),
                requiredDepartmentId(request.departmentId()),
                trimToNull(request.positionName()),
                normalizeStatus(request.status()),
                trimToNull(request.remark()),
                normalizeRoleIds(request.roleIds())
        );
    }

    private UserMutation normalizeForUpdate(UserRecord existing, UserSaveRequest request) {
        if (request == null) {
            throw new BusinessException(400, "参数错误");
        }
        return new UserMutation(
                existing.username(),
                requiredText(request.realName(), "请输入姓名"),
                trimToNull(request.nickname()),
                trimToNull(request.avatar()),
                trimToNull(request.email()),
                trimToNull(request.phone()),
                requiredDepartmentId(request.departmentId()),
                trimToNull(request.positionName()),
                normalizeStatus(request.status() == null ? existing.status() : request.status()),
                trimToNull(request.remark()),
                normalizeRoleIds(request.roleIds())
        );
    }

    private void validateDepartment(Long departmentId) {
        if (!repository.existsEnabledDepartment(departmentId)) {
            throw new BusinessException(400, "所属部门不可用");
        }
    }

    private void validateAssignableRoles(List<Long> roleIds) {
        if (roleIds.isEmpty()) {
            return;
        }
        List<Long> assignableIds = repository.findAssignableRoleIds(roleIds);
        if (assignableIds.size() != roleIds.size()) {
            throw new BusinessException(400, "存在不可分配的角色");
        }
    }

    private static UserQuery normalize(UserQuery query) {
        if (query == null) {
            return new UserQuery(null, null, null, 1, 10);
        }
        long page = query.page() < 1 ? 1 : query.page();
        long pageSize = query.pageSize() < 1 ? 10 : Math.min(query.pageSize(), 100);
        return new UserQuery(trimToNull(query.keyword()), query.departmentId(), query.status(), page, pageSize);
    }

    private static List<Long> normalizeRoleIds(List<Long> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream()
                .filter(value -> value != null && value > 0)
                .distinct()
                .toList();
    }

    private static List<Long> normalizeIds(List<Long> values) {
        if (values == null) {
            throw new BusinessException(400, "请选择用户");
        }
        Set<Long> ids = new LinkedHashSet<>();
        for (Long value : values) {
            if (value != null && value > 0) {
                ids.add(value);
            }
        }
        if (ids.isEmpty()) {
            throw new BusinessException(400, "请选择用户");
        }
        return new ArrayList<>(ids);
    }

    private static Integer normalizeStatus(Integer status) {
        if (status == null) {
            return 1;
        }
        if (status != 0 && status != 1) {
            throw new BusinessException(400, "用户状态参数错误");
        }
        return status;
    }

    private static Long requiredDepartmentId(Long departmentId) {
        if (departmentId == null || departmentId <= 0) {
            throw new BusinessException(400, "请选择部门");
        }
        return departmentId;
    }

    private static String requiredText(String value, String message) {
        String text = trimToNull(value);
        if (text == null) {
            throw new BusinessException(400, message);
        }
        return text;
    }

    private static void validatePassword(String password) {
        if (password == null || !password.matches(PASSWORD_PATTERN)) {
            throw new BusinessException(400, "密码需包含字母和数字，长度不少于8位");
        }
    }

    private static String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static boolean contains(String value, String keyword) {
        return value != null && value.contains(keyword);
    }

    private static String format(LocalDateTime value) {
        return value == null ? null : value.format(DATE_TIME_FORMATTER);
    }
}
