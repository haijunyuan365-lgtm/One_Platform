package com.oneplatform.backend.project;

import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.oneplatform.backend.project.entity.OpProjectQrcode;
import com.oneplatform.backend.project.mapper.ProjectQrcodeMapper;

import org.springframework.stereotype.Repository;

@Repository
public class MybatisProjectQrcodeRepository implements ProjectQrcodeRepository {

    private final ProjectQrcodeMapper projectQrcodeMapper;

    public MybatisProjectQrcodeRepository(ProjectQrcodeMapper projectQrcodeMapper) {
        this.projectQrcodeMapper = projectQrcodeMapper;
    }

    @Override
    public List<ProjectQrcodeRecord> findQrcodes(ProjectQrcodeQuery query) {
        return projectQrcodeMapper.findQrcodes(query.projectId(), query.status());
    }

    @Override
    public ProjectQrcodeRecord findById(Long id) {
        return projectQrcodeMapper.findQrcodeById(id);
    }

    @Override
    public ProjectQrcodeRecord create(ProjectQrcodeMutation mutation) {
        OpProjectQrcode qrcode = new OpProjectQrcode();
        apply(qrcode, mutation);
        LocalDateTime now = LocalDateTime.now();
        qrcode.setCreatedAt(now);
        qrcode.setUpdatedAt(now);
        qrcode.setDeleted(0);
        projectQrcodeMapper.insert(qrcode);
        return findById(qrcode.getId());
    }

    @Override
    public ProjectQrcodeRecord update(Long id, ProjectQrcodeMutation mutation) {
        projectQrcodeMapper.update(null, new LambdaUpdateWrapper<OpProjectQrcode>()
                .eq(OpProjectQrcode::getId, id)
                .eq(OpProjectQrcode::getDeleted, 0)
                .set(OpProjectQrcode::getProjectId, mutation.projectId())
                .set(OpProjectQrcode::getName, mutation.name())
                .set(OpProjectQrcode::getFileId, mutation.fileId())
                .set(OpProjectQrcode::getImageUrl, mutation.imageUrl())
                .set(OpProjectQrcode::getAudience, mutation.audience())
                .set(OpProjectQrcode::getDescription, mutation.description())
                .set(OpProjectQrcode::getStatus, mutation.status())
                .set(OpProjectQrcode::getUpdatedAt, LocalDateTime.now()));
        return findById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        projectQrcodeMapper.update(null, new LambdaUpdateWrapper<OpProjectQrcode>()
                .eq(OpProjectQrcode::getId, id)
                .eq(OpProjectQrcode::getDeleted, 0)
                .set(OpProjectQrcode::getStatus, status)
                .set(OpProjectQrcode::getUpdatedAt, LocalDateTime.now()));
    }

    @Override
    public void softDelete(Long id) {
        projectQrcodeMapper.update(null, new LambdaUpdateWrapper<OpProjectQrcode>()
                .eq(OpProjectQrcode::getId, id)
                .set(OpProjectQrcode::getDeleted, 1)
                .set(OpProjectQrcode::getUpdatedAt, LocalDateTime.now()));
    }

    private void apply(OpProjectQrcode qrcode, ProjectQrcodeMutation mutation) {
        qrcode.setProjectId(mutation.projectId());
        qrcode.setName(mutation.name());
        qrcode.setFileId(mutation.fileId());
        qrcode.setImageUrl(mutation.imageUrl());
        qrcode.setAudience(mutation.audience());
        qrcode.setDescription(mutation.description());
        qrcode.setStatus(mutation.status());
    }
}
