package com.oneplatform.backend.project;

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

class ProjectServiceTest {

    @Test
    void listFiltersByKeywordCategoryMaintainerEnabledAndStatus() {
        InMemoryProjectRepository repository = new InMemoryProjectRepository();
        ProjectService service = new ProjectService(repository);

        PageResponse<ProjectResponse> response = service.list(new ProjectQuery(
                "数据",
                "数据平台",
                "李四",
                1,
                "异常",
                1,
                10
        ));

        assertThat(response.total()).isEqualTo(1);
        assertThat(response.list()).singleElement().satisfies(project -> {
            assertThat(project.name()).isEqualTo("经营数据分析平台");
            assertThat(project.tags()).containsExactly("正式", "内网", "报表");
            assertThat(project.logo()).isEqualTo("/assets/project/bi.svg");
            assertThat(project.responseTime()).isEqualTo(0);
        });
    }

    @Test
    void optionsReturnEnabledProjectsOrderedForSelect() {
        InMemoryProjectRepository repository = new InMemoryProjectRepository();
        ProjectService service = new ProjectService(repository);

        List<ProjectOptionResponse> options = service.options();

        assertThat(options)
                .extracting(ProjectOptionResponse::shortName)
                .containsExactly("统一门户", "数据分析", "AI 原型");
    }

