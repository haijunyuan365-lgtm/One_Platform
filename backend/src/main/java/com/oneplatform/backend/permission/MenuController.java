package com.oneplatform.backend.permission;

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
@RequestMapping("/admin/permission/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/list")
    public ApiResponse<List<MenuResponse>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status
    ) {
        return ApiResponse.success(menuService.list(new MenuQuery(name, status)));
    }

    @PostMapping
    public ApiResponse<MenuResponse> add(@RequestBody MenuSaveRequest request) {
        return ApiResponse.success(menuService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<MenuResponse> update(@PathVariable Long id, @RequestBody MenuSaveRequest request) {
        return ApiResponse.success(menuService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestBody MenuStatusRequest request) {
        menuService.updateStatus(id, request);
        return ApiResponse.success(null);
    }
}
