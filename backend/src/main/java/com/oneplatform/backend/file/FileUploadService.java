package com.oneplatform.backend.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import com.oneplatform.backend.common.BusinessException;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {

    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Set<String> LOGO_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> QRCODE_EXTENSIONS = Set.of("jpg", "jpeg", "png");
    private static final Set<String> IMAGE_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final FileUploadRepository repository;
    private final FileUploadProperties properties;

    public FileUploadService(FileUploadRepository repository, FileUploadProperties properties) {
        this.repository = repository;
        this.properties = properties;
    }

    public FileUploadResponse upload(MultipartFile file, String bizType) {
        validateFile(file);
        String safeBizType = normalizeBizType(bizType);
        String originName = cleanOriginName(file.getOriginalFilename());
        String extension = extensionOf(originName);
        validateImageType(safeBizType, extension, file.getContentType());

        String day = LocalDate.now().format(DAY_FORMATTER);
        String storageName = UUID.randomUUID() + "." + extension;
        Path root = Path.of(properties.getUploadDir()).toAbsolutePath().normalize();
        Path directory = root.resolve(safeBizType).resolve(day).normalize();
        Path target = directory.resolve(storageName).normalize();
        if (!target.startsWith(root)) {
            throw new BusinessException(400, "文件路径不合法");
        }
        try {
            Files.createDirectories(directory);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new BusinessException(500, "文件保存失败");
        }

        PlatformFileRecord record = repository.create(new PlatformFileMutation(
                originName,
                storageName,
                target.toString(),
                buildUrl(safeBizType, day, storageName),
                normalizeContentType(file.getContentType(), extension),
                file.getSize(),
                safeBizType,
                1L
        ));
        return map(record);
    }

    private FileUploadResponse map(PlatformFileRecord record) {
        return new FileUploadResponse(
                record.id(),
                record.originName(),
                record.url(),
                record.contentType(),
                record.size(),
                record.bizType(),
                format(record.uploadedAt())
        );
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "请选择上传文件");
        }
        if (file.getSize() > properties.getMaxSizeBytes()) {
            throw new BusinessException(400, "文件大小超出限制");
        }
    }

    private static String cleanOriginName(String originalFilename) {
        String originName = StringUtils.cleanPath(originalFilename == null ? "" : originalFilename);
        if (originName.isBlank() || originName.contains("..") || originName.contains("/") || originName.contains("\\")) {
            throw new BusinessException(400, "文件名不合法");
        }
        return originName;
    }

    private static String extensionOf(String originName) {
        int dotIndex = originName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == originName.length() - 1) {
            throw new BusinessException(400, "文件类型不支持");
        }
        return originName.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }

    private static void validateImageType(String bizType, String extension, String contentType) {
        Set<String> allowedExtensions = "qrcode".equals(bizType) ? QRCODE_EXTENSIONS : LOGO_EXTENSIONS;
        if (!allowedExtensions.contains(extension)) {
            throw new BusinessException(400, "文件类型不支持");
        }
        String normalizedContentType = normalizeContentType(contentType, extension);
        if (!IMAGE_CONTENT_TYPES.contains(normalizedContentType)) {
            throw new BusinessException(400, "文件类型不支持");
        }
    }

    private static String normalizeContentType(String contentType, String extension) {
        if (contentType != null && !contentType.isBlank()) {
            return contentType.toLowerCase(Locale.ROOT);
        }
        if ("png".equals(extension)) {
            return "image/png";
        }
        if ("webp".equals(extension)) {
            return "image/webp";
        }
        return "image/jpeg";
    }

    private static String normalizeBizType(String bizType) {
        if (bizType == null || bizType.isBlank()) {
            return "common";
        }
        String value = bizType.trim().toLowerCase(Locale.ROOT);
        if (!value.matches("[a-z0-9_-]{1,50}")) {
            throw new BusinessException(400, "业务类型不合法");
        }
        return value;
    }

    private String buildUrl(String bizType, String day, String storageName) {
        String prefix = properties.getPublicPrefix();
        if (prefix == null || prefix.isBlank()) {
            prefix = "/uploads";
        }
        if (prefix.endsWith("/")) {
            prefix = prefix.substring(0, prefix.length() - 1);
        }
        return prefix + "/" + bizType + "/" + day + "/" + storageName;
    }

    private static String format(LocalDateTime value) {
        return value == null ? null : value.format(DATE_TIME_FORMATTER);
    }
}