    @Test
    void createRejectsDuplicateNameAndUsesDefaults() {
        InMemoryProjectRepository repository = new InMemoryProjectRepository();
        ProjectService service = new ProjectService(repository);

        assertThatThrownBy(() -> service.create(newProject("公司统一门户")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("项目名称已存在");

        ProjectResponse created = service.create(newProject("知识库"));

        assertThat(created.id()).isEqualTo(6L);
        assertThat(created.shortName()).isEqualTo("知识库");
        assertThat(created.enabled()).isEqualTo(1);
        assertThat(created.status()).isEqualTo("未检测");
        assertThat(created.sort()).isEqualTo(100);
        assertThat(repository.projects.get(6L).tags()).isEqualTo("内部,文档");
    }

    @Test
    void updateRejectsDuplicateNameAndPersistsEditableFields() {
        InMemoryProjectRepository repository = new InMemoryProjectRepository();
        ProjectService service = new ProjectService(repository);

        assertThatThrownBy(() -> service.update(3L, newProject("公司统一门户")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("项目名称已存在");

        ProjectResponse updated = service.update(3L, new ProjectSaveRequest(
                "AI 原型平台",
                "AI 原型",
                "/assets/project/ai-new.svg",
                "AI工具",
                List.of("测试", "AI"),
                "智能原型生成",
                4L,
                "赵六",
                9,
                0,
                "可用"
        ));

        assertThat(updated.name()).isEqualTo("AI 原型平台");
        assertThat(updated.logo()).isEqualTo("/assets/project/ai-new.svg");
        assertThat(updated.enabled()).isEqualTo(0);
        assertThat(updated.tags()).containsExactly("测试", "AI");
    }

    @Test
    void deleteRejectsRelatedProjectAndSoftDeletesEmptyProject() {
        InMemoryProjectRepository repository = new InMemoryProjectRepository();
        ProjectService service = new ProjectService(repository);

        assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("该项目存在关联数据，不可删除");

        service.delete(3L);

        assertThat(repository.deletedProjectIds).containsExactly(3L);
    }

    @Test
    void updateEnabledValidatesProjectAndStatusValue() {
        InMemoryProjectRepository repository = new InMemoryProjectRepository();
        ProjectService service = new ProjectService(repository);

        service.updateEnabled(1L, new ProjectEnabledRequest(0));

        assertThat(repository.projects.get(1L).enabled()).isEqualTo(0);
        assertThatThrownBy(() -> service.updateEnabled(1L, new ProjectEnabledRequest(2)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("项目启用状态参数错误");
    }

    private static ProjectSaveRequest newProject(String name) {
        return new ProjectSaveRequest(
                name,
                "知识库",
                null,
                "内部系统",
                List.of("内部", "", "文档", "内部"),
                "团队知识沉淀",
                2L,
                "李四",
                null,
                null,
                null
        );
    }

    static final class InMemoryProjectRepository implements ProjectRepository {

        private final Map<Long, ProjectRecord> projects = new HashMap<>();
        private final Map<Long, Long> relationCounts = new HashMap<>();
        private final List<Long> deletedProjectIds = new ArrayList<>();
        private long nextProjectId = 6L;

        InMemoryProjectRepository() {
            LocalDateTime now = LocalDateTime.of(2026, 6, 5, 9, 0);
            projects.put(1L, new ProjectRecord(1L, "公司统一门户", "统一门户", null, "/assets/project/portal.svg", "内部系统", "正式,内网,SSO", "公司入口", 1L, "管理员", 1, 1, "可用", now, 126, null, now, now));
            projects.put(3L, new ProjectRecord(3L, "AI 原型生成平台", "AI 原型", null, "/assets/project/ai.svg", "AI工具", "测试,内网,AI", "智能原型", 4L, "赵六", 3, 1, "可用", now, 98, null, now, now));
            projects.put(4L, new ProjectRecord(4L, "经营数据分析平台", "数据分析", null, "/assets/project/bi.svg", "数据平台", "正式,内网,报表", "经营数据", 2L, "李四", 2, 1, "异常", now, 0, "HTTP 502", now, now));
            projects.put(5L, new ProjectRecord(5L, "停用系统", "停用", null, null, "内部系统", "", "已停用", 2L, "李四", 4, 0, "未检测", null, null, null, now, now));
            relationCounts.put(1L, 2L);
        }

        @Override
        public List<ProjectRecord> findProjects() {
            return projects.entrySet().stream()
                    .filter(entry -> !deletedProjectIds.contains(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .toList();
        }

        @Override
        public ProjectRecord findById(Long id) {
            if (deletedProjectIds.contains(id)) {
                return null;
            }
            return projects.get(id);
        }

        @Override
        public boolean existsName(String name, Long excludeId) {
            return projects.values().stream()
                    .anyMatch(project -> project.name().equals(name)
                            && !project.id().equals(excludeId)
                            && !deletedProjectIds.contains(project.id()));
        }

        @Override
        public ProjectRecord create(ProjectMutation mutation) {
            Long id = nextProjectId++;
            ProjectRecord record = toRecord(id, mutation);
            projects.put(id, record);
            return record;
        }

        @Override
        public ProjectRecord update(Long id, ProjectMutation mutation) {
            ProjectRecord record = toRecord(id, mutation);
            projects.put(id, record);
            return record;
        }

        @Override
        public void updateEnabled(Long id, Integer enabled) {
            ProjectRecord old = projects.get(id);
            projects.put(id, new ProjectRecord(old.id(), old.name(), old.shortName(), old.logoFileId(), old.logoUrl(), old.category(), old.tags(), old.description(), old.maintainerId(), old.maintainerName(), old.sort(), enabled, old.status(), old.lastCheckTime(), old.responseTime(), old.abnormalReason(), old.createdAt(), old.updatedAt()));
        }

        @Override
        public long countRelations(Long id) {
            return relationCounts.getOrDefault(id, 0L);
        }

        @Override
        public void softDelete(Long id) {
            deletedProjectIds.add(id);
        }

        private ProjectRecord toRecord(Long id, ProjectMutation mutation) {
            LocalDateTime now = LocalDateTime.of(2026, 6, 5, 10, 0);
            return new ProjectRecord(
                    id,
                    mutation.name(),
                    mutation.shortName(),
                    mutation.logoFileId(),
                    mutation.logoUrl(),
                    mutation.category(),
                    mutation.tags(),
                    mutation.description(),
                    mutation.maintainerId(),
                    mutation.maintainerName(),
                    mutation.sort(),
                    mutation.enabled(),
                    mutation.status(),
                    null,
                    null,
                    null,
                    now,
                    now
            );
        }
    }
}
