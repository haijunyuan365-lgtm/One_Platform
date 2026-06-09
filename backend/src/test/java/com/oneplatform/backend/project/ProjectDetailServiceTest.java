package com.oneplatform.backend.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oneplatform.backend.common.BusinessException;

import org.junit.jupiter.api.Test;

class ProjectDetailServiceTest {

    @Test
    void getDetailContentAggregatesProjectAddressesAndInstruction() {
        ProjectServiceTest.InMemoryProjectRepository projectRepository = new ProjectServiceTest.InMemoryProjectRepository();
        InMemoryProjectDetailRepository detailRepository = new InMemoryProjectDetailRepository();
        ProjectDetailService service = new ProjectDetailService(projectRepository, detailRepository);

        ProjectDetailContentResponse response = service.getDetailContent(1L);

        assertThat(response.project().name()).isEqualTo("公司统一门户");
        assertThat(response.addresses()).hasSize(2);
        assertThat(response.addresses().get(0).name()).isEqualTo("正式地址");
        assertThat(response.instruction().browserRequirement()).isEqualTo("Chrome 120+");
        assertThat(response.instruction().projectId()).isEqualTo(1L);
    }

    @Test
    void saveAddressesReplacesProjectAddressesAndNormalizesProjectId() {
        ProjectServiceTest.InMemoryProjectRepository projectRepository = new ProjectServiceTest.InMemoryProjectRepository();
        InMemoryProjectDetailRepository detailRepository = new InMemoryProjectDetailRepository();
        ProjectDetailService service = new ProjectDetailService(projectRepository, detailRepository);

        List<ProjectAddressResponse> saved = service.saveAddresses(1L, new ProjectAddressListRequest(List.of(
                new ProjectAddressRequest(1L, 999L, "正式地址", "Web", "https://portal.example.test", 1, 1, 1, 1),
                new ProjectAddressRequest(null, null, "后台地址", "后台", "https://portal.example.test/admin", 0, 0, 2, null)
        )));

        assertThat(saved).hasSize(2);
        assertThat(saved).extracting(ProjectAddressResponse::projectId).containsOnly(1L);
        assertThat(saved.get(1).id()).isEqualTo(8L);
        assertThat(saved.get(1).status()).isEqualTo(1);
    }

    @Test
    void saveAddressesDefaultsSwitchFlagsToOffAndStatusToEnabled() {
        ProjectServiceTest.InMemoryProjectRepository projectRepository = new ProjectServiceTest.InMemoryProjectRepository();
        InMemoryProjectDetailRepository detailRepository = new InMemoryProjectDetailRepository();
        ProjectDetailService service = new ProjectDetailService(projectRepository, detailRepository);

        List<ProjectAddressResponse> saved = service.saveAddresses(1L, new ProjectAddressListRequest(List.of(
                new ProjectAddressRequest(null, null, "新地址", "Web", "https://new.example.test", null, null, 1, null)
        )));

        assertThat(saved).singleElement().satisfies(address -> {
            assertThat(address.isDefault()).isZero();
            assertThat(address.isDetection()).isZero();
            assertThat(address.status()).isEqualTo(1);
        });
    }

    @Test
    void saveAddressesRejectsMultipleDefaultsOrDetections() {
        ProjectServiceTest.InMemoryProjectRepository projectRepository = new ProjectServiceTest.InMemoryProjectRepository();
        InMemoryProjectDetailRepository detailRepository = new InMemoryProjectDetailRepository();
        ProjectDetailService service = new ProjectDetailService(projectRepository, detailRepository);

        assertThatThrownBy(() -> service.saveAddresses(1L, new ProjectAddressListRequest(List.of(
                new ProjectAddressRequest(null, null, "地址1", "Web", "https://a.example.test", 1, 0, 1, 1),
                new ProjectAddressRequest(null, null, "地址2", "Web", "https://b.example.test", 1, 0, 2, 1)
        )))).isInstanceOf(BusinessException.class)
                .hasMessage("每个项目最多一个默认打开地址");

        assertThatThrownBy(() -> service.saveAddresses(1L, new ProjectAddressListRequest(List.of(
                new ProjectAddressRequest(null, null, "地址1", "Web", "https://a.example.test", 0, 1, 1, 1),
                new ProjectAddressRequest(null, null, "地址2", "Web", "https://b.example.test", 0, 1, 2, 1)
        )))).isInstanceOf(BusinessException.class)
                .hasMessage("每个项目最多一个检测地址");
    }

