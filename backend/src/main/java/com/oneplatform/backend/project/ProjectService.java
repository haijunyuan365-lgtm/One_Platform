package com.oneplatform.backend.project;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.oneplatform.backend.common.BusinessException;
import com.oneplatform.backend.common.PageResponse;

import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    private static final String DEFAULT_CATEGORY = "内部系统";
    private static final String DEFAULT_STATUS = "未检测";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ProjectRepository repository;

    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    public List<ProjectOptionResponse> options() {
        return repository.findProjects().stream()
                .filter(project -> project.enabled() != null && project.enabled() == 1)
                .sorted(projectComparator())
                .map(project -> new ProjectOptionResponse(project.id(), project.name(), project.shortName()))
                .toList();
    }

    public PageResponse<ProjectResponse> list(ProjectQuery query) {
        ProjectQuery safeQuery = normalizeQuery(query);
        List<ProjectRecord> filtered = repository.findProjects().stream()
                .filter(project -> matches(project, safeQuery))
                .sorted(projectComparator())
                .toList();
        long from = Math.max(0, (safeQuery.page() - 1) * safeQuery.pageSize());
        long to = Math.min(filtered.size(), from + safeQuery.pageSize());
        List<ProjectResponse> list = from >= filtered.size()
                ? List.of()
                : filtered.subList((int) from, (int) to).stream().map(this::map).toList();
        return new PageResponse<>(list, filtered.size(), safeQuery.page(), safeQuery.pageSize());
    }

    public ProjectResponse create(ProjectSaveRequest request) {
        ProjectMutation mutation = normalizeForSave(request);
        if (repository.existsName(mutation.name(), null)) {
            throw new BusinessException(400, "项目名称已存在");
        }
        return map(repository.create(mutation));
    }

    public ProjectResponse update(Long id, ProjectSaveRequest request) {
        requireProject(id);
        ProjectMutation mutation = normalizeForSave(request);
        if (repository.existsName(mutation.name(), id)) {
            throw new BusinessException(400, "项目名称已存在");
        }
        return map(repository.update(id, mutation));
    }

    public void delete(Long id) {
        requireProject(id);
        if (repository.countRelations(id) > 0) {
            throw new BusinessException(400, "该项目存在关联数据，不可删除");
        }
        repository.softDelete(id);
    }

    public void updateEnabled(Long id, ProjectEnabledRequest request) {
        requireProject(id);
        repository.updateEnabled(id, normalizeEnabled(request == null ? null : request.enabled()));
    }

    private ProjectResponse map(ProjectRecord project) {
        return new ProjectResponse(
                project.id(),
                project.name(),
                project.shortName(),
                project.logoUrl(),
                project.category(),
                splitTags(project.tags()),
                project.description(),
                project.maintainerId(),
                project.maintainerName(),
                project.sort(),
                project.enabled(),
                project.status(),
                format(project.lastCheckTime()),
                project.responseTime(),
                project.abnormalReason(),
                format(project.createdAt()),
                format(project.updatedAt())
        );
    }

    private ProjectRecord requireProject(Long id) {
        if (id == null) {
            throw new BusinessException(404, "项目不存在");
        }
        ProjectRecord project = repository.findById(id);
        if (project == null) {
            throw new BusinessException(404, "项目不存在");
        }
        return project;
    }

    private static ProjectMutation normalizeForSave(ProjectSaveRequest request) {
        if (request == null) {
            throw new BusinessException(400, "参数错误");
        }
        String name = requiredText(request.name(), "请输入项目名称");
        return new ProjectMutation(
                name,
                defaultText(request.shortName(), name),
                null,
                trimToNull(request.logo()),
                defaultText(request.category(), DEFAULT_CATEGORY),
                joinTags(request.tags()),
                trimToNull(request.description()),
                request.maintainerId(),
                trimToNull(request.maintainer()),
                request.sort() == null ? 100 : request.sort(),
                normalizeEnabled(request.enabled()),
                defaultText(request.status(), DEFAULT_STATUS)
        );
    }

    private static ProjectQuery normalizeQuery(ProjectQuery query) {
        if (query == null) {
            return new ProjectQuery(null, null, null, null, null, 1, 10);
        }
        long page = query.page() < 1 ? 1 : query.page();
        long pageSize = query.pageSize() < 1 ? 10 : Math.min(query.pageSize(), 100);
        return new ProjectQuery(
                trimToNull(query.keyword()),
                trimToNull(query.category()),
                trimToNull(query.maintainer()),
                query.enabled(),
                trimToNull(query.status()),
                page,
                pageSize
        );
    }

    private static boolean matches(ProjectRecord project, ProjectQuery query) {
        boolean keywordMatched = query.keyword() == null
                || contains(project.name(), query.keyword())
                || contains(project.shortName(), query.keyword())
                || contains(project.description(), query.keyword())
                || contains(project.tags(), query.keyword());
        boolean categoryMatched = query.category() == null || query.category().equals(project.category());
        boolean maintainerMatched = query.maintainer() == null || contains(project.maintainerName(), query.maintainer());
        boolean enabledMatched = query.enabled() == null || query.enabled().equals(project.enabled());
        boolean statusMatched = query.status() == null || query.status().equals(project.status());
        return keywordMatched && categoryMatched && maintainerMatched && enabledMatched && statusMatched;
    }

    private static Comparator<ProjectRecord> projectComparator() {
        return Comparator.comparing(ProjectRecord::sort, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(ProjectRecord::id);
    }

    private static Integer normalizeEnabled(Integer enabled) {
        if (enabled == null) {
            return 1;
        }
        if (enabled != 0 && enabled != 1) {
            throw new BusinessException(400, "项目启用状态参数错误");
        }
        return enabled;
    }

    private static String joinTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        Set<String> normalized = new LinkedHashSet<>();
        for (String tag : tags) {
            String value = trimToNull(tag);
            if (value != null) {
                normalized.add(value);
            }
        }
        return normalized.isEmpty() ? null : String.join(",", normalized);
    }

    private static List<String> splitTags(String tags) {
        String value = trimToNull(tags);
        if (value == null) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
                .map(ProjectService::trimToNull)
                .filter(tag -> tag != null)
                .toList();
    }

    private static String requiredText(String value, String message) {
        String text = trimToNull(value);
        if (text == null) {
            throw new BusinessException(400, message);
        }
        return text;
    }

    private static String defaultText(String value, String defaultValue) {
        String text = trimToNull(value);
        return text == null ? defaultValue : text;
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
