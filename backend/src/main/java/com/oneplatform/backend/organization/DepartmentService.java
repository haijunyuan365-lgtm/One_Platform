package com.oneplatform.backend.organization;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import com.oneplatform.backend.common.BusinessException;
import com.oneplatform.backend.common.PageResponse;

import org.springframework.stereotype.Service;

@Service
public class DepartmentService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final DepartmentRepository repository;

    public DepartmentService(DepartmentRepository repository) {
        this.repository = repository;
    }

    public PageResponse<DepartmentResponse> list(DepartmentQuery query) {
        List<DepartmentRecord> departments = repository.findAll().stream()
                .sorted(Comparator.comparing(DepartmentRecord::sort, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(DepartmentRecord::id))
                .toList();
        List<DepartmentResponse> roots = buildTree(departments, null, query, 1);
        return new PageResponse<>(roots, roots.size(), 1, roots.size());
    }

    public boolean updateStatus(Long id, DepartmentStatusRequest request) {
        DepartmentRecord department = repository.findById(id);
        if (department == null) {
            throw new BusinessException(404, "部门不存在");
        }
        Integer status = request == null ? null : request.status();
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(400, "部门状态不正确");
        }
        if (status == 0 && repository.countEnabledUsers(id) > 0) {
            throw new BusinessException(400, "部门下存在启用用户，请先调整用户状态");
        }
        repository.updateStatus(id, status);
        return true;
    }

    private List<DepartmentResponse> buildTree(
            List<DepartmentRecord> departments,
            Long parentId,
            DepartmentQuery query,
            int level
    ) {
        List<DepartmentResponse> nodes = new ArrayList<>();
        for (DepartmentRecord department : departments) {
            if (!Objects.equals(department.parentId(), parentId)) {
                continue;
            }
            List<DepartmentResponse> children = buildTree(departments, department.id(), query, level + 1);
            boolean selfMatched = matches(department, query);
            if (selfMatched || !children.isEmpty()) {
                nodes.add(map(department, children, level));
            }
        }
        return nodes;
    }

    private DepartmentResponse map(DepartmentRecord department, List<DepartmentResponse> children, int level) {
        return new DepartmentResponse(
                department.id(),
                department.parentId(),
                department.name(),
                department.code(),
                typeOf(level),
                department.leader(),
                department.phone(),
                department.sort(),
                department.status(),
                department.createdAt() == null ? null : department.createdAt().format(DATE_TIME_FORMATTER),
                department.updatedAt() == null ? null : department.updatedAt().format(DATE_TIME_FORMATTER),
                children
        );
    }

    private static boolean matches(DepartmentRecord department, DepartmentQuery query) {
        if (query == null) {
            return true;
        }
        boolean nameMatched = query.name() == null
                || query.name().isBlank()
                || department.name().contains(query.name().trim());
        boolean statusMatched = query.status() == null || department.status().equals(query.status());
        return nameMatched && statusMatched;
    }

    private static String typeOf(int level) {
        if (level <= 1) {
            return "公司";
        }
        if (level == 2) {
            return "中心";
        }
        return "部门";
    }
}
