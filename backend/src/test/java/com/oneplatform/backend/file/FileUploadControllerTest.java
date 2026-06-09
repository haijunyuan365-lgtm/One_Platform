package com.oneplatform.backend.file;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FileUploadController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(FileUploadProperties.class)
class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileUploadService fileUploadService;

    @Test
    void uploadRouteReturnsStoredFileMetadata() throws Exception {
        when(fileUploadService.upload(any(), eq("qrcode"))).thenReturn(new FileUploadResponse(
                1L,
                "portal.png",
                "/uploads/qrcode/20260605/uuid.png",
                "image/png",
                4L,
                "qrcode",
                "2026-06-05 10:00:00"
        ));
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "portal.png",
                "image/png",
                new byte[] {1, 2, 3, 4}
        );

        mockMvc.perform(multipart("/admin/file/upload")
                        .file(file)
                        .param("bizType", "qrcode"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.url").value("/uploads/qrcode/20260605/uuid.png"))
                .andExpect(jsonPath("$.data.bizType").value("qrcode"));

        verify(fileUploadService).upload(any(), eq("qrcode"));
    }
}
