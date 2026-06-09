package com.oneplatform.backend.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oneplatform.backend.common.BusinessException;
import com.oneplatform.backend.portal.CredentialSecretService;

import org.junit.jupiter.api.Test;

class ProjectCredentialServiceTest {

    @Test
    void listFiltersByProjectEnvironmentAndStatusWithoutPlaintextOrCipher() {
        ProjectServiceTest.InMemoryProjectRepository projectRepository = new ProjectServiceTest.InMemoryProjectRepository();
        InMemoryProjectCredentialRepository credentialRepository = new InMemoryProjectCredentialRepository();
        ProjectCredentialService service = new ProjectCredentialService(
                projectRepository,
                credentialRepository,
                new TestCredentialSecretService()
        );

        List<ProjectCredentialResponse> response = service.list(new ProjectCredentialQuery(1L, "test", 1));

        assertThat(response).singleElement().satisfies(credential -> {
            assertThat(credential.name()).isEqualTo("portal tester");
            assertThat(credential.projectName()).isEqualTo(projectRepository.findById(1L).name());
            assertThat(credential.passwordMasked()).isEqualTo("************");
            assertThat(credential.updateTime()).isEqualTo("2026-06-05 10:00:00");
        });
        assertThat(response.toString()).doesNotContain("PortalSecret", "cipher:");
    }

    @Test
    void saveCreatesEncryptedCredentialAndMasksPassword() {
        ProjectServiceTest.InMemoryProjectRepository projectRepository = new ProjectServiceTest.InMemoryProjectRepository();
        InMemoryProjectCredentialRepository credentialRepository = new InMemoryProjectCredentialRepository();
        ProjectCredentialService service = new ProjectCredentialService(
                projectRepository,
                credentialRepository,
                new TestCredentialSecretService()
        );

        ProjectCredentialResponse created = service.save(new ProjectCredentialSaveRequest(
                null,
                1L,
                "new admin",
                "new_admin",
                "Secret#2026",
                "prod",
                "admin credential",
                LocalDate.of(2026, 12, 31),
                null
        ));

        ProjectCredentialRecord stored = credentialRepository.credentials.get(created.id());
        assertThat(created.passwordMasked()).isEqualTo("************");
        assertThat(created.status()).isEqualTo(1);
        assertThat(created.toString()).doesNotContain("Secret#2026", "cipher:");
        assertThat(stored.passwordCipher()).startsWith("cipher:");
        assertThat(stored.passwordCipher()).doesNotContain("Secret#2026");
        assertThat(stored.passwordMasked()).isEqualTo("************");
    }

    @Test
    void saveUpdatesWithoutPasswordKeepsCipherAndNewPasswordReplacesCipher() {
        ProjectServiceTest.InMemoryProjectRepository projectRepository = new ProjectServiceTest.InMemoryProjectRepository();
        InMemoryProjectCredentialRepository credentialRepository = new InMemoryProjectCredentialRepository();
        ProjectCredentialService service = new ProjectCredentialService(
                projectRepository,
                credentialRepository,
                new TestCredentialSecretService()
        );
        String oldCipher = credentialRepository.credentials.get(1L).passwordCipher();

        service.save(new ProjectCredentialSaveRequest(
                1L,
                1L,
                "portal admin",
                "portal_admin",
                "",
                "prod",
                "updated description",
                LocalDate.of(2026, 11, 30),
                1
        ));

        assertThat(credentialRepository.credentials.get(1L).passwordCipher()).isEqualTo(oldCipher);
        assertThat(credentialRepository.credentials.get(1L).description()).isEqualTo("updated description");

        service.save(new ProjectCredentialSaveRequest(
                1L,
                1L,
                "portal admin",
                "portal_admin",
                "NewSecret#2026",
                "prod",
                "updated description",
                LocalDate.of(2026, 11, 30),
                1
        ));

        assertThat(credentialRepository.credentials.get(1L).passwordCipher()).isNotEqualTo(oldCipher);
        assertThat(service.reveal(1L)).isEqualTo("NewSecret#2026");
    }

