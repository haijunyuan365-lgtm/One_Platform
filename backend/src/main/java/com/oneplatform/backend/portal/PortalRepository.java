package com.oneplatform.backend.portal;

import java.util.List;

import com.oneplatform.backend.portal.dto.PortalProjectQuery;

public interface PortalRepository {

    List<PortalProjectRecord> findVisibleProjects(PortalUserContext user, PortalProjectQuery query);

    PortalProjectRecord findVisibleProjectById(Long projectId, PortalUserContext user);

    List<PortalAddressRecord> findAddresses(Long projectId);

    List<PortalCredentialRecord> findCredentials(Long projectId);

    List<PortalQrcodeRecord> findQrcodes(Long projectId);

    PortalInstructionRecord findInstruction(Long projectId);

    PortalCredentialRecord findCredentialById(Long credentialId);

    PortalPermissionSummary findPermission(Long projectId, PortalUserContext user);

    void recordOperation(PortalOperationLogRecord log);

    void recordRecentVisit(Long userId, Long projectId);
}
