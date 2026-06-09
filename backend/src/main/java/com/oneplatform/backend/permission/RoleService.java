package com.oneplatform.backend.permission;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.oneplatform.backend.common.BusinessException;
import com.oneplatform.backend.common.PageResponse;

import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RoleRepository repository;

    public RoleService(RoleRepository repository) {
        this.repository = repository;
    }

    public PageResponse<RoleResponse> list(RoleQuery query) {
        RoleQuery safeQuery = normalizeQuery(query);
        List<RoleRecord> filtered = repository.findRoles().stream()
                .filter(role -> matches(role, safeQuery))
                .sorted(Comparator.comparing(RoleRecord::sort, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(RoleRecord::id))
                .toList();
        long from = Math.max(0, (safeQuery.page() - 1) * safeQuery.pageSize());
        long to = Math.min(filtered.size(), from + safeQuery.pageSize());
        List<RoleResponse> list = from >= filtered.size()
                ? List.of()
                : filtered.subList((int) from, (int) to).stream().map(this::map).toList();
        return new PageResponse<>(list, filtered.size(), safeQuery.page(), safeQuery.pageSize());
    }

    public RoleResponse create(RoleSaveRequest request) {
        RoleMutation mutation = normalizeForSave(request);
        if (repository.existsRoleCode(mutation.code(), null)) {
            throw new BusinessException(400, "角色代码已存在");
        }
        validateMenuIds(mutation.menuIds());
        return map(repository.createRole(mutation));
    }

    public RoleResponse update(Long id, RoleSaveRequest request) {
        requireRole(id);
        RoleMutation mutation = normalizeForSave(request);
        if (repository.existsRoleCode(mutation.code(), id)) {
            throw new BusinessException(400, "角色代码已存在");
        }
        validateMenuIds(mutation.menuIds());
        return map(repository.updateRole(id, mutation));
    }

    public void delete(Long id) {
        RoleRecord role = requireRole(id);
        if (role.isSystem() != null && role.isSystem() == 1) {
            throw new BusinessException(400, "内置角色不可删除");
        }
        repository.softDeleteRole(id);
    }

    public void updateStatus(Long id, RoleStatusRequest request) {
        requireRole(id);
        repository.updateRoleStatus(id, normalizeStatus(request == null ? null : request.status()));
    }

    public List<Long> getMenus(Long id) {
        requireRole(id);
        return repository.findRoleMenuIds(id);
    }

    public void updateMenus(Long id, RoleMenusRequest request) {
        requireRole(id);
        List<Long> menuIds = normalizeIds(request == null ? null : request.menuIds());
        validateMenuIds(menuIds);
        repository.replaceRoleMenus(id, menuIds);
    }

    public void updatePermissions(Long id, RoleMenusRequest request) {
        updateMenus(id, request);
    }

    private RoleResponse map(RoleRecord role) {
        return new RoleResponse(
                role.id(),
                role.code(),
                role.name(),
                role.description(),
                repository.findRoleMenuIds(role.id()),
                repository.countRoleUsers(role.id()),
                role.sort(),
                role.status(),
                format(role.createdAt()),
                format(role.updatedAt())
        );
    }

    private RoleRecord requireRole(Long id) {
        if (id == null) {
            throw new BusinessException(404, "角色不存在");
        }
        RoleRecord role = repository.findRoleById(id);
        if (role == null) {
            throw new BusinessException(404, "角色不存在");
        }
        return role;
    }

    private void validateMenuIds(List<Long> menuIds) {
        if (menuIds.isEmpty()) {
            return;
        }
        if (repository.findExistingMenuIds(menuIds).size() != menuIds.size()) {
            throw new BusinessException(400, "存在不可用的菜单权限");
        }
    }

    private static RoleMutation normalizeForSave(RoleSaveRequest request) {
        if (request == null) {
            throw new BusinessException(400, "参数错误");
        }
        return new RoleMutation(
                requiredText(request.code(), "请输入角色代码"),
                requiredText(request.name(), "请输入角色名称"),
                trimToNull(request.description()),
                request.sort() == null ? 100 : request.sort(),
                normalizeStatus(request.status()),
                normalizeIds(request.menuIds())
        );
    }

    private static RoleQuery normalizeQuery(RoleQuery query) {
        if (query == null) {
            return new RoleQuery(null, null, 1, 10);
        }
        long page = query.page() < 1 ? 1 : query.page();
        long pageSize = query.pageSize() < 1 ? 10 : Math.min(query.pageSize(), 100);
        return new RoleQuery(trimToNull(query.name()), query.status(), page, pageSize);
    }

    private static boolean matches(RoleRecord role, RoleQuery query) {
        boolean nameMatched = query.name() == null
                || contains(role.name(), query.name())
                || contains(role.code(), query.name());
        boolean statusMatched = query.status() == null || role.status().equals(query.status());
        return nameMatched && statusMatched;
    }

    private static List<Long> normalizeIds(List<Long> values) {
        if (values == null) {
            return List.of();
        }
        Set<Long> ids = new LinkedHashSet<>();
        for (Long value : values) {
            if (value != null && value > 0) {
                ids.add(value);
            }
        }
        return new ArrayList<>(ids);
    }

    private static Integer normalizeStatus(Integer status) {
        if (status == null) {
            return 1;
        }
        if (status != 0 && status != 1) {
            throw new BusinessException(400, "角色状态参数错误");
        }
        return status;
    }

    private static String requiredText(String value, String message) {
        String text = trimToNull(value);
        if (text == null) {
            throw new BusinessException(400, message);
        }
        return text;
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
