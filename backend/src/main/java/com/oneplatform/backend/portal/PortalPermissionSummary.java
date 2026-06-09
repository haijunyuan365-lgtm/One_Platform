package com.oneplatform.backend.portal;

public record PortalPermissionSummary(
        boolean visible,
        boolean passwordView,
        boolean passwordCopy,
        boolean qrcodeView
) {
}
