package com.oneplatform.backend.project;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.oneplatform.backend.common.BusinessException;

import org.springframework.stereotype.Service;

@Service
public class ProjectDetailService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ProjectRepository projectRepository;
    private final ProjectDetailRepository detailRepository;

    public ProjectDetailService(ProjectRepository projectRepository, ProjectDetailRepository detailRepository) {
        this.projectRepository = projectRepository;
        this.detailRepository = detailRepository;
    }

    public ProjectDetailContentResponse getDetailContent(Long projectId) {
        ProjectRecord project = requireProject(projectId);
        return new ProjectDetailContentResponse(
                mapProject(project),
                detailRepository.findAddresses(projectId).stream().map(this::mapAddress).toList(),
                mapInstruction(projectId, detailRepository.findInstruction(projectId))
        );
    }

    public List<ProjectAddressResponse> saveAddresses(Long projectId, ProjectAddressListRequest request) {
        requireProject(projectId);
        List<ProjectAddressMutation> mutations = normalizeAddresses(projectId, request);
        return detailRepository.replaceAddresses(projectId, mutations).stream()
                .map(this::mapAddress)
                .toList();
    }

    public ProjectInstructionResponse saveInstruction(Long projectId, ProjectInstructionRequest request) {
        requireProject(projectId);
        ProjectInstructionMutation mutation = normalizeInstruction(request);
        return mapInstruction(projectId, detailRepository.upsertInstruction(projectId, mutation));
    }

    private ProjectRecord requireProject(Long projectId) {
        if (projectId == null) {
            throw new BusinessException(404, "项目不存在");
        }
        ProjectRecord project = projectRepository.findById(projectId);
        if (project == null) {
            throw new BusinessException(404, "项目不存在");
        }
        return project;
    }

    private List<ProjectAddressMutation> normalizeAddresses(Long projectId, ProjectAddressListRequest request) {
        List<ProjectAddressRequest> list = request == null || request.list() == null ? List.of() : request.list();
        Set<Long> existingIds = new LinkedHashSet<>(detailRepository.findAddressIds(projectId));
        int defaultCount = 0;
        int detectionCount = 0;
        int index = 0;
        Set<Long> requestedIds = new LinkedHashSet<>();
        List<ProjectAddressMutation> mutations = new java.util.ArrayList<>();
        for (ProjectAddressRequest item : list) {
            index++;
            if (item == null) {
                continue;
            }
            Long id = item.id() == null || item.id() <= 0 ? null : item.id();
            if (id != null && (!existingIds.contains(id) || !requestedIds.add(id))) {
                throw new BusinessException(404, "访问地址不存在");
            }
            Integer isDefault = normalizeFlag(item.isDefault(), 0, "默认地址参数错误");
            Integer isDetection = normalizeFlag(item.isDetection(), 0, "检测地址参数错误");
            if (isDefault == 1) {
                defaultCount++;
            }
            if (isDetection == 1) {
                detectionCount++;
            }
            mutations.add(new ProjectAddressMutation(
                    id,
                    requiredText(item.name(), "请输入地址名称"),
                    requiredText(item.type(), "请选择地址类型"),
                    requiredText(item.url(), "请输入访问地址"),
                    isDefault,
                    isDetection,
                    item.sort() == null ? index : item.sort(),
                    normalizeFlag(item.status(), 1, "地址状态参数错误")
            ));
        }
        if (defaultCount > 1) {
            throw new BusinessException(400, "每个项目最多一个默认打开地址");
        }
        if (detectionCount > 1) {
            throw new BusinessException(400, "每个项目最多一个检测地址");
        }
        return mutations;
    }

    private ProjectInstructionMutation normalizeInstruction(ProjectInstructionRequest request) {
        if (request == null) {
            throw new BusinessException(400, "参数错误");
        }
        return new ProjectInstructionMutation(
                trimToEmpty(request.browserRequirement()),
                trimToEmpty(request.vpnRequirement()),
                trimToEmpty(request.notes()),
                requiredText(request.maintainer(), "请输入维护联系人"),
                trimToEmpty(request.contactPhone())
        );
    }

    private ProjectResponse mapProject(ProjectRecord project) {
        return new ProjectResponse(
                project.id(),
                project.name(),
                project.shortName(),
                project.logoUrl(),
                project.category(),
                splitTags(project.tags()),
                project.description(),
                project.maintainerId(),
                project.maintainerName(),
                project.sort(),
                project.enabled(),
                project.status(),
                format(project.lastCheckTime()),
                project.responseTime(),
                project.abnormalReason(),
                format(project.createdAt()),
                format(project.updatedAt())
        );
    }

    private ProjectAddressResponse mapAddress(ProjectAddressRecord address) {
        return new ProjectAddressResponse(
                address.id(),
                address.projectId(),
                address.name(),
                address.type(),
                address.url(),
                address.isDefault(),
                address.isDetection(),
                address.sort(),
                address.status()
        );
    }

    private ProjectInstructionResponse mapInstruction(Long projectId, ProjectInstructionRecord instruction) {
        if (instruction == null) {
            return new ProjectInstructionResponse(projectId, "", "", "", "", "", null);
        }
        return new ProjectInstructionResponse(
                projectId,
                instruction.browserRequirement(),
                instruction.vpnRequirement(),
                instruction.notes(),
                instruction.maintainer(),
                instruction.contactPhone(),
                format(instruction.updatedAt())
        );
    }

    private static Integer normalizeFlag(Integer value, Integer defaultValue, String message) {
        if (value == null) {
            return defaultValue;
        }
        if (value != 0 && value != 1) {
            throw new BusinessException(400, message);
        }
        return value;
    }

    private static String requiredText(String value, String message) {
        String text = trimToNull(value);
        if (text == null) {
            throw new BusinessException(400, message);
        }
        return text;
    }

    private static String trimToEmpty(String value) {
        String text = trimToNull(value);
        return text == null ? "" : text;
    }

    private static String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static List<String> splitTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return List.of();
        }
        return Arrays.stream(tags.split(","))
                .map(ProjectDetailService::trimToNull)
                .filter(tag -> tag != null)
                .toList();
    }

    private static String format(LocalDateTime value) {
        return value == null ? null : value.format(DATE_TIME_FORMATTER);
    }
}
