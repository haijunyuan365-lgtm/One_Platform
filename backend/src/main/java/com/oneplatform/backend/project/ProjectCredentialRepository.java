package com.oneplatform.backend.project;

import java.util.List;

public interface ProjectCredentialRepository {

    List<ProjectCredentialRecord> findCredentials(ProjectCredentialQuery query);

    ProjectCredentialRecord findById(Long id);

    ProjectCredentialRecord create(ProjectCredentialMutation mutation);

    ProjectCredentialRecord update(Long id, ProjectCredentialMutation mutation);

    void updateStatus(Long id, Integer status);

    void softDelete(Long id);

    void recordOperation(ProjectCredentialOperationLogRecord log);
}
