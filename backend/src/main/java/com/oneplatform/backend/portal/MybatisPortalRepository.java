package com.oneplatform.backend.portal;

import java.util.List;

import com.oneplatform.backend.portal.dto.PortalProjectQuery;
import com.oneplatform.backend.portal.mapper.PortalMapper;

import org.springframework.stereotype.Repository;

@Repository
public class MybatisPortalRepository implements PortalRepository {

    private final PortalMapper mapper;

    public MybatisPortalRepository(PortalMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<PortalProjectRecord> findVisibleProjects(PortalUserContext user, PortalProjectQuery query) {
        return mapper.findVisibleProjects(
                user.userId(),
                user.departmentId(),
                user.roleIds(),
                trimToNull(query == null ? null : query.keyword()),
                trimToNull(query == null ? null : query.category()),
                trimToNull(query == null ? null : query.status())
        );
    }

    @Override
    public PortalProjectRecord findVisibleProjectById(Long projectId, PortalUserContext user) {
        return mapper.findVisibleProjectById(projectId, user.userId(), user.departmentId(), user.roleIds());
    }

    @Override
    public List<PortalAddressRecord> findAddresses(Long projectId) {
        return mapper.findAddresses(projectId);
    }

    @Override
    public List<PortalCredentialRecord> findCredentials(Long projectId) {
        return mapper.findCredentials(projectId);
    }

    @Override
    public List<PortalQrcodeRecord> findQrcodes(Long projectId) {
        return mapper.findQrcodes(projectId);
    }

    @Override
    public PortalInstructionRecord findInstruction(Long projectId) {
        return mapper.findInstruction(projectId);
    }

    @Override
    public PortalCredentialRecord findCredentialById(Long credentialId) {
        return mapper.findCredentialById(credentialId);
    }

    @Override
    public PortalPermissionSummary findPermission(Long projectId, PortalUserContext user) {
        PortalPermissionSummary permission = mapper.findPermission(projectId, user.userId(), user.departmentId(), user.roleIds());
        if (permission == null) {
            return new PortalPermissionSummary(false, false, false, false);
        }
        return permission;
    }

    @Override
    public void recordOperation(PortalOperationLogRecord log) {
        mapper.recordOperation(log);
    }

    @Override
    public void recordRecentVisit(Long userId, Long projectId) {
        mapper.recordRecentVisit(userId, projectId);
    }

    private static String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
