package com.oneplatform.backend.project;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.oneplatform.backend.common.BusinessException;
import com.oneplatform.backend.portal.CredentialSecretService;

import org.springframework.stereotype.Service;

@Service
public class ProjectCredentialService {

    private static final String DEFAULT_ENVIRONMENT = "test";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ProjectRepository projectRepository;
    private final ProjectCredentialRepository credentialRepository;
    private final CredentialSecretService credentialSecretService;

    public ProjectCredentialService(
            ProjectRepository projectRepository,
            ProjectCredentialRepository credentialRepository,
            CredentialSecretService credentialSecretService
    ) {
        this.projectRepository = projectRepository;
        this.credentialRepository = credentialRepository;
        this.credentialSecretService = credentialSecretService;
    }

    public List<ProjectCredentialResponse> list(ProjectCredentialQuery query) {
        return credentialRepository.findCredentials(normalizeQuery(query)).stream()
                .map(this::map)
                .toList();
    }

    public ProjectCredentialResponse save(ProjectCredentialSaveRequest request) {
        if (request == null) {
            throw new BusinessException(400, "参数错误");
        }
        ProjectCredentialRecord existing = request.id() == null ? null : requireCredential(request.id());
        Long projectId = request.projectId() == null && existing != null ? existing.projectId() : request.projectId();
        ProjectRecord project = requireProject(projectId);
        String name = requiredText(request.name(), "请输入凭据名称");
        String username = requiredText(request.username(), "请输入账号");
        String environment = defaultText(request.environment(), DEFAULT_ENVIRONMENT);
        Integer status = normalizeStatus(request.status());
        String rawPassword = trimToNull(request.password());
        String passwordCipher;
        String passwordMasked;
        if (existing == null) {
            if (rawPassword == null) {
                throw new BusinessException(400, "请输入密码");
            }
            passwordCipher = credentialSecretService.encrypt(rawPassword);
            passwordMasked = PasswordMasker.mask(rawPassword);
        } else if (rawPassword == null) {
            passwordCipher = existing.passwordCipher();
            passwordMasked = existing.passwordMasked();
        } else {
            passwordCipher = credentialSecretService.encrypt(rawPassword);
            passwordMasked = PasswordMasker.mask(rawPassword);
        }

        ProjectCredentialMutation mutation = new ProjectCredentialMutation(
                project.id(),
                name,
                username,
                passwordCipher,
                passwordMasked,
                environment,
                trimToNull(request.description()),
                request.expireDate(),
                status
        );
        ProjectCredentialRecord saved = existing == null
                ? credentialRepository.create(mutation)
                : credentialRepository.update(existing.id(), mutation);
        return map(withProjectName(saved, project.name()));
    }

    public void delete(Long id) {
        requireCredential(id);
        credentialRepository.softDelete(id);
    }

    public void updateStatus(Long id, ProjectCredentialStatusRequest request) {
        requireCredential(id);
        credentialRepository.updateStatus(id, normalizeStatus(request == null ? null : request.status()));
    }

    public String reveal(Long id) {
        ProjectCredentialRecord credential = requireCredential(id);
        record(credential, "查看密码", "权限校验通过");
        return credentialSecretService.decrypt(credential.passwordCipher());
    }

    public String copy(Long id, ProjectCredentialCopyRequest request) {
        ProjectCredentialRecord credential = requireCredential(id);
        String field = request == null ? "" : request.field();
        if ("username".equals(field)) {
            record(credential, "复制账号", "复制成功");
            return credential.username();
        }
        if ("password".equals(field)) {
            record(credential, "复制密码", "复制成功");
            return credentialSecretService.decrypt(credential.passwordCipher());
        }
        throw new BusinessException(400, "复制字段不支持");
    }

    private ProjectCredentialResponse map(ProjectCredentialRecord credential) {
        return new ProjectCredentialResponse(
                credential.id(),
                credential.projectId(),
                resolveProjectName(credential),
                credential.name(),
                credential.username(),
                credential.passwordMasked(),
                credential.environment(),
                credential.description(),
                credential.expireDate(),
                credential.status(),
                format(credential.updatedAt())
        );
    }

    private ProjectCredentialRecord requireCredential(Long id) {
        if (id == null) {
            throw new BusinessException(404, "凭据不存在");
        }
        ProjectCredentialRecord credential = credentialRepository.findById(id);
        if (credential == null) {
            throw new BusinessException(404, "凭据不存在");
        }
        return credential;
    }

    private ProjectRecord requireProject(Long id) {
        if (id == null) {
            throw new BusinessException(404, "项目不存在");
        }
        ProjectRecord project = projectRepository.findById(id);
        if (project == null) {
            throw new BusinessException(404, "项目不存在");
        }
        return project;
    }

    private void record(ProjectCredentialRecord credential, String operationType, String remark) {
        String projectName = resolveProjectName(credential);
        credentialRepository.recordOperation(new ProjectCredentialOperationLogRecord(
                1L,
                "管理员",
                "admin",
                1L,
                "总公司",
                credential.projectId(),
                projectName,
                operationType,
                credential.name(),
                "成功",
                "",
                remark
        ));
    }

    private String resolveProjectName(ProjectCredentialRecord credential) {
        String projectName = trimToNull(credential.projectName());
        if (projectName != null) {
            return projectName;
        }
        ProjectRecord project = projectRepository.findById(credential.projectId());
        return project == null ? "" : project.name();
    }

    private ProjectCredentialRecord withProjectName(ProjectCredentialRecord credential, String projectName) {
        return new ProjectCredentialRecord(
                credential.id(),
                credential.projectId(),
                projectName,
                credential.name(),
                credential.username(),
                credential.passwordCipher(),
                credential.passwordMasked(),
                credential.environment(),
                credential.description(),
                credential.expireDate(),
                credential.status(),
                credential.updatedAt()
        );
    }

    private static ProjectCredentialQuery normalizeQuery(ProjectCredentialQuery query) {
        if (query == null) {
            return new ProjectCredentialQuery(null, null, null);
        }
        return new ProjectCredentialQuery(
                query.projectId(),
                trimToNull(query.environment()),
                query.status()
        );
    }

    private static Integer normalizeStatus(Integer status) {
        if (status == null) {
            return 1;
        }
        if (status != 0 && status != 1) {
            throw new BusinessException(400, "凭据状态参数错误");
        }
        return status;
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

    private static String format(LocalDateTime value) {
        return value == null ? null : value.format(DATE_TIME_FORMATTER);
    }
}
