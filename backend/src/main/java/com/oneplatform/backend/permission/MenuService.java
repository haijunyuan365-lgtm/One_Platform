package com.oneplatform.backend.permission;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.oneplatform.backend.common.BusinessException;

import org.springframework.stereotype.Service;

@Service
public class MenuService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Set<String> MENU_TYPES = Set.of("directory", "menu", "button");

    private final MenuRepository repository;

    public MenuService(MenuRepository repository) {
        this.repository = repository;
    }

    public List<MenuResponse> list(MenuQuery query) {
        MenuQuery safeQuery = normalizeQuery(query);
        List<MenuRecord> sorted = repository.findMenus().stream()
                .sorted(Comparator.comparing(MenuRecord::sort, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(MenuRecord::id))
                .toList();
        Map<Long, List<MenuRecord>> childrenByParent = sorted.stream()
                .filter(menu -> menu.parentId() != null)
                .collect(Collectors.groupingBy(MenuRecord::parentId));
        return sorted.stream()
                .filter(menu -> menu.parentId() == null)
                .map(menu -> buildNode(menu, childrenByParent, safeQuery))
                .filter(Objects::nonNull)
                .toList();
    }

    public MenuResponse create(MenuSaveRequest request) {
        MenuMutation mutation = normalizeForSave(request);
        validateParent(mutation.parentId(), null);
        validatePermission(mutation.permission(), null);
        return map(repository.createMenu(mutation), List.of());
    }

    public MenuResponse update(Long id, MenuSaveRequest request) {
        requireMenu(id);
        MenuMutation mutation = normalizeForSave(request);
        validateParent(mutation.parentId(), id);
        validatePermission(mutation.permission(), id);
        return map(repository.updateMenu(id, mutation), List.of());
    }

    public void delete(Long id) {
        requireMenu(id);
        if (repository.countChildren(id) > 0) {
            throw new BusinessException(400, "存在子菜单，不能删除");
        }
        if (repository.countRoleReferences(id) > 0) {
            throw new BusinessException(400, "菜单已分配给角色，不能删除");
        }
        repository.softDeleteMenu(id);
    }

    public void updateStatus(Long id, MenuStatusRequest request) {
        requireMenu(id);
        repository.updateMenuStatus(id, normalizeStatus(request == null ? null : request.status()));
    }

    private MenuResponse buildNode(MenuRecord menu, Map<Long, List<MenuRecord>> childrenByParent, MenuQuery query) {
        List<MenuResponse> children = childrenByParent.getOrDefault(menu.id(), List.of()).stream()
                .map(child -> buildNode(child, childrenByParent, query))
                .filter(Objects::nonNull)
                .toList();
        if (!matches(menu, query) && children.isEmpty()) {
            return null;
        }
        return map(menu, children);
    }

    private MenuResponse map(MenuRecord menu, List<MenuResponse> children) {
        return new MenuResponse(
                menu.id(),
                menu.parentId(),
                menu.name(),
                menu.title(),
                menu.type(),
                menu.path(),
                menu.component(),
                menu.permission(),
                menu.icon(),
                menu.sort(),
                menu.status(),
                format(menu.createdAt()),
                format(menu.updatedAt()),
                children
        );
    }

    private MenuRecord requireMenu(Long id) {
        if (id == null) {
            throw new BusinessException(404, "菜单不存在");
        }
        MenuRecord menu = repository.findMenuById(id);
        if (menu == null) {
            throw new BusinessException(404, "菜单不存在");
        }
        return menu;
    }

    private void validateParent(Long parentId, Long selfId) {
        if (parentId == null || parentId == 0) {
            return;
        }
        if (selfId != null && parentId.equals(selfId)) {
            throw new BusinessException(400, "上级菜单不能选择自身");
        }
        if (repository.findMenuById(parentId) == null) {
            throw new BusinessException(400, "上级菜单不存在");
        }
    }

    private void validatePermission(String permission, Long excludeId) {
        if (permission != null && repository.existsMenuPermission(permission, excludeId)) {
            throw new BusinessException(400, "权限标识已存在");
        }
    }

    private static MenuMutation normalizeForSave(MenuSaveRequest request) {
        if (request == null) {
            throw new BusinessException(400, "参数错误");
        }
        String type = requiredText(request.type(), "请选择菜单类型");
        if (!MENU_TYPES.contains(type)) {
            throw new BusinessException(400, "菜单类型参数错误");
        }
        return new MenuMutation(
                request.parentId() == null || request.parentId() == 0 ? null : request.parentId(),
                type.equals("button") ? trimToNull(request.name()) : requiredText(request.name(), "请输入路由名称"),
                requiredText(request.title(), "请输入菜单名称"),
                type,
                type.equals("button") ? trimToNull(request.path()) : requiredText(request.path(), "请输入路由路径"),
                trimToNull(request.component()),
                requiredText(request.permission(), "请输入权限标识"),
                trimToNull(request.icon()),
                request.sort() == null ? 100 : request.sort(),
                normalizeStatus(request.status())
        );
    }

    private static MenuQuery normalizeQuery(MenuQuery query) {
        if (query == null) {
            return new MenuQuery(null, null);
        }
        return new MenuQuery(trimToNull(query.name()), query.status());
    }

    private static boolean matches(MenuRecord menu, MenuQuery query) {
        boolean nameMatched = query.name() == null
                || contains(menu.title(), query.name())
                || contains(menu.name(), query.name())
                || contains(menu.permission(), query.name());
        boolean statusMatched = query.status() == null || menu.status().equals(query.status());
        return nameMatched && statusMatched;
    }

    private static Integer normalizeStatus(Integer status) {
        if (status == null) {
            return 1;
        }
        if (status != 0 && status != 1) {
            throw new BusinessException(400, "菜单状态参数错误");
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
