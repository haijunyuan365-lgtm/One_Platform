package com.oneplatform.backend.organization;

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

import com.oneplatform.backend.common.PageResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void userListReturnsPageResponseForAdminTable() throws Exception {
        when(userService.list(any())).thenReturn(new PageResponse<>(
                List.of(new AdminUserResponse(
                        2L,
                        "lisi",
                        "李四",
                        "李四",
                        null,
                        "lisi@one-platform.local",
                        "13800138001",
                        32L,
                        "运维部",
                        null,
                        "运维负责人",
                        1,
                        "2026-06-05 09:00:00",
                        "2026-06-05 09:00:00",
                        null,
                        null,
                        null,
                        List.of(new AdminUserResponse.RoleBrief(2L, "项目管理员", "project_admin"))
                )),
                1,
                1,
                10
        ));

        mockMvc.perform(get("/admin/organization/user/list")
                        .param("keyword", "li")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list[0].username").value("lisi"))
                .andExpect(jsonPath("$.data.list[0].roles[0].name").value("项目管理员"));
    }

    @Test
    void addAndUpdateUserUseAdminOrganizationRoutes() throws Exception {
        AdminUserResponse response = new AdminUserResponse(
                6L,
                "newuser",
                "新用户",
                "新用户",
                null,
                "newuser@one-platform.local",
                "13900139000",
                21L,
                "研发部",
                null,
                "研发工程师",
                1,
                "2026-06-05 10:00:00",
                "2026-06-05 10:00:00",
                null,
                null,
                null,
                List.of(new AdminUserResponse.RoleBrief(4L, "普通员工", "employee"))
        );
        when(userService.create(any())).thenReturn(response);
        when(userService.update(eq(6L), any())).thenReturn(response);

        String body = """
                {
                  "username": "newuser",
                  "realName": "新用户",
                  "departmentId": 21,
                  "positionName": "研发工程师",
                  "roleIds": [4]
                }
                """;

        mockMvc.perform(post("/admin/organization/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("newuser"));

        mockMvc.perform(put("/admin/organization/user/6")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(6));

        verify(userService).create(any());
        verify(userService).update(eq(6L), any());
    }

    @Test
    void userWriteActionsReturnUnifiedSuccessResponse() throws Exception {
        mockMvc.perform(delete("/admin/organization/user/6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(post("/admin/organization/user/batch-delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ids\":[6,7]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(put("/admin/organization/user/6/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(put("/admin/organization/user/6/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"Abcd1234\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(post("/admin/organization/user/batch-set-position")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ids\":[6,7],\"positionName\":\"交付顾问\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(post("/admin/organization/user/batch-set-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ids\":[6,7],\"roleIds\":[2,4]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(userService).delete(6L);
        verify(userService).batchDelete(any());
        verify(userService).updateStatus(eq(6L), any());
        verify(userService).resetPassword(eq(6L), any());
        verify(userService).batchSetPosition(any());
        verify(userService).batchSetRole(any());
    }
}
