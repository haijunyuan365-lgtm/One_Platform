package com.oneplatform.backend.project;

import java.util.List;

public interface ProjectRepository {

    List<ProjectRecord> findProjects();

    ProjectRecord findById(Long id);

    boolean existsName(String name, Long excludeId);

    ProjectRecord create(ProjectMutation mutation);

    ProjectRecord update(Long id, ProjectMutation mutation);

    void updateEnabled(Long id, Integer enabled);

    long countRelations(Long id);

    void softDelete(Long id);
}
