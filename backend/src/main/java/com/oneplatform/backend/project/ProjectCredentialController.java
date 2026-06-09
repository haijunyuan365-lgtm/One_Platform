package com.oneplatform.backend.project;

import java.util.List;

import com.oneplatform.backend.common.ApiResponse;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/project/credential")
public class ProjectCredentialController {

    private final ProjectCredentialService projectCredentialService;

    public ProjectCredentialController(ProjectCredentialService projectCredentialService) {
        this.projectCredentialService = projectCredentialService;
    }

    @GetMapping("/list")
    public ApiResponse<List<ProjectCredentialResponse>> list(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String environment,
            @RequestParam(required = false) Integer status
    ) {
        return ApiResponse.success(projectCredentialService.list(new ProjectCredentialQuery(projectId, environment, status)));
    }

    @PostMapping
    public ApiResponse<ProjectCredentialResponse> save(@RequestBody ProjectCredentialSaveRequest request) {
        return ApiResponse.success(projectCredentialService.save(request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        projectCredentialService.delete(id);
        return ApiResponse.success(true);
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Boolean> updateStatus(@PathVariable Long id, @RequestBody ProjectCredentialStatusRequest request) {
        projectCredentialService.updateStatus(id, request);
        return ApiResponse.success(true);
    }

    @PostMapping("/{id}/reveal")
    public ApiResponse<String> reveal(@PathVariable Long id) {
        return ApiResponse.success(projectCredentialService.reveal(id));
    }

    @PostMapping("/{id}/copy")
    public ApiResponse<String> copy(@PathVariable Long id, @RequestBody ProjectCredentialCopyRequest request) {
        return ApiResponse.success(projectCredentialService.copy(id, request));
    }
}
