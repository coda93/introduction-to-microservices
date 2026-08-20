package com.example.song_service.controller;

import com.example.song_service.dto.SongDto;
import com.example.song_service.exception.SongAlreadyExistsException;
import com.example.song_service.exception.SongNotFoundException;
import com.example.song_service.service.SongService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SongController.class)
class SongControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SongService songService;

    private static final String VALID_BODY = """
            {
              "id": 1,
              "name": "We are the champions",
              "artist": "Queen",
              "album": "News of the world",
              "duration": "02:59",
              "year": "1977"
            }
            """;

    @Test
    void createReturnsId() throws Exception {
        when(songService.create(any(SongDto.class))).thenReturn(1);

        mockMvc.perform(post("/songs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createWithInvalidFieldsReturnsValidationError() throws Exception {
        String invalid = """
                {
                  "id": 1,
                  "name": "Name",
                  "artist": "Artist",
                  "album": "Album",
                  "duration": "2:5",
                  "year": "1800"
                }
                """;

        mockMvc.perform(post("/songs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Validation error"))
                .andExpect(jsonPath("$.errorCode").value("400"))
                .andExpect(jsonPath("$.details.duration").exists())
                .andExpect(jsonPath("$.details.year").exists());
    }

    @Test
    void createDuplicateReturnsConflict() throws Exception {
        when(songService.create(any(SongDto.class))).thenThrow(new SongAlreadyExistsException(1));

        mockMvc.perform(post("/songs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("409"));
    }

    @Test
    void getReturnsSong() throws Exception {
        when(songService.getById(1)).thenReturn(
                new SongDto(1, "We are the champions", "Queen", "News of the world", "02:59", "1977"));

        mockMvc.perform(get("/songs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("We are the champions"))
                .andExpect(jsonPath("$.duration").value("02:59"));
    }

    @Test
    void getMissingReturns404() throws Exception {
        when(songService.getById(9)).thenThrow(new SongNotFoundException(9));

        mockMvc.perform(get("/songs/9"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("404"));
    }

    @Test
    void getWithNonIntegerIdReturns400() throws Exception {
        mockMvc.perform(get("/songs/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400"));
    }

    @Test
    void deleteReturnsIds() throws Exception {
        when(songService.delete(eq("1,2"))).thenReturn(List.of(1, 2));

        mockMvc.perform(delete("/songs").param("id", "1,2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids[0]").value(1))
                .andExpect(jsonPath("$.ids[1]").value(2));
    }
}
