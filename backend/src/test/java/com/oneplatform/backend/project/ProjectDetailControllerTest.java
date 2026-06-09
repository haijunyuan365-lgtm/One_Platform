package com.oneplatform.backend.project;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

@WebMvcTest(ProjectDetailController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProjectDetailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectDetailService projectDetailService;

    @Test
    void getDetailContentReturnsProjectAddressesAndInstruction() throws Exception {
        when(projectDetailService.getDetailContent(1L)).thenReturn(sampleDetail());

        mockMvc.perform(get("/admin/project/1/detail-content"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.project.name").value("公司统一门户"))
                .andExpect(jsonPath("$.data.addresses[0].url").value("https://portal.example.test"))
                .andExpect(jsonPath("$.data.instruction.maintainer").value("管理员"));
    }

    @Test
    void saveAddressesAndInstructionUsePathProjectId() throws Exception {
        when(projectDetailService.saveAddresses(eq(1L), any())).thenReturn(List.of(sampleAddress()));
        when(projectDetailService.saveInstruction(eq(1L), any())).thenReturn(sampleInstruction());

        String addressesBody = """
                {
                  "list": [
                    {
                      "id": 1,
                      "projectId": 999,
                      "name": "正式地址",
                      "type": "Web",
                      "url": "https://portal.example.test",
                      "isDefault": 1,
                      "isDetection": 1,
                      "sort": 1,
                      "status": 1
                    }
                  ]
                }
                """;
        String instructionBody = """
                {
                  "projectId": 999,
                  "browserRequirement": "Chrome 120+",
                  "vpnRequirement": "需要 VPN",
                  "notes": "注意事项",
                  "maintainer": "管理员",
                  "contactPhone": "13800138000"
                }
                """;

        mockMvc.perform(put("/admin/project/1/addresses").contentType(MediaType.APPLICATION_JSON).content(addressesBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].projectId").value(1));
        mockMvc.perform(put("/admin/project/1/instruction").contentType(MediaType.APPLICATION_JSON).content(instructionBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.projectId").value(1));

        verify(projectDetailService).saveAddresses(eq(1L), any());
        verify(projectDetailService).saveInstruction(eq(1L), any());
    }

    private static ProjectDetailContentResponse sampleDetail() {
        return new ProjectDetailContentResponse(
                new ProjectResponse(1L, "公司统一门户", "统一门户", "/assets/project/portal.svg", "内部系统", List.of("正式", "内网"), "公司入口", 1L, "管理员", 1, 1, "可用", "2026-06-05 09:10:00", 126, null, "2026-06-05 09:00:00", "2026-06-05 09:00:00"),
                List.of(sampleAddress()),
                sampleInstruction()
        );
    }

    private static ProjectAddressResponse sampleAddress() {
        return new ProjectAddressResponse(1L, 1L, "正式地址", "Web", "https://portal.example.test", 1, 1, 1, 1);
    }

    private static ProjectInstructionResponse sampleInstruction() {
        return new ProjectInstructionResponse(1L, "Chrome 120+", "需要 VPN", "注意事项", "管理员", "13800138000", "2026-06-05 09:00:00");
    }
}
