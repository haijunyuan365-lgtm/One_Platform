package com.oneplatform.backend.permission;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.oneplatform.backend.common.PageResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RoleController.class)
@AutoConfigureMockMvc(addFilters = false)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @Test
    void roleListReturnsAdminPageShape() throws Exception {
        when(roleService.list(any())).thenReturn(new PageResponse<>(
                List.of(new RoleResponse(2L, "project_admin", "项目管理员", "负责项目维护", List.of(2L, 21L), 2L, 2, 1, "2026-06-05 09:00:00", "2026-06-05 09:00:00")),
                1,
                1,
                10
        ));

        mockMvc.perform(get("/admin/permission/role/list")
                        .param("name", "项目")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list[0].menuIds[0]").value(2))
                .andExpect(jsonPath("$.data.list[0].userCount").value(2));
    }

    @Test
    void roleWriteRoutesSupportBothAddPathsAndPermissionAliases() throws Exception {
        RoleResponse response = new RoleResponse(6L, "auditor", "审计员", "审计查看", List.of(4L, 41L), 0L, 9, 1, "2026-06-05 10:00:00", "2026-06-05 10:00:00");
        when(roleService.create(any())).thenReturn(response);
        when(roleService.update(eq(6L), any())).thenReturn(response);
        when(roleService.getMenus(6L)).thenReturn(List.of(4L, 41L));

        String body = "{\"code\":\"auditor\",\"name\":\"审计员\",\"menuIds\":[4,41]}";

        mockMvc.perform(post("/admin/permission/role/add").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code").value("auditor"));
        mockMvc.perform(post("/admin/permission/role").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(6));
        mockMvc.perform(put("/admin/permission/role/6").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(get("/admin/permission/role/6/menus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[1]").value(41));
        mockMvc.perform(put("/admin/permission/role/6/menus").contentType(MediaType.APPLICATION_JSON).content("{\"menuIds\":[4,41]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(put("/admin/permission/role/6/permissions").contentType(MediaType.APPLICATION_JSON).content("{\"menuIds\":[4,41]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(put("/admin/permission/role/6/status").contentType(MediaType.APPLICATION_JSON).content("{\"status\":0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(delete("/admin/permission/role/6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(roleService, times(2)).create(any());
        verify(roleService).update(eq(6L), any());
        verify(roleService).updateMenus(eq(6L), any());
        verify(roleService).updatePermissions(eq(6L), any());
        verify(roleService).updateStatus(eq(6L), any());
        verify(roleService).delete(6L);
    }
}
