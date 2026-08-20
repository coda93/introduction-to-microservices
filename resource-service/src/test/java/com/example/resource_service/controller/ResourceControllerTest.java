package com.example.resource_service.controller;

import com.example.resource_service.exception.ResourceNotFoundException;
import com.example.resource_service.service.ResourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResourceController.class)
class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResourceService resourceService;

    @Test
    void uploadReturnsId() throws Exception {
        when(resourceService.upload(any())).thenReturn(1);

        mockMvc.perform(post("/resources")
                        .contentType("audio/mpeg")
                        .content(new byte[]{1, 2, 3}))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getReturnsAudioBytes() throws Exception {
        when(resourceService.getData(1)).thenReturn(new byte[]{10, 20, 30});

        mockMvc.perform(get("/resources/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("audio/mpeg"))
                .andExpect(content().bytes(new byte[]{10, 20, 30}));
    }

    @Test
    void getWithNonIntegerIdReturns400() throws Exception {
        mockMvc.perform(get("/resources/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400"));
    }

    @Test
    void getMissingResourceReturns404() throws Exception {
        when(resourceService.getData(99)).thenThrow(new ResourceNotFoundException(99));

        mockMvc.perform(get("/resources/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("404"))
                .andExpect(jsonPath("$.errorMessage").value("Resource with ID=99 not found"));
    }

    @Test
    void deleteReturnsIds() throws Exception {
        when(resourceService.delete(eq("1,2"))).thenReturn(List.of(1, 2));

        mockMvc.perform(delete("/resources").param("id", "1,2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids[0]").value(1))
                .andExpect(jsonPath("$.ids[1]").value(2));
    }
}
