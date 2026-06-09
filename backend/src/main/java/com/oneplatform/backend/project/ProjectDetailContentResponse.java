package com.oneplatform.backend.project;

import java.util.List;

public record ProjectDetailContentResponse(
        ProjectResponse project,
        List<ProjectAddressResponse> addresses,
        ProjectInstructionResponse instruction
) {
}
