package com.oneplatform.backend.project;

import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.oneplatform.backend.project.entity.OpProject;
import com.oneplatform.backend.project.mapper.ProjectMapper;

import org.springframework.stereotype.Repository;

@Repository
public class MybatisProjectRepository implements ProjectRepository {

    private final ProjectMapper projectMapper;

    public MybatisProjectRepository(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    @Override
    public List<ProjectRecord> findProjects() {
        return projectMapper.selectList(new LambdaQueryWrapper<OpProject>()
                        .eq(OpProject::getDeleted, 0)
                        .orderByAsc(OpProject::getSort)
                        .orderByAsc(OpProject::getId))
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public ProjectRecord findById(Long id) {
        OpProject project = projectMapper.selectOne(new LambdaQueryWrapper<OpProject>()
                .eq(OpProject::getId, id)
                .eq(OpProject::getDeleted, 0)
                .last("LIMIT 1"));
        return project == null ? null : map(project);
    }

    @Override
    public boolean existsName(String name, Long excludeId) {
        LambdaQueryWrapper<OpProject> wrapper = new LambdaQueryWrapper<OpProject>()
                .eq(OpProject::getName, name)
                .eq(OpProject::getDeleted, 0);
        if (excludeId != null) {
            wrapper.ne(OpProject::getId, excludeId);
        }
        return projectMapper.selectCount(wrapper) > 0;
    }

    @Override
    public ProjectRecord create(ProjectMutation mutation) {
        OpProject project = new OpProject();
        apply(project, mutation);
        LocalDateTime now = LocalDateTime.now();
        project.setCreatedAt(now);
        project.setUpdatedAt(now);
        project.setDeleted(0);
        projectMapper.insert(project);
        return findById(project.getId());
    }

    @Override
    public ProjectRecord update(Long id, ProjectMutation mutation) {
        projectMapper.update(null, new LambdaUpdateWrapper<OpProject>()
                .eq(OpProject::getId, id)
                .eq(OpProject::getDeleted, 0)
                .set(OpProject::getName, mutation.name())
                .set(OpProject::getShortName, mutation.shortName())
                .set(OpProject::getLogoFileId, mutation.logoFileId())
                .set(OpProject::getLogoUrl, mutation.logoUrl())
                .set(OpProject::getCategory, mutation.category())
                .set(OpProject::getTags, mutation.tags())
                .set(OpProject::getDescription, mutation.description())
                .set(OpProject::getMaintainerId, mutation.maintainerId())
                .set(OpProject::getMaintainerName, mutation.maintainerName())
                .set(OpProject::getSort, mutation.sort())
                .set(OpProject::getEnabled, mutation.enabled())
                .set(OpProject::getStatus, mutation.status())
                .set(OpProject::getUpdatedAt, LocalDateTime.now()));
        return findById(id);
    }

    @Override
    public void updateEnabled(Long id, Integer enabled) {
        projectMapper.update(null, new LambdaUpdateWrapper<OpProject>()
                .eq(OpProject::getId, id)
                .eq(OpProject::getDeleted, 0)
                .set(OpProject::getEnabled, enabled)
                .set(OpProject::getUpdatedAt, LocalDateTime.now()));
    }

    @Override
    public long countRelations(Long id) {
        return projectMapper.countProjectRelations(id);
    }

    @Override
    public void softDelete(Long id) {
        projectMapper.update(null, new LambdaUpdateWrapper<OpProject>()
                .eq(OpProject::getId, id)
                .set(OpProject::getDeleted, 1)
                .set(OpProject::getUpdatedAt, LocalDateTime.now()));
    }

    private ProjectRecord map(OpProject project) {
        return new ProjectRecord(
                project.getId(),
                project.getName(),
                project.getShortName(),
                project.getLogoFileId(),
                project.getLogoUrl(),
                project.getCategory(),
                project.getTags(),
                project.getDescription(),
                project.getMaintainerId(),
                project.getMaintainerName(),
                project.getSort(),
                project.getEnabled(),
                project.getStatus(),
                project.getLastCheckTime(),
                project.getResponseTime(),
                project.getAbnormalReason(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }

    private void apply(OpProject project, ProjectMutation mutation) {
        project.setName(mutation.name());
        project.setShortName(mutation.shortName());
        project.setLogoFileId(mutation.logoFileId());
        project.setLogoUrl(mutation.logoUrl());
        project.setCategory(mutation.category());
        project.setTags(mutation.tags());
        project.setDescription(mutation.description());
        project.setMaintainerId(mutation.maintainerId());
        project.setMaintainerName(mutation.maintainerName());
        project.setSort(mutation.sort());
        project.setEnabled(mutation.enabled());
        project.setStatus(mutation.status());
    }
}
