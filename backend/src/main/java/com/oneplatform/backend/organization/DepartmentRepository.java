package com.oneplatform.backend.organization;

import java.util.List;

public interface DepartmentRepository {

    List<DepartmentRecord> findAll();

    DepartmentRecord findById(Long id);

    long countEnabledUsers(Long departmentId);

    void updateStatus(Long id, Integer status);
}
