package com.oneplatform.backend.project;

import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.oneplatform.backend.project.entity.OpProjectCredential;
import com.oneplatform.backend.project.mapper.ProjectCredentialMapper;

import org.springframework.stereotype.Repository;

@Repository
public class MybatisProjectCredentialRepository implements ProjectCredentialRepository {

    private final ProjectCredentialMapper projectCredentialMapper;

    public MybatisProjectCredentialRepository(ProjectCredentialMapper projectCredentialMapper) {
        this.projectCredentialMapper = projectCredentialMapper;
    }

    @Override
    public List<ProjectCredentialRecord> findCredentials(ProjectCredentialQuery query) {
        return projectCredentialMapper.findCredentials(query.projectId(), query.environment(), query.status());
    }

    @Override
    public ProjectCredentialRecord findById(Long id) {
        return projectCredentialMapper.findCredentialById(id);
    }

    @Override
    public ProjectCredentialRecord create(ProjectCredentialMutation mutation) {
        OpProjectCredential credential = new OpProjectCredential();
        apply(credential, mutation);
        LocalDateTime now = LocalDateTime.now();
        credential.setCreatedAt(now);
        credential.setUpdatedAt(now);
        credential.setDeleted(0);
        projectCredentialMapper.insert(credential);
        return findById(credential.getId());
    }

    @Override
    public ProjectCredentialRecord update(Long id, ProjectCredentialMutation mutation) {
        projectCredentialMapper.update(null, new LambdaUpdateWrapper<OpProjectCredential>()
                .eq(OpProjectCredential::getId, id)
                .eq(OpProjectCredential::getDeleted, 0)
                .set(OpProjectCredential::getProjectId, mutation.projectId())
                .set(OpProjectCredential::getName, mutation.name())
                .set(OpProjectCredential::getUsername, mutation.username())
                .set(OpProjectCredential::getPasswordCipher, mutation.passwordCipher())
                .set(OpProjectCredential::getPasswordMasked, mutation.passwordMasked())
                .set(OpProjectCredential::getEnvironment, mutation.environment())
                .set(OpProjectCredential::getDescription, mutation.description())
                .set(OpProjectCredential::getExpireDate, mutation.expireDate())
                .set(OpProjectCredential::getStatus, mutation.status())
                .set(OpProjectCredential::getUpdatedAt, LocalDateTime.now()));
        return findById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        projectCredentialMapper.update(null, new LambdaUpdateWrapper<OpProjectCredential>()
                .eq(OpProjectCredential::getId, id)
                .eq(OpProjectCredential::getDeleted, 0)
                .set(OpProjectCredential::getStatus, status)
                .set(OpProjectCredential::getUpdatedAt, LocalDateTime.now()));
    }

    @Override
    public void softDelete(Long id) {
        projectCredentialMapper.update(null, new LambdaUpdateWrapper<OpProjectCredential>()
                .eq(OpProjectCredential::getId, id)
                .set(OpProjectCredential::getDeleted, 1)
                .set(OpProjectCredential::getUpdatedAt, LocalDateTime.now()));
    }

    @Override
    public void recordOperation(ProjectCredentialOperationLogRecord log) {
        projectCredentialMapper.recordOperation(log);
    }

    private void apply(OpProjectCredential credential, ProjectCredentialMutation mutation) {
        credential.setProjectId(mutation.projectId());
        credential.setName(mutation.name());
        credential.setUsername(mutation.username());
        credential.setPasswordCipher(mutation.passwordCipher());
        credential.setPasswordMasked(mutation.passwordMasked());
        credential.setEnvironment(mutation.environment());
        credential.setDescription(mutation.description());
        credential.setExpireDate(mutation.expireDate());
        credential.setStatus(mutation.status());
    }
}
