package com.oneplatform.backend.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.oneplatform.backend.common.BusinessException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

class FileUploadServiceTest {

    @TempDir
    private Path tempDir;

    @Test
    void uploadQrcodeStoresFileAndRecordsMetadata() throws Exception {
        InMemoryFileUploadRepository repository = new InMemoryFileUploadRepository();
        FileUploadService service = new FileUploadService(repository, properties(1024 * 1024));
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "portal.png",
                "image/png",
                new byte[] {1, 2, 3, 4}
        );

        FileUploadResponse response = service.upload(file, "qrcode");

        PlatformFileRecord stored = repository.files.get(response.id());
        assertThat(response.originName()).isEqualTo("portal.png");
        assertThat(response.bizType()).isEqualTo("qrcode");
        assertThat(response.url()).startsWith("/uploads/qrcode/");
        assertThat(stored.storageName()).endsWith(".png");
        assertThat(stored.storageName()).isNotEqualTo("portal.png");
        assertThat(stored.contentType()).isEqualTo("image/png");
        assertThat(stored.size()).isEqualTo(4);
        assertThat(Files.exists(Path.of(stored.storagePath()))).isTrue();
    }

    @Test
    void uploadRejectsUnsupportedTypeForQrcodeButAllowsWebpLogo() {
        InMemoryFileUploadRepository repository = new InMemoryFileUploadRepository();
        FileUploadService service = new FileUploadService(repository, properties(1024 * 1024));
        MockMultipartFile webp = new MockMultipartFile(
                "file",
                "logo.webp",
                "image/webp",
                new byte[] {1}
        );

        assertThatThrownBy(() -> service.upload(webp, "qrcode"))
                .isInstanceOf(BusinessException.class);

        FileUploadResponse response = service.upload(webp, "logo");

        assertThat(response.bizType()).isEqualTo("logo");
        assertThat(response.url()).endsWith(".webp");
    }

    @Test
    void uploadRejectsOversizedOrEmptyFiles() {
        FileUploadService service = new FileUploadService(new InMemoryFileUploadRepository(), properties(2));

        assertThatThrownBy(() -> service.upload(new MockMultipartFile(
                "file",
                "large.png",
                "image/png",
                new byte[] {1, 2, 3}
        ), "qrcode")).isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> service.upload(new MockMultipartFile(
                "file",
                "empty.png",
                "image/png",
                new byte[0]
        ), "qrcode")).isInstanceOf(BusinessException.class);
    }

    private FileUploadProperties properties(long maxSizeBytes) {
        FileUploadProperties properties = new FileUploadProperties();
        properties.setUploadDir(tempDir.toString());
        properties.setPublicPrefix("/uploads");
        properties.setMaxSizeBytes(maxSizeBytes);
        return properties;
    }

    static final class InMemoryFileUploadRepository implements FileUploadRepository {

        private final Map<Long, PlatformFileRecord> files = new HashMap<>();
        private long nextFileId = 10L;

        @Override
        public PlatformFileRecord create(PlatformFileMutation mutation) {
            Long id = nextFileId++;
            PlatformFileRecord record = new PlatformFileRecord(
                    id,
                    mutation.originName(),
                    mutation.storageName(),
                    mutation.storagePath(),
                    mutation.url(),
                    mutation.contentType(),
                    mutation.size(),
                    mutation.bizType(),
                    mutation.uploaderId(),
                    LocalDateTime.of(2026, 6, 5, 10, 0)
            );
            files.put(id, record);
            return record;
        }
    }
}
