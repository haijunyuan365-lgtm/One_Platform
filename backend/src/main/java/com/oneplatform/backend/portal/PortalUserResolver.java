package com.oneplatform.backend.portal;

public interface PortalUserResolver {

    PortalUserContext resolve(String authorization);
}
