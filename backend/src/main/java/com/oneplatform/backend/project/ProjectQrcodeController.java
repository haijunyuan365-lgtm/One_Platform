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
@RequestMapping("/admin/project/qrcode")
public class ProjectQrcodeController {

    private final ProjectQrcodeService projectQrcodeService;

    public ProjectQrcodeController(ProjectQrcodeService projectQrcodeService) {
        this.projectQrcodeService = projectQrcodeService;
    }

    @GetMapping("/list")
    public ApiResponse<List<ProjectQrcodeResponse>> list(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Integer status
    ) {
        return ApiResponse.success(projectQrcodeService.list(new ProjectQrcodeQuery(projectId, status)));
    }

    @PostMapping
    public ApiResponse<ProjectQrcodeResponse> save(@RequestBody ProjectQrcodeSaveRequest request) {
        return ApiResponse.success(projectQrcodeService.save(request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        projectQrcodeService.delete(id);
        return ApiResponse.success(true);
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Boolean> updateStatus(@PathVariable Long id, @RequestBody ProjectQrcodeStatusRequest request) {
        projectQrcodeService.updateStatus(id, request);
        return ApiResponse.success(true);
    }
}