    @Test
    void deleteAndStatusValidateCredential() {
        ProjectServiceTest.InMemoryProjectRepository projectRepository = new ProjectServiceTest.InMemoryProjectRepository();
        InMemoryProjectCredentialRepository credentialRepository = new InMemoryProjectCredentialRepository();
        ProjectCredentialService service = new ProjectCredentialService(
                projectRepository,
                credentialRepository,
                new TestCredentialSecretService()
        );

        service.updateStatus(1L, new ProjectCredentialStatusRequest(0));
        service.delete(2L);

        assertThat(credentialRepository.credentials.get(1L).status()).isZero();
        assertThat(credentialRepository.deletedCredentialIds).containsExactly(2L);
        assertThatThrownBy(() -> service.updateStatus(1L, new ProjectCredentialStatusRequest(2)))
                .isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void revealAndCopyDecryptAfterRecordingOperationWithoutPlaintextInLog() {
        ProjectServiceTest.InMemoryProjectRepository projectRepository = new ProjectServiceTest.InMemoryProjectRepository();
        InMemoryProjectCredentialRepository credentialRepository = new InMemoryProjectCredentialRepository();
        ProjectCredentialService service = new ProjectCredentialService(
                projectRepository,
                credentialRepository,
                new TestCredentialSecretService()
        );

        String revealed = service.reveal(1L);
        String copiedUsername = service.copy(1L, new ProjectCredentialCopyRequest("username"));
        String copiedPassword = service.copy(1L, new ProjectCredentialCopyRequest("password"));

        assertThat(revealed).isEqualTo("PortalSecret");
        assertThat(copiedUsername).isEqualTo("portal_admin");
        assertThat(copiedPassword).isEqualTo("PortalSecret");
        assertThat(credentialRepository.logs)
                .extracting(ProjectCredentialOperationLogRecord::operationType)
                .containsExactly("查看密码", "复制账号", "复制密码");
        assertThat(credentialRepository.logs.toString()).doesNotContain("PortalSecret");
        assertThatThrownBy(() -> service.copy(1L, new ProjectCredentialCopyRequest("token")))
                .isInstanceOf(BusinessException.class);
    }

    static final class InMemoryProjectCredentialRepository implements ProjectCredentialRepository {

        private final Map<Long, ProjectCredentialRecord> credentials = new HashMap<>();
        private final List<Long> deletedCredentialIds = new ArrayList<>();
        private final List<ProjectCredentialOperationLogRecord> logs = new ArrayList<>();
        private long nextCredentialId = 10L;

        InMemoryProjectCredentialRepository() {
            LocalDateTime now = LocalDateTime.of(2026, 6, 5, 10, 0);
            credentials.put(1L, new ProjectCredentialRecord(
                    1L,
                    1L,
                    null,
                    "portal admin",
                    "portal_admin",
                    TestCredentialSecretService.encryptValue("PortalSecret"),
                    "************",
                    "prod",
                    "portal admin credential",
                    LocalDate.of(2026, 12, 31),
                    1,
                    now
            ));
            credentials.put(2L, new ProjectCredentialRecord(
                    2L,
                    1L,
                    null,
                    "portal tester",
                    "portal_tester",
                    TestCredentialSecretService.encryptValue("TestSecret"),
                    "************",
                    "test",
                    "portal test credential",
                    null,
                    1,
                    now
            ));
            credentials.put(3L, new ProjectCredentialRecord(
                    3L,
                    3L,
                    null,
                    "ai tester",
                    "ai_tester",
                    TestCredentialSecretService.encryptValue("AiSecret"),
                    "************",
                    "test",
                    "ai test credential",
                    null,
                    0,
                    now
            ));
        }

        @Override
        public List<ProjectCredentialRecord> findCredentials(ProjectCredentialQuery query) {
            return credentials.values().stream()
                    .filter(credential -> !deletedCredentialIds.contains(credential.id()))
                    .filter(credential -> query.projectId() == null || query.projectId().equals(credential.projectId()))
                    .filter(credential -> query.environment() == null || query.environment().equals(credential.environment()))
                    .filter(credential -> query.status() == null || query.status().equals(credential.status()))
                    .sorted((left, right) -> left.id().compareTo(right.id()))
                    .toList();
        }

        @Override
        public ProjectCredentialRecord findById(Long id) {
            if (deletedCredentialIds.contains(id)) {
                return null;
            }
            return credentials.get(id);
        }

        @Override
        public ProjectCredentialRecord create(ProjectCredentialMutation mutation) {
            Long id = nextCredentialId++;
            ProjectCredentialRecord record = toRecord(id, mutation);
            credentials.put(id, record);
            return record;
        }

        @Override
        public ProjectCredentialRecord update(Long id, ProjectCredentialMutation mutation) {
            ProjectCredentialRecord record = toRecord(id, mutation);
            credentials.put(id, record);
            return record;
        }

        @Override
        public void updateStatus(Long id, Integer status) {
            ProjectCredentialRecord old = credentials.get(id);
            credentials.put(id, new ProjectCredentialRecord(
                    old.id(),
                    old.projectId(),
                    old.projectName(),
                    old.name(),
                    old.username(),
                    old.passwordCipher(),
                    old.passwordMasked(),
                    old.environment(),
                    old.description(),
                    old.expireDate(),
                    status,
                    LocalDateTime.of(2026, 6, 5, 11, 0)
            ));
        }

        @Override
        public void softDelete(Long id) {
            deletedCredentialIds.add(id);
        }

        @Override
        public void recordOperation(ProjectCredentialOperationLogRecord log) {
            logs.add(log);
        }

        private ProjectCredentialRecord toRecord(Long id, ProjectCredentialMutation mutation) {
            return new ProjectCredentialRecord(
                    id,
                    mutation.projectId(),
                    null,
                    mutation.name(),
                    mutation.username(),
                    mutation.passwordCipher(),
                    mutation.passwordMasked(),
                    mutation.environment(),
                    mutation.description(),
                    mutation.expireDate(),
                    mutation.status(),
                    LocalDateTime.of(2026, 6, 5, 10, 0)
            );
        }
    }

    static final class TestCredentialSecretService implements CredentialSecretService {

        @Override
        public String encrypt(String rawPassword) {
            return encryptValue(rawPassword);
        }

        @Override
        public String decrypt(String passwordCipher) {
            if (passwordCipher == null || passwordCipher.isBlank()) {
                return "";
            }
            if (!passwordCipher.startsWith("cipher:")) {
                throw new BusinessException(500, "invalid cipher");
            }
            byte[] decoded = Base64.getDecoder().decode(passwordCipher.substring("cipher:".length()));
            return new String(decoded, StandardCharsets.UTF_8);
        }

        static String encryptValue(String rawPassword) {
            return "cipher:" + Base64.getEncoder().encodeToString(rawPassword.getBytes(StandardCharsets.UTF_8));
        }
    }
}
