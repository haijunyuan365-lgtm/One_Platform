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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProjectQrcodeController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProjectQrcodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectQrcodeService projectQrcodeService;

    @Test
    void listReturnsQrcodeRowsForAdminPage() throws Exception {
        when(projectQrcodeService.list(any())).thenReturn(List.of(sampleQrcode()));

        mockMvc.perform(get("/admin/project/qrcode/list")
                        .param("projectId", "1")
                        .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].projectName").value("Company Portal"))
                .andExpect(jsonPath("$.data[0].image").value("/assets/qrcode/portal.png"))
                .andExpect(jsonPath("$.data[0].updateTime").value("2026-06-05 10:00:00"));

        verify(projectQrcodeService).list(any());
    }

    @Test
    void saveDeleteAndStatusUseAdminQrcodeRoutes() throws Exception {
        when(projectQrcodeService.save(any())).thenReturn(sampleQrcode());

        String saveBody = """
                {
                  "id": 1,
                  "projectId": 1,
                  "name": "portal mobile entry",
                  "image": "/assets/qrcode/portal.png",
                  "audience": "all staff",
                  "description": "scan to open portal",
                  "status": 1
                }
                """;

        mockMvc.perform(post("/admin/project/qrcode").contentType(MediaType.APPLICATION_JSON).content(saveBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
        mockMvc.perform(put("/admin/project/qrcode/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
        mockMvc.perform(delete("/admin/project/qrcode/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));

        verify(projectQrcodeService).save(any());
        verify(projectQrcodeService).updateStatus(eq(1L), any());
        verify(projectQrcodeService).delete(1L);
    }

    private static ProjectQrcodeResponse sampleQrcode() {
        return new ProjectQrcodeResponse(
                1L,
                1L,
                "Company Portal",
                "portal mobile entry",
                "/assets/qrcode/portal.png",
                "all staff",
                "scan to open portal",
                1,
                "2026-06-05 10:00:00"
        );
    }
}
