package com.example.song_service.mapper;

import com.example.song_service.dto.SongDto;
import com.example.song_service.model.Song;
import org.springframework.stereotype.Component;

@Component
public class SongMapper {

    public Song toEntity(SongDto dto) {
        return new Song(
                dto.id(),
                dto.name(),
                dto.artist(),
                dto.album(),
                dto.duration(),
                dto.year()
        );
    }

    public SongDto toDto(Song song) {
        return new SongDto(
                song.getId(),
                song.getName(),
                song.getArtist(),
                song.getAlbum(),
                song.getDuration(),
                song.getYear()
        );
    }
}
