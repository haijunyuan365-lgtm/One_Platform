package com.oneplatform.backend.organization;

import com.oneplatform.backend.common.ApiResponse;
import com.oneplatform.backend.common.PageResponse;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/organization/department")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/list")
    public ApiResponse<PageResponse<DepartmentResponse>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status
    ) {
        return ApiResponse.success(departmentService.list(new DepartmentQuery(name, status)));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Boolean> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentStatusRequest request
    ) {
        return ApiResponse.success(departmentService.updateStatus(id, request));
    }
}
