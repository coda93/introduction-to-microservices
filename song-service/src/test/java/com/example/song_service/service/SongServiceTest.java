package com.example.song_service.service;

import com.example.song_service.dto.SongDto;
import com.example.song_service.exception.InvalidRequestException;
import com.example.song_service.exception.SongAlreadyExistsException;
import com.example.song_service.exception.SongNotFoundException;
import com.example.song_service.mapper.SongMapper;
import com.example.song_service.model.Song;
import com.example.song_service.repository.SongRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SongServiceTest {

    @Mock
    private SongRepository songRepository;

    // Real mapper is simple enough to use directly.
    private final SongMapper songMapper = new SongMapper();

    private SongService songService;

    private SongDto sampleDto() {
        return new SongDto(1, "We are the champions", "Queen", "News of the world", "02:59", "1977");
    }

    private Song sampleEntity() {
        return new Song(1, "We are the champions", "Queen", "News of the world", "02:59", "1977");
    }

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        songService = new SongService(songRepository, songMapper);
    }

    @Test
    void createSavesAndReturnsId() {
        when(songRepository.existsById(1)).thenReturn(false);
        when(songRepository.save(any(Song.class))).thenReturn(sampleEntity());

        Integer id = songService.create(sampleDto());

        assertThat(id).isEqualTo(1);
        verify(songRepository).save(any(Song.class));
    }

    @Test
    void createThrowsConflictWhenExists() {
        when(songRepository.existsById(1)).thenReturn(true);

        assertThatThrownBy(() -> songService.create(sampleDto()))
                .isInstanceOf(SongAlreadyExistsException.class);
        verify(songRepository, never()).save(any());
    }

    @Test
    void getByIdReturnsDto() {
        when(songRepository.findById(1)).thenReturn(Optional.of(sampleEntity()));

        SongDto dto = songService.getById(1);

        assertThat(dto.name()).isEqualTo("We are the champions");
        assertThat(dto.artist()).isEqualTo("Queen");
    }

    @Test
    void getByIdThrowsWhenMissing() {
        when(songRepository.findById(7)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> songService.getById(7))
                .isInstanceOf(SongNotFoundException.class);
    }

    @Test
    void getByIdRejectsNonPositiveId() {
        assertThatThrownBy(() -> songService.getById(0))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void deleteReturnsOnlyExistingIds() {
        when(songRepository.existsById(1)).thenReturn(true);
        when(songRepository.existsById(2)).thenReturn(false);

        List<Integer> deleted = songService.delete("1,2");

        assertThat(deleted).containsExactly(1);
        verify(songRepository).deleteById(1);
        verify(songRepository, never()).deleteById(2);
    }

    @Test
    void deleteRejectsTooLongCsv() {
        String csv = "1,".repeat(150);
        assertThatThrownBy(() -> songService.delete(csv))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void deleteRejectsInvalidFormat() {
        assertThatThrownBy(() -> songService.delete("1,x"))
                .isInstanceOf(InvalidRequestException.class);
    }
}
