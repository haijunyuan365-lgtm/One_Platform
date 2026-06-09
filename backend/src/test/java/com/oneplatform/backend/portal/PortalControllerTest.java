package com.oneplatform.backend.portal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.oneplatform.backend.portal.dto.CopyCredentialRequest;
import com.oneplatform.backend.portal.dto.PortalProjectDetailResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PortalController.class)
@AutoConfigureMockMvc(addFilters = false)
class PortalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PortalService portalService;

    @Test
    void projectsRouteUsesUnifiedSuccessResponse() throws Exception {
        when(portalService.listProjects(any(), eq("Bearer token"))).thenReturn(List.of(
                new PortalProjectDetailResponse.ProjectCard(
                        1L,
                        "公司统一门户",
                        "统一门户",
                        "/portal.svg",
                        "内部系统",
                        List.of("正式", "内网"),
                        "统一入口",
                        "管理员",
                        "可用",
                        "2026-06-05 09:10:00",
                        126,
                        null
                )
        ));

        mockMvc.perform(get("/app/portal/projects")
                        .header("Authorization", "Bearer token")
                        .param("keyword", "门户"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("公司统一门户"));
    }

    @Test
    void copyCredentialRouteReturnsCopiedValueInUnifiedResponse() throws Exception {
        when(portalService.copyCredential(eq(9L), any(CopyCredentialRequest.class), eq("Bearer token")))
                .thenReturn("secret-value");

        mockMvc.perform(post("/app/portal/credentials/9/copy")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"field\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("secret-value"));
    }
}
