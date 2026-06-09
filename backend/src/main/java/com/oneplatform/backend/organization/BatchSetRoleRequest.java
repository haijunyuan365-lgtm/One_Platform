package com.oneplatform.backend.organization;

import java.util.List;

public record BatchSetRoleRequest(List<Long> ids, List<Long> roleIds) {
}
