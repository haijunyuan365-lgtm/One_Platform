package com.oneplatform.backend.portal;

import java.util.List;

import com.oneplatform.backend.common.ApiResponse;
import com.oneplatform.backend.portal.dto.CopyCredentialRequest;
import com.oneplatform.backend.portal.dto.PortalActionLogRequest;
import com.oneplatform.backend.portal.dto.PortalProjectDetailResponse;
import com.oneplatform.backend.portal.dto.PortalProjectQuery;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/portal")
public class PortalController {

    private final PortalService portalService;

    public PortalController(PortalService portalService) {
        this.portalService = portalService;
    }

    @GetMapping("/projects")
    public ApiResponse<List<PortalProjectDetailResponse.ProjectCard>> listProjects(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return ApiResponse.success(portalService.listProjects(
                new PortalProjectQuery(keyword, category, status),
                authorization
        ));
    }

    @GetMapping("/projects/{projectId}")
    public ApiResponse<PortalProjectDetailResponse> getProjectDetail(
            @PathVariable Long projectId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return ApiResponse.success(portalService.getProjectDetail(projectId, authorization));
    }

    @PostMapping("/action-log")
    public ApiResponse<Boolean> recordAction(
            @Valid @RequestBody PortalActionLogRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return ApiResponse.success(portalService.recordAction(request, authorization));
    }

    @PostMapping("/credentials/{credentialId}/reveal")
    public ApiResponse<String> revealCredential(
            @PathVariable Long credentialId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return ApiResponse.success(portalService.revealCredential(credentialId, authorization));
    }

    @PostMapping("/credentials/{credentialId}/copy")
    public ApiResponse<String> copyCredential(
            @PathVariable Long credentialId,
            @Valid @RequestBody CopyCredentialRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return ApiResponse.success(portalService.copyCredential(credentialId, request, authorization));
    }
}
