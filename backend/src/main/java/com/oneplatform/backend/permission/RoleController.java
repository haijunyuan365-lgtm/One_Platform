package com.oneplatform.backend.permission;

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
@RequestMapping("/admin/permission/role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/list")
    public ApiResponse<PageResponse<RoleResponse>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize
    ) {
        String queryName = name == null ? keyword : name;
        return ApiResponse.success(roleService.list(new RoleQuery(queryName, status, page, pageSize)));
    }

    @PostMapping({"/add", ""})
    public ApiResponse<RoleResponse> add(@RequestBody RoleSaveRequest request) {
        return ApiResponse.success(roleService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<RoleResponse> update(@PathVariable Long id, @RequestBody RoleSaveRequest request) {
        return ApiResponse.success(roleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestBody RoleStatusRequest request) {
        roleService.updateStatus(id, request);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}/menus")
    public ApiResponse<List<Long>> getMenus(@PathVariable Long id) {
        return ApiResponse.success(roleService.getMenus(id));
    }

    @PutMapping("/{id}/menus")
    public ApiResponse<Void> updateMenus(@PathVariable Long id, @RequestBody RoleMenusRequest request) {
        roleService.updateMenus(id, request);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/permissions")
    public ApiResponse<Void> updatePermissions(@PathVariable Long id, @RequestBody RoleMenusRequest request) {
        roleService.updatePermissions(id, request);
        return ApiResponse.success(null);
    }
}
