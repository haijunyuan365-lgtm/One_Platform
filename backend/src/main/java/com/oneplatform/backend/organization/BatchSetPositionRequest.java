package com.oneplatform.backend.organization;

import java.util.List;

public record BatchSetPositionRequest(List<Long> ids, String positionName) {
}
