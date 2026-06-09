package com.oneplatform.backend.project;

import java.util.List;

import com.oneplatform.backend.common.ApiResponse;
import com.oneplatform.backend.common.PageResponse;

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
@RequestMapping("/admin/project")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/options")
    public ApiResponse<List<ProjectOptionResponse>> options() {
        return ApiResponse.success(projectService.options());
    }

    @GetMapping("/list")
    public ApiResponse<PageResponse<ProjectResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String maintainer,
            @RequestParam(required = false) Integer enabled,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize
    ) {
        return ApiResponse.success(projectService.list(new ProjectQuery(keyword, category, maintainer, enabled, status, page, pageSize)));
    }

    @PostMapping
    public ApiResponse<ProjectResponse> create(@RequestBody ProjectSaveRequest request) {
        return ApiResponse.success(projectService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ProjectResponse> update(@PathVariable Long id, @RequestBody ProjectSaveRequest request) {
        return ApiResponse.success(projectService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        projectService.delete(id);
        return ApiResponse.success(true);
    }

    @PutMapping("/{id}/enabled")
    public ApiResponse<Boolean> updateEnabled(@PathVariable Long id, @RequestBody ProjectEnabledRequest request) {
        projectService.updateEnabled(id, request);
        return ApiResponse.success(true);
    }
}
