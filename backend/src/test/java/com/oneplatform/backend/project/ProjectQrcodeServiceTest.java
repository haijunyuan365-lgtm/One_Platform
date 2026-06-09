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

class ProjectQrcodeServiceTest {

    @Test
    void listFiltersByProjectAndStatusWithProjectName() {
        ProjectServiceTest.InMemoryProjectRepository projectRepository = new ProjectServiceTest.InMemoryProjectRepository();
        InMemoryProjectQrcodeRepository qrcodeRepository = new InMemoryProjectQrcodeRepository();
        ProjectQrcodeService service = new ProjectQrcodeService(projectRepository, qrcodeRepository);

        List<ProjectQrcodeResponse> response = service.list(new ProjectQrcodeQuery(1L, 1));

        assertThat(response)
                .extracting(ProjectQrcodeResponse::name)
                .containsExactly("portal mobile entry");
        assertThat(response.get(0).projectName()).isEqualTo(projectRepository.findById(1L).name());
        assertThat(response.get(0).image()).isEqualTo("/assets/qrcode/portal.png");
        assertThat(response.get(0).updateTime()).isEqualTo("2026-06-05 10:00:00");
    }

    @Test
    void saveCreatesQrcodeWithDefaultsAndValidatesRequiredFields() {
        ProjectServiceTest.InMemoryProjectRepository projectRepository = new ProjectServiceTest.InMemoryProjectRepository();
        InMemoryProjectQrcodeRepository qrcodeRepository = new InMemoryProjectQrcodeRepository();
        ProjectQrcodeService service = new ProjectQrcodeService(projectRepository, qrcodeRepository);

        assertThatThrownBy(() -> service.save(new ProjectQrcodeSaveRequest(
                null,
                1L,
                "",
                "/assets/qrcode/new.png",
                "all",
                "new entry",
                1
        ))).isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> service.save(new ProjectQrcodeSaveRequest(
                null,
                1L,
                "new entry",
                "",
                "all",
                "new entry",
                1
        ))).isInstanceOf(BusinessException.class);

        ProjectQrcodeResponse created = service.save(new ProjectQrcodeSaveRequest(
                null,
                1L,
                "new entry",
                "/assets/qrcode/new.png",
                "all",
                "new entry",
                null
        ));

