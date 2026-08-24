package com.example.song_service.mapper;

import com.example.song_service.dto.SongDto;
import com.example.song_service.model.Song;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class SongMapperTest {

    private final SongMapper mapper = Mappers.getMapper(SongMapper.class);

    @Test
    void toEntityMapsAllFields() {
        SongDto dto = new SongDto(1, "Name", "Artist", "Album", "02:59", "1977");
        Song entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isEqualTo(1);
        assertThat(entity.getName()).isEqualTo("Name");
        assertThat(entity.getArtist()).isEqualTo("Artist");
        assertThat(entity.getAlbum()).isEqualTo("Album");
        assertThat(entity.getDuration()).isEqualTo("02:59");
        assertThat(entity.getYear()).isEqualTo("1977");
    }

    @Test
    void toDtoMapsAllFields() {
        Song entity = new Song(2, "Name2", "Artist2", "Album2", "03:00", "1990");
        SongDto dto = mapper.toDto(entity);

        assertThat(dto.id()).isEqualTo(2);
        assertThat(dto.name()).isEqualTo("Name2");
        assertThat(dto.artist()).isEqualTo("Artist2");
        assertThat(dto.album()).isEqualTo("Album2");
        assertThat(dto.duration()).isEqualTo("03:00");
        assertThat(dto.year()).isEqualTo("1990");
    }
}
