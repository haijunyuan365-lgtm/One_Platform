package com.oneplatform.backend.portal;

import java.util.List;

class TestPortalUserResolver implements PortalUserResolver {

    @Override
    public PortalUserContext resolve(String authorization) {
        return new PortalUserContext(1L, "admin", 21L, "研发部", List.of(1L, 4L));
    }
}
