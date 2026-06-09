package com.oneplatform.backend.portal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.oneplatform.backend.portal.dto.PortalProjectQuery;

class InMemoryPortalRepository implements PortalRepository {

    private final List<String> operationLogs = new ArrayList<>();
    private final List<String> recentVisits = new ArrayList<>();

    @Override
    public List<PortalProjectRecord> findVisibleProjects(PortalUserContext user, PortalProjectQuery query) {
        return projects().stream()
                .filter(project -> findPermission(project.id(), user).visible())
                .filter(project -> matches(project, query))
                .toList();
    }

    @Override
    public PortalProjectRecord findVisibleProjectById(Long projectId, PortalUserContext user) {
        return projects().stream()
                .filter(project -> project.id().equals(projectId))
                .filter(project -> findPermission(project.id(), user).visible())
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<PortalAddressRecord> findAddresses(Long projectId) {
        return List.of(new PortalAddressRecord(1L, "正式地址", "Web", "https://portal.example.test", 1));
    }

    @Override
    public List<PortalCredentialRecord> findCredentials(Long projectId) {
        return credentials().stream()
                .filter(credential -> credential.projectId().equals(projectId))
                .toList();
    }

    @Override
    public List<PortalQrcodeRecord> findQrcodes(Long projectId) {
        return List.of(new PortalQrcodeRecord(1L, "移动入口", "/assets/qrcode/portal.png", "全员", "移动端入口"));
    }

    @Override
    public PortalInstructionRecord findInstruction(Long projectId) {
        return new PortalInstructionRecord("Chrome 120+", "需连接 VPN", "联系维护人", "管理员", "13800138000");
    }

    @Override
    public PortalCredentialRecord findCredentialById(Long credentialId) {
        return credentials().stream()
                .filter(credential -> credential.id().equals(credentialId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public PortalPermissionSummary findPermission(Long projectId, PortalUserContext user) {
        if (projectId == 1L) {
            return new PortalPermissionSummary(true, true, false, true);
        }
        if (projectId == 3L) {
            return new PortalPermissionSummary(true, false, false, true);
        }
        if (projectId == 5L) {
            return new PortalPermissionSummary(true, true, true, true);
        }
        return new PortalPermissionSummary(false, false, false, false);
    }

    @Override
    public void recordOperation(PortalOperationLogRecord log) {
        operationLogs.add("%d:%d:%s:%s:%s".formatted(
                log.operatorId(),
                log.projectId(),
                log.operationType(),
                log.target(),
                log.result()
        ));
    }

    @Override
    public void recordRecentVisit(Long userId, Long projectId) {
        recentVisits.add("%d:%d".formatted(userId, projectId));
    }

    List<String> operationLogs() {
        return operationLogs;
    }

    List<String> recentVisits() {
        return recentVisits;
    }

    private static List<PortalProjectRecord> projects() {
        return List.of(
                new PortalProjectRecord(1L, "公司统一门户", "统一门户", "/portal.svg", "内部系统", "正式,内网", "统一入口", "管理员", "可用", LocalDateTime.now(), 126, null),
                new PortalProjectRecord(2L, "停用项目", "停用", "/disabled.svg", "内部系统", "停用", "不可见", "管理员", "可用", LocalDateTime.now(), 0, null),
                new PortalProjectRecord(3L, "AI 原型生成平台", "AI 原型", "/ai.svg", "AI工具", "测试,AI", "原型工具", "赵六", "可用", LocalDateTime.now(), 98, null),
                new PortalProjectRecord(5L, "运维监控中心", "监控中心", "/ops.svg", "运维服务", "正式,监控", "运维入口", "李四", "可用", LocalDateTime.now(), 164, null)
        );
    }

    private static List<PortalCredentialRecord> credentials() {
        return List.of(
                new PortalCredentialRecord(1L, 1L, "演示账号", "portal_demo", "secret:portal", "************", "演示", "内部培训", 1),
                new PortalCredentialRecord(2L, 3L, "测试账号", "prototype_demo", "secret:prototype", "************", "测试", "产品体验", 1),
                new PortalCredentialRecord(3L, 5L, "监控账号", "ops_viewer", "secret:ops", "************", "正式", "运维查看", 1)
        );
    }

    private static boolean matches(PortalProjectRecord project, PortalProjectQuery query) {
        if (query == null) {
            return true;
        }
        boolean categoryMatched = query.category() == null || query.category().isBlank() || project.category().equals(query.category());
        boolean statusMatched = query.status() == null || query.status().isBlank() || project.status().equals(query.status());
        boolean keywordMatched = query.keyword() == null
                || query.keyword().isBlank()
                || project.name().contains(query.keyword())
                || project.shortName().contains(query.keyword())
                || project.description().contains(query.keyword())
                || project.tags().contains(query.keyword());
        return categoryMatched && statusMatched && keywordMatched;
    }
}
