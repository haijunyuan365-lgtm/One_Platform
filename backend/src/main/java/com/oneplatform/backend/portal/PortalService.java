package com.oneplatform.backend.portal;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import com.oneplatform.backend.common.BusinessException;
import com.oneplatform.backend.portal.dto.CopyCredentialRequest;
import com.oneplatform.backend.portal.dto.PortalActionLogRequest;
import com.oneplatform.backend.portal.dto.PortalProjectDetailResponse;
import com.oneplatform.backend.portal.dto.PortalProjectQuery;

import org.springframework.stereotype.Service;

@Service
public class PortalService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final PortalRepository repository;
    private final CredentialSecretService credentialSecretService;
    private final PortalUserResolver userResolver;
    private final String defaultIp;

    public PortalService(
            PortalRepository repository,
            CredentialSecretService credentialSecretService,
            PortalUserResolver userResolver
    ) {
        this(repository, credentialSecretService, userResolver, "");
    }

    public PortalService(
            PortalRepository repository,
            CredentialSecretService credentialSecretService,
            PortalUserResolver userResolver,
            String defaultIp
    ) {
        this.repository = repository;
        this.credentialSecretService = credentialSecretService;
        this.userResolver = userResolver;
        this.defaultIp = defaultIp;
    }

    public List<PortalProjectDetailResponse.ProjectCard> listProjects(PortalProjectQuery query, String authorization) {
        PortalUserContext user = userResolver.resolve(authorization);
        return repository.findVisibleProjects(user, query).stream()
                .map(this::mapProject)
                .toList();
    }

    public PortalProjectDetailResponse getProjectDetail(Long projectId, String authorization) {
        PortalUserContext user = userResolver.resolve(authorization);
        PortalProjectRecord project = requireVisibleProject(projectId, user);
        PortalPermissionSummary permission = repository.findPermission(projectId, user);
        repository.recordRecentVisit(user.userId(), projectId);
        return new PortalProjectDetailResponse(
                mapProject(project),
                repository.findAddresses(projectId).stream().map(this::mapAddress).toList(),
                repository.findCredentials(projectId).stream().map(this::mapCredential).toList(),
                repository.findQrcodes(projectId).stream().map(this::mapQrcode).toList(),
                mapInstruction(repository.findInstruction(projectId)),
                permission.passwordView(),
                permission.passwordCopy(),
                permission.qrcodeView()
        );
    }

    public String revealCredential(Long credentialId, String authorization) {
        PortalUserContext user = userResolver.resolve(authorization);
        PortalCredentialRecord credential = requireEnabledCredential(credentialId);
        PortalProjectRecord project = requireVisibleProject(credential.projectId(), user);
        PortalPermissionSummary permission = repository.findPermission(credential.projectId(), user);
        if (!permission.passwordView()) {
            throw new BusinessException(403, "无密码查看权限");
        }
        record(user, project, "查看密码", credential.name(), "成功", null);
        return credentialSecretService.decrypt(credential.passwordCipher());
    }

    public String copyCredential(Long credentialId, CopyCredentialRequest request, String authorization) {
        PortalUserContext user = userResolver.resolve(authorization);
        PortalCredentialRecord credential = requireEnabledCredential(credentialId);
        PortalProjectRecord project = requireVisibleProject(credential.projectId(), user);
        String field = request == null ? "" : request.field();
        if ("username".equals(field)) {
            record(user, project, "复制账号", credential.name(), "成功", null);
            return credential.username();
        }
        if ("password".equals(field)) {
            PortalPermissionSummary permission = repository.findPermission(credential.projectId(), user);
            if (!permission.passwordCopy()) {
                throw new BusinessException(403, "无密码复制权限");
            }
            record(user, project, "复制密码", credential.name(), "成功", null);
            return credentialSecretService.decrypt(credential.passwordCipher());
        }
        throw new BusinessException(400, "复制字段不支持");
    }

    public boolean recordAction(PortalActionLogRequest request, String authorization) {
        PortalUserContext user = userResolver.resolve(authorization);
        PortalProjectRecord project = requireVisibleProject(request.projectId(), user);
        record(user, project, request.action(), request.target(), "成功", null);
        return true;
    }

    private PortalCredentialRecord requireEnabledCredential(Long credentialId) {
        PortalCredentialRecord credential = repository.findCredentialById(credentialId);
        if (credential == null || credential.status() == null || credential.status() != 1) {
            throw new BusinessException(404, "凭据不存在或已停用");
        }
        return credential;
    }

    private PortalProjectRecord requireVisibleProject(Long projectId, PortalUserContext user) {
        PortalProjectRecord project = repository.findVisibleProjectById(projectId, user);
        if (project == null) {
            throw new BusinessException(403, "无项目访问权限");
        }
        return project;
    }

    private void record(
            PortalUserContext user,
            PortalProjectRecord project,
            String operationType,
            String target,
            String result,
            String remark
    ) {
        repository.recordOperation(new PortalOperationLogRecord(
                user.userId(),
                user.username(),
                user.username(),
                user.departmentId(),
                user.departmentName(),
                project.id(),
                project.name(),
                operationType,
                target,
                result,
                defaultIp,
                remark
        ));
    }

    private PortalProjectDetailResponse.ProjectCard mapProject(PortalProjectRecord project) {
        return new PortalProjectDetailResponse.ProjectCard(
                project.id(),
                project.name(),
                project.shortName(),
                project.logoUrl(),
                project.category(),
                splitTags(project.tags()),
                project.description(),
                project.maintainerName(),
                project.status(),
                project.lastCheckTime() == null ? null : project.lastCheckTime().format(DATE_TIME_FORMATTER),
                project.responseTime(),
                project.abnormalReason()
        );
    }

    private PortalProjectDetailResponse.ProjectAddress mapAddress(PortalAddressRecord address) {
        return new PortalProjectDetailResponse.ProjectAddress(
                address.id(),
                address.name(),
                address.type(),
                address.url(),
                address.isDefault()
        );
    }

    private PortalProjectDetailResponse.ProjectCredential mapCredential(PortalCredentialRecord credential) {
        return new PortalProjectDetailResponse.ProjectCredential(
                credential.id(),
                credential.name(),
                credential.username(),
                credential.passwordMasked(),
                credential.environment(),
                credential.description()
        );
    }

    private PortalProjectDetailResponse.ProjectQrcode mapQrcode(PortalQrcodeRecord qrcode) {
        return new PortalProjectDetailResponse.ProjectQrcode(
                qrcode.id(),
                qrcode.name(),
                qrcode.imageUrl(),
                qrcode.audience(),
                qrcode.description()
        );
    }

    private PortalProjectDetailResponse.ProjectInstruction mapInstruction(PortalInstructionRecord instruction) {
        if (instruction == null) {
            return new PortalProjectDetailResponse.ProjectInstruction("", "", "", "", "");
        }
        return new PortalProjectDetailResponse.ProjectInstruction(
                instruction.browserRequirement(),
                instruction.vpnRequirement(),
                instruction.notes(),
                instruction.maintainer(),
                instruction.contactPhone()
        );
    }

    private static List<String> splitTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return List.of();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList();
    }
}
