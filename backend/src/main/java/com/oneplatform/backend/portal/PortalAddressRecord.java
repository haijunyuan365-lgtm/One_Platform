package com.oneplatform.backend.portal;

public record PortalAddressRecord(
        Long id,
        String name,
        String type,
        String url,
        Integer isDefault
) {
}
