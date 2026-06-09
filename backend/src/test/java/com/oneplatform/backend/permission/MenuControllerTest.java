package com.oneplatform.backend.permission;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MenuController.class)
@AutoConfigureMockMvc(addFilters = false)
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuService menuService;

    @Test
    void menuListReturnsTreeShape() throws Exception {
        when(menuService.list(any())).thenReturn(List.of(
                new MenuResponse(1L, null, "SystemManagement", "系统管理", "directory", "/organization", null, "system:view", "Setting", 1, 1, "2026-06-05 09:00:00", "2026-06-05 09:00:00", List.of(
                        new MenuResponse(11L, 1L, "UserManagement", "用户管理", "menu", "/organization/user", "@/views/organization/user/index.vue", "organization:user:view", "User", 1, 1, "2026-06-05 09:00:00", "2026-06-05 09:00:00", List.of())
                ))
        ));

        mockMvc.perform(get("/admin/permission/menu/list").param("name", "用户"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].children[0].title").value("用户管理"));
    }

    @Test
    void menuWriteRoutesReturnUnifiedSuccessResponse() throws Exception {
        MenuResponse response = new MenuResponse(100L, 4L, "AuditLog", "审计日志", "menu", "/audit/log", "@/views/audit/log/index.vue", "audit:log:view", "List", 42, 1, "2026-06-05 10:00:00", "2026-06-05 10:00:00", List.of());
        when(menuService.create(any())).thenReturn(response);
        when(menuService.update(eq(100L), any())).thenReturn(response);

        String body = "{\"parentId\":4,\"name\":\"AuditLog\",\"title\":\"审计日志\",\"type\":\"menu\",\"path\":\"/audit/log\",\"component\":\"@/views/audit/log/index.vue\",\"permission\":\"audit:log:view\"}";

        mockMvc.perform(post("/admin/permission/menu").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("审计日志"));
        mockMvc.perform(put("/admin/permission/menu/100").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(100));
        mockMvc.perform(put("/admin/permission/menu/100/status").contentType(MediaType.APPLICATION_JSON).content("{\"status\":0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(delete("/admin/permission/menu/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(menuService).create(any());
        verify(menuService).update(eq(100L), any());
        verify(menuService).updateStatus(eq(100L), any());
        verify(menuService).delete(100L);
    }
}
