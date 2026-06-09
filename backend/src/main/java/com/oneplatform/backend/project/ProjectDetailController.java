package com.oneplatform.backend.project;

import java.util.List;

import com.oneplatform.backend.common.ApiResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/project/{projectId}")
public class ProjectDetailController {

    private final ProjectDetailService projectDetailService;

    public ProjectDetailController(ProjectDetailService projectDetailService) {
        this.projectDetailService = projectDetailService;
    }

    @GetMapping("/detail-content")
    public ApiResponse<ProjectDetailContentResponse> getDetailContent(@PathVariable Long projectId) {
        return ApiResponse.success(projectDetailService.getDetailContent(projectId));
    }

    @PutMapping("/addresses")
    public ApiResponse<List<ProjectAddressResponse>> saveAddresses(
            @PathVariable Long projectId,
            @RequestBody ProjectAddressListRequest request
    ) {
        return ApiResponse.success(projectDetailService.saveAddresses(projectId, request));
    }

    @PutMapping("/instruction")
    public ApiResponse<ProjectInstructionResponse> saveInstruction(
            @PathVariable Long projectId,
            @RequestBody ProjectInstructionRequest request
    ) {
        return ApiResponse.success(projectDetailService.saveInstruction(projectId, request));
    }
}
