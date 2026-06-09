package com.oneplatform.backend.project;

public record ProjectCredentialOperationLogRecord(
        Long operatorId,
        String operator,
        String account,
        Long departmentId,
        String department,
        Long projectId,
        String projectName,
        String operationType,
        String target,
        String result,
        String ip,
        String remark
) {
}
