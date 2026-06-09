package com.oneplatform.backend.project;

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

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Test
    void listAndOptionsReturnAdminProjectShape() throws Exception {
        when(projectService.options()).thenReturn(List.of(new ProjectOptionResponse(1L, "公司统一门户", "统一门户")));
        when(projectService.list(any())).thenReturn(new PageResponse<>(
                List.of(sampleProject()),
                1,
                1,
                10
        ));

        mockMvc.perform(get("/admin/project/options"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].shortName").value("统一门户"));

        mockMvc.perform(get("/admin/project/list")
                        .param("keyword", "门户")
                        .param("category", "内部系统")
                        .param("enabled", "1")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].tags[1]").value("内网"))
                .andExpect(jsonPath("$.data.list[0].maintainer").value("管理员"));
    }

    @Test
    void writeRoutesCreateUpdateDeleteAndToggleEnabled() throws Exception {
        when(projectService.create(any())).thenReturn(sampleProject());
        when(projectService.update(eq(1L), any())).thenReturn(sampleProject());

        String body = """
                {
                  "name":"公司统一门户",
                  "shortName":"统一门户",
                  "category":"内部系统",
                  "tags":["正式","内网"],
                  "description":"公司入口",
                  "maintainerId":1,
                  "maintainer":"管理员"
                }
                """;

        mockMvc.perform(post("/admin/project").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("公司统一门户"));
        mockMvc.perform(put("/admin/project/1").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
        mockMvc.perform(put("/admin/project/1/enabled").contentType(MediaType.APPLICATION_JSON).content("{\"enabled\":0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
        mockMvc.perform(delete("/admin/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));

        verify(projectService).create(any());
        verify(projectService).update(eq(1L), any());
        verify(projectService).updateEnabled(eq(1L), any());
        verify(projectService).delete(1L);
    }

    private static ProjectResponse sampleProject() {
        return new ProjectResponse(
                1L,
                "公司统一门户",
                "统一门户",
                "/assets/project/portal.svg",
                "内部系统",
                List.of("正式", "内网"),
                "公司入口",
                1L,
                "管理员",
                1,
                1,
                "可用",
                "2026-06-05 09:10:00",
                126,
                null,
                "2026-06-05 09:00:00",
                "2026-06-05 09:00:00"
        );
    }
}
