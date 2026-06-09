package com.oneplatform.backend.project;

import java.util.List;

public interface ProjectQrcodeRepository {

    List<ProjectQrcodeRecord> findQrcodes(ProjectQrcodeQuery query);

    ProjectQrcodeRecord findById(Long id);

    ProjectQrcodeRecord create(ProjectQrcodeMutation mutation);

    ProjectQrcodeRecord update(Long id, ProjectQrcodeMutation mutation);

    void updateStatus(Long id, Integer status);

    void softDelete(Long id);
}
