package com.example.song_service.controller;

import com.example.song_service.dto.DeleteSongResponse;
import com.example.song_service.dto.IdResponse;
import com.example.song_service.dto.SongDto;
import com.example.song_service.service.SongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @PostMapping
    public ResponseEntity<IdResponse> create(@Valid @RequestBody SongDto songDto) {
        Integer id = songService.create(songDto);
        return ResponseEntity.ok(new IdResponse(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongDto> get(@PathVariable Integer id) {
        return ResponseEntity.ok(songService.getById(id));
    }

    @DeleteMapping
    public ResponseEntity<DeleteSongResponse> delete(@RequestParam("id") String id) {
        return ResponseEntity.ok(new DeleteSongResponse(songService.delete(id)));
    }
}