        ProjectQrcodeRecord stored = qrcodeRepository.qrcodes.get(created.id());
        assertThat(created.status()).isEqualTo(1);
        assertThat(created.projectName()).isEqualTo(projectRepository.findById(1L).name());
        assertThat(stored.imageUrl()).isEqualTo("/assets/qrcode/new.png");
        assertThat(stored.fileId()).isNull();
    }

    @Test
    void saveUpdatesExistingQrcodeAndUsesExistingProjectWhenProjectIdOmitted() {
        ProjectServiceTest.InMemoryProjectRepository projectRepository = new ProjectServiceTest.InMemoryProjectRepository();
        InMemoryProjectQrcodeRepository qrcodeRepository = new InMemoryProjectQrcodeRepository();
        ProjectQrcodeService service = new ProjectQrcodeService(projectRepository, qrcodeRepository);

        ProjectQrcodeResponse updated = service.save(new ProjectQrcodeSaveRequest(
                1L,
                null,
                "portal app entry",
                "/assets/qrcode/portal-new.png",
                "all staff",
                "updated qrcode",
                0
        ));

        assertThat(updated.projectId()).isEqualTo(1L);
        assertThat(updated.status()).isZero();
        assertThat(qrcodeRepository.qrcodes.get(1L).imageUrl()).isEqualTo("/assets/qrcode/portal-new.png");
    }

    @Test
    void deleteAndStatusValidateQrcode() {
        ProjectServiceTest.InMemoryProjectRepository projectRepository = new ProjectServiceTest.InMemoryProjectRepository();
        InMemoryProjectQrcodeRepository qrcodeRepository = new InMemoryProjectQrcodeRepository();
        ProjectQrcodeService service = new ProjectQrcodeService(projectRepository, qrcodeRepository);

        service.updateStatus(1L, new ProjectQrcodeStatusRequest(0));
        service.delete(2L);

        assertThat(qrcodeRepository.qrcodes.get(1L).status()).isZero();
        assertThat(qrcodeRepository.deletedQrcodeIds).containsExactly(2L);
        assertThatThrownBy(() -> service.updateStatus(1L, new ProjectQrcodeStatusRequest(2)))
                .isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(BusinessException.class);
    }

    static final class InMemoryProjectQrcodeRepository implements ProjectQrcodeRepository {

        private final Map<Long, ProjectQrcodeRecord> qrcodes = new HashMap<>();
        private final List<Long> deletedQrcodeIds = new ArrayList<>();
        private long nextQrcodeId = 10L;

        InMemoryProjectQrcodeRepository() {
            LocalDateTime now = LocalDateTime.of(2026, 6, 5, 10, 0);
            qrcodes.put(1L, new ProjectQrcodeRecord(
                    1L,
                    1L,
                    null,
                    "portal mobile entry",
                    null,
                    "/assets/qrcode/portal.png",
                    "all staff",
                    "scan to open portal",
                    1,
                    now
            ));
            qrcodes.put(2L, new ProjectQrcodeRecord(
                    2L,
                    1L,
                    null,
                    "portal disabled entry",
                    null,
                    "/assets/qrcode/portal-disabled.png",
                    "ops",
                    "disabled qrcode",
                    0,
                    now
            ));
            qrcodes.put(3L, new ProjectQrcodeRecord(
                    3L,
                    3L,
                    null,
                    "ai entry",
                    null,
                    "/assets/qrcode/ai.png",
                    "product",
                    "ai qrcode",
                    1,
                    now
            ));
        }

        @Override
        public List<ProjectQrcodeRecord> findQrcodes(ProjectQrcodeQuery query) {
            return qrcodes.values().stream()
                    .filter(qrcode -> !deletedQrcodeIds.contains(qrcode.id()))
                    .filter(qrcode -> query.projectId() == null || query.projectId().equals(qrcode.projectId()))
                    .filter(qrcode -> query.status() == null || query.status().equals(qrcode.status()))
                    .sorted((left, right) -> left.id().compareTo(right.id()))
                    .toList();
        }

        @Override
        public ProjectQrcodeRecord findById(Long id) {
            if (deletedQrcodeIds.contains(id)) {
                return null;
            }
            return qrcodes.get(id);
        }

        @Override
        public ProjectQrcodeRecord create(ProjectQrcodeMutation mutation) {
            Long id = nextQrcodeId++;
            ProjectQrcodeRecord record = toRecord(id, mutation);
            qrcodes.put(id, record);
            return record;
        }

        @Override
        public ProjectQrcodeRecord update(Long id, ProjectQrcodeMutation mutation) {
            ProjectQrcodeRecord record = toRecord(id, mutation);
            qrcodes.put(id, record);
            return record;
        }

        @Override
        public void updateStatus(Long id, Integer status) {
            ProjectQrcodeRecord old = qrcodes.get(id);
            qrcodes.put(id, new ProjectQrcodeRecord(
                    old.id(),
                    old.projectId(),
                    old.projectName(),
                    old.name(),
                    old.fileId(),
                    old.imageUrl(),
                    old.audience(),
                    old.description(),
                    status,
                    LocalDateTime.of(2026, 6, 5, 11, 0)
            ));
        }

        @Override
        public void softDelete(Long id) {
            deletedQrcodeIds.add(id);
        }

        private ProjectQrcodeRecord toRecord(Long id, ProjectQrcodeMutation mutation) {
            return new ProjectQrcodeRecord(
                    id,
                    mutation.projectId(),
                    null,
                    mutation.name(),
                    mutation.fileId(),
                    mutation.imageUrl(),
                    mutation.audience(),
                    mutation.description(),
                    mutation.status(),
                    LocalDateTime.of(2026, 6, 5, 10, 0)
            );
        }
    }
}