    @Test
    void saveInstructionUpsertsAndRequiresMaintainer() {
        ProjectServiceTest.InMemoryProjectRepository projectRepository = new ProjectServiceTest.InMemoryProjectRepository();
        InMemoryProjectDetailRepository detailRepository = new InMemoryProjectDetailRepository();
        ProjectDetailService service = new ProjectDetailService(projectRepository, detailRepository);

        assertThatThrownBy(() -> service.saveInstruction(1L, new ProjectInstructionRequest(
                null,
                "Chrome",
                "VPN",
                "notes",
                "",
                "13800138000"
        ))).isInstanceOf(BusinessException.class)
                .hasMessage("请输入维护联系人");

        ProjectInstructionResponse saved = service.saveInstruction(1L, new ProjectInstructionRequest(
                999L,
                "Edge 120+",
                "需要 VPN",
                "发布窗口注意维护提示",
                "管理员",
                "13800138000"
        ));

        assertThat(saved.projectId()).isEqualTo(1L);
        assertThat(saved.browserRequirement()).isEqualTo("Edge 120+");
        assertThat(saved.updateTime()).isEqualTo("2026-06-05 10:00:00");
    }

    static final class InMemoryProjectDetailRepository implements ProjectDetailRepository {

        private final Map<Long, ProjectAddressRecord> addresses = new HashMap<>();
        private final Map<Long, ProjectInstructionRecord> instructions = new HashMap<>();
        private long nextAddressId = 8L;

        InMemoryProjectDetailRepository() {
            LocalDateTime now = LocalDateTime.of(2026, 6, 5, 9, 0);
            addresses.put(1L, new ProjectAddressRecord(1L, 1L, "正式地址", "Web", "https://portal.example.test", 1, 1, 1, 1, now));
            addresses.put(2L, new ProjectAddressRecord(2L, 1L, "文档地址", "文档", "https://docs.example.test", 0, 0, 2, 1, now));
            instructions.put(1L, new ProjectInstructionRecord(1L, 1L, "Chrome 120+", "需要 VPN", "注意事项", "管理员", "13800138000", now));
        }

        @Override
        public List<ProjectAddressRecord> findAddresses(Long projectId) {
            return addresses.values().stream()
                    .filter(address -> address.projectId().equals(projectId))
                    .sorted((left, right) -> left.sort().compareTo(right.sort()))
                    .toList();
        }

        @Override
        public List<Long> findAddressIds(Long projectId) {
            return findAddresses(projectId).stream().map(ProjectAddressRecord::id).toList();
        }

        @Override
        public List<ProjectAddressRecord> replaceAddresses(Long projectId, List<ProjectAddressMutation> mutations) {
            List<Long> nextIds = new ArrayList<>();
            for (ProjectAddressMutation mutation : mutations) {
                Long id = mutation.id() == null ? nextAddressId++ : mutation.id();
                nextIds.add(id);
                addresses.put(id, toAddress(id, projectId, mutation));
            }
            addresses.keySet().removeIf(id -> addresses.get(id).projectId().equals(projectId) && !nextIds.contains(id));
            return findAddresses(projectId);
        }

        @Override
        public ProjectInstructionRecord findInstruction(Long projectId) {
            return instructions.get(projectId);
        }

        @Override
        public ProjectInstructionRecord upsertInstruction(Long projectId, ProjectInstructionMutation mutation) {
            ProjectInstructionRecord record = new ProjectInstructionRecord(
                    instructions.get(projectId) == null ? 1L : instructions.get(projectId).id(),
                    projectId,
                    mutation.browserRequirement(),
                    mutation.vpnRequirement(),
                    mutation.notes(),
                    mutation.maintainer(),
                    mutation.contactPhone(),
                    LocalDateTime.of(2026, 6, 5, 10, 0)
            );
            instructions.put(projectId, record);
            return record;
        }

        private ProjectAddressRecord toAddress(Long id, Long projectId, ProjectAddressMutation mutation) {
            return new ProjectAddressRecord(
                    id,
                    projectId,
                    mutation.name(),
                    mutation.type(),
                    mutation.url(),
                    mutation.isDefault(),
                    mutation.isDetection(),
                    mutation.sort(),
                    mutation.status(),
                    LocalDateTime.of(2026, 6, 5, 10, 0)
            );
        }
    }
}
