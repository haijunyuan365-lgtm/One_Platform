package com.oneplatform.backend.organization;

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
@RequestMapping("/admin/organization/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    public ApiResponse<PageResponse<AdminUserResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize
    ) {
        return ApiResponse.success(userService.list(new UserQuery(keyword, departmentId, status, page, pageSize)));
    }

    @PostMapping("/add")
    public ApiResponse<AdminUserResponse> add(@RequestBody UserSaveRequest request) {
        return ApiResponse.success(userService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<AdminUserResponse> update(@PathVariable Long id, @RequestBody UserSaveRequest request) {
        return ApiResponse.success(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/batch-delete")
    public ApiResponse<Void> batchDelete(@RequestBody BatchIdsRequest request) {
        userService.batchDelete(request);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestBody UserStatusRequest request) {
        userService.updateStatus(id, request);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/reset-password")
    public ApiResponse<Void> resetPassword(@PathVariable Long id, @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(id, request);
        return ApiResponse.success(null);
    }

    @PostMapping("/batch-set-position")
    public ApiResponse<Void> batchSetPosition(@RequestBody BatchSetPositionRequest request) {
        userService.batchSetPosition(request);
        return ApiResponse.success(null);
    }

    @PostMapping("/batch-set-role")
    public ApiResponse<Void> batchSetRole(@RequestBody BatchSetRoleRequest request) {
        userService.batchSetRole(request);
        return ApiResponse.success(null);
    }
}
