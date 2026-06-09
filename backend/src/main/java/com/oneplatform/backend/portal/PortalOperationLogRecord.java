package com.oneplatform.backend.portal;

public record PortalOperationLogRecord(
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
