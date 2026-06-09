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

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProjectCredentialController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProjectCredentialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectCredentialService projectCredentialService;

    @Test
    void listReturnsCredentialRowsForAdminPage() throws Exception {
        when(projectCredentialService.list(any())).thenReturn(List.of(sampleCredential()));

        mockMvc.perform(get("/admin/project/credential/list")
                        .param("projectId", "1")
                        .param("environment", "prod")
                        .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].projectName").value("Company Portal"))
                .andExpect(jsonPath("$.data[0].passwordMasked").value("************"))
                .andExpect(jsonPath("$.data[0].updateTime").value("2026-06-05 10:00:00"));

        verify(projectCredentialService).list(any());
    }

    @Test
    void saveDeleteStatusRevealAndCopyUseAdminCredentialRoutes() throws Exception {
        when(projectCredentialService.save(any())).thenReturn(sampleCredential());
        when(projectCredentialService.reveal(1L)).thenReturn("Secret#2026");
        when(projectCredentialService.copy(eq(1L), any())).thenReturn("portal_admin");

        String saveBody = """
                {
                  "id": 1,
                  "projectId": 1,
                  "name": "portal admin",
                  "username": "portal_admin",
                  "password": "Secret#2026",
                  "environment": "prod",
                  "description": "admin credential",
                  "expireDate": "2026-12-31",
                  "status": 1
                }
                """;

        mockMvc.perform(post("/admin/project/credential").contentType(MediaType.APPLICATION_JSON).content(saveBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
        mockMvc.perform(put("/admin/project/credential/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
        mockMvc.perform(post("/admin/project/credential/1/reveal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Secret#2026"));
        mockMvc.perform(post("/admin/project/credential/1/copy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"field\":\"username\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("portal_admin"));
        mockMvc.perform(delete("/admin/project/credential/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));

        verify(projectCredentialService).save(any());
        verify(projectCredentialService).updateStatus(eq(1L), any());
        verify(projectCredentialService).reveal(1L);
        verify(projectCredentialService).copy(eq(1L), any());
        verify(projectCredentialService).delete(1L);
    }

    private static ProjectCredentialResponse sampleCredential() {
        return new ProjectCredentialResponse(
                1L,
                1L,
                "Company Portal",
                "portal admin",
                "portal_admin",
                "************",
                "prod",
                "admin credential",
                LocalDate.of(2026, 12, 31),
                1,
                "2026-06-05 10:00:00"
        );
    }
}
