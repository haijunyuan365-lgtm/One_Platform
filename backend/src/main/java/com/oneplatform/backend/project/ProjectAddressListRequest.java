package com.oneplatform.backend.project;

import java.util.List;

public record ProjectAddressListRequest(
        List<ProjectAddressRequest> list
) {
}
