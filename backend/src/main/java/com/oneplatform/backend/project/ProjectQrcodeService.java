package com.oneplatform.backend.project;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.oneplatform.backend.common.BusinessException;

import org.springframework.stereotype.Service;

@Service
public class ProjectQrcodeService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ProjectRepository projectRepository;
    private final ProjectQrcodeRepository qrcodeRepository;

    public ProjectQrcodeService(ProjectRepository projectRepository, ProjectQrcodeRepository qrcodeRepository) {
        this.projectRepository = projectRepository;
        this.qrcodeRepository = qrcodeRepository;
    }

    public List<ProjectQrcodeResponse> list(ProjectQrcodeQuery query) {
        return qrcodeRepository.findQrcodes(normalizeQuery(query)).stream()
                .map(this::map)
                .toList();
    }

    public ProjectQrcodeResponse save(ProjectQrcodeSaveRequest request) {
        if (request == null) {
            throw new BusinessException(400, "参数错误");
        }
        ProjectQrcodeRecord existing = request.id() == null ? null : requireQrcode(request.id());
        Long projectId = request.projectId() == null && existing != null ? existing.projectId() : request.projectId();
        ProjectRecord project = requireProject(projectId);
        String name = requiredText(request.name(), "请输入二维码名称");
        String imageUrl = requiredText(request.image(), "请上传二维码图片");
        Integer status = normalizeStatus(request.status());
        ProjectQrcodeMutation mutation = new ProjectQrcodeMutation(
                project.id(),
                name,
                existing == null ? null : existing.fileId(),
                imageUrl,
                trimToNull(request.audience()),
                trimToNull(request.description()),
                status
        );
        ProjectQrcodeRecord saved = existing == null
                ? qrcodeRepository.create(mutation)
                : qrcodeRepository.update(existing.id(), mutation);
        return map(withProjectName(saved, project.name()));
    }

    public void delete(Long id) {
        requireQrcode(id);
        qrcodeRepository.softDelete(id);
    }

    public void updateStatus(Long id, ProjectQrcodeStatusRequest request) {
        requireQrcode(id);
        qrcodeRepository.updateStatus(id, normalizeStatus(request == null ? null : request.status()));
    }

    private ProjectQrcodeResponse map(ProjectQrcodeRecord qrcode) {
        return new ProjectQrcodeResponse(
                qrcode.id(),
                qrcode.projectId(),
                resolveProjectName(qrcode),
                qrcode.name(),
                qrcode.imageUrl(),
                qrcode.audience(),
                qrcode.description(),
                qrcode.status(),
                format(qrcode.updatedAt())
        );
    }

    private ProjectQrcodeRecord requireQrcode(Long id) {
        if (id == null) {
            throw new BusinessException(404, "二维码不存在");
        }
        ProjectQrcodeRecord qrcode = qrcodeRepository.findById(id);
        if (qrcode == null) {
            throw new BusinessException(404, "二维码不存在");
        }
        return qrcode;
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

    private String resolveProjectName(ProjectQrcodeRecord qrcode) {
        String projectName = trimToNull(qrcode.projectName());
        if (projectName != null) {
            return projectName;
        }
        ProjectRecord project = projectRepository.findById(qrcode.projectId());
        return project == null ? "" : project.name();
    }

    private ProjectQrcodeRecord withProjectName(ProjectQrcodeRecord qrcode, String projectName) {
        return new ProjectQrcodeRecord(
                qrcode.id(),
                qrcode.projectId(),
                projectName,
                qrcode.name(),
                qrcode.fileId(),
                qrcode.imageUrl(),
                qrcode.audience(),
                qrcode.description(),
                qrcode.status(),
                qrcode.updatedAt()
        );
    }

    private static ProjectQrcodeQuery normalizeQuery(ProjectQrcodeQuery query) {
        if (query == null) {
            return new ProjectQrcodeQuery(null, null);
        }
        return new ProjectQrcodeQuery(query.projectId(), query.status());
    }

    private static Integer normalizeStatus(Integer status) {
        if (status == null) {
            return 1;
        }
        if (status != 0 && status != 1) {
            throw new BusinessException(400, "二维码状态参数错误");
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
