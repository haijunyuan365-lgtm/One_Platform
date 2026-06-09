package com.oneplatform.backend.file;

import java.time.LocalDateTime;

import com.oneplatform.backend.file.entity.SysFile;
import com.oneplatform.backend.file.mapper.SysFileMapper;

import org.springframework.stereotype.Repository;

@Repository
public class MybatisFileUploadRepository implements FileUploadRepository {

    private final SysFileMapper sysFileMapper;

    public MybatisFileUploadRepository(SysFileMapper sysFileMapper) {
        this.sysFileMapper = sysFileMapper;
    }

    @Override
    public PlatformFileRecord create(PlatformFileMutation mutation) {
        LocalDateTime now = LocalDateTime.now();
        SysFile file = new SysFile();
        file.setOriginName(mutation.originName());
        file.setStorageName(mutation.storageName());
        file.setStoragePath(mutation.storagePath());
        file.setUrl(mutation.url());
        file.setContentType(mutation.contentType());
        file.setSize(mutation.size());
        file.setBizType(mutation.bizType());
        file.setUploaderId(mutation.uploaderId());
        file.setUploadedAt(now);
        file.setCreatedAt(now);
        file.setUpdatedAt(now);
        file.setDeleted(0);
        sysFileMapper.insert(file);
        return map(file);
    }

    private PlatformFileRecord map(SysFile file) {
        return new PlatformFileRecord(
                file.getId(),
                file.getOriginName(),
                file.getStorageName(),
                file.getStoragePath(),
                file.getUrl(),
                file.getContentType(),
                file.getSize(),
                file.getBizType(),
                file.getUploaderId(),
                file.getUploadedAt()
        );
    }
}
