package com.oneplatform.backend.organization;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

@WebMvcTest(DepartmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartmentService departmentService;

    @Test
    void departmentListReturnsDataListForAdminPages() throws Exception {
        when(departmentService.list(any())).thenReturn(new PageResponse<>(
                List.of(new DepartmentResponse(
                        1L,
                        null,
                        "公司总部",
                        "HQ",
                        "公司",
                        "管理员",
                        "",
                        1,
                        1,
                        "2026-06-05 09:00:00",
                        "2026-06-05 09:00:00",
                        List.of()
                )),
                1,
                1,
                1
        ));

        mockMvc.perform(get("/admin/organization/department/list").param("name", "公司"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list[0].name").value("公司总部"));
    }

    @Test
    void updateStatusRouteUsesUnifiedResponse() throws Exception {
        when(departmentService.updateStatus(any(), any())).thenReturn(true);

        mockMvc.perform(put("/admin/organization/department/21/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }
}
