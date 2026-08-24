package com.example.song_service.mapper;

import com.example.song_service.dto.SongDto;
import com.example.song_service.model.Song;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SongMapper {

    Song toEntity(SongDto dto);

    SongDto toDto(Song song);
}
