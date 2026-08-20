package com.example.song_service.service;

import com.example.song_service.dto.SongDto;
import com.example.song_service.exception.InvalidRequestException;
import com.example.song_service.exception.SongAlreadyExistsException;
import com.example.song_service.exception.SongNotFoundException;
import com.example.song_service.mapper.SongMapper;
import com.example.song_service.model.Song;
import com.example.song_service.repository.SongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SongService {

    private static final int MAX_CSV_LENGTH = 200;

    private final SongRepository songRepository;
    private final SongMapper songMapper;

    public SongService(SongRepository songRepository, SongMapper songMapper) {
        this.songRepository = songRepository;
        this.songMapper = songMapper;
    }

    @Transactional
    public Integer create(SongDto dto) {
        if (songRepository.existsById(dto.id())) {
            throw new SongAlreadyExistsException(dto.id());
        }
        Song saved = songRepository.save(songMapper.toEntity(dto));
        return saved.getId();
    }

    @Transactional(readOnly = true)
    public SongDto getById(Integer id) {
        validatePositiveId(id);
        return songRepository.findById(id)
                .map(songMapper::toDto)
                .orElseThrow(() -> new SongNotFoundException(id));
    }

    @Transactional
    public List<Integer> delete(String csvIds) {
        List<Integer> ids = parseCsvIds(csvIds);

        List<Integer> deleted = new ArrayList<>();
        for (Integer id : ids) {
            if (songRepository.existsById(id)) {
                songRepository.deleteById(id);
                deleted.add(id);
            }
        }
        return deleted;
    }

    private void validatePositiveId(Integer id) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException("Invalid value '" + id + "' for ID. Must be a positive integer");
        }
    }

    private List<Integer> parseCsvIds(String csvIds) {
        if (csvIds == null || csvIds.isBlank()) {
            throw new InvalidRequestException("The 'id' parameter is required");
        }
        if (csvIds.length() > MAX_CSV_LENGTH) {
            throw new InvalidRequestException(
                    "CSV string is too long: received " + csvIds.length()
                            + " characters, maximum allowed is " + MAX_CSV_LENGTH);
        }

        List<Integer> ids = new ArrayList<>();
        for (String part : csvIds.split(",")) {
            String trimmed = part.trim();
            int value;
            try {
                value = Integer.parseInt(trimmed);
            } catch (NumberFormatException ex) {
                throw new InvalidRequestException("Invalid ID format: '" + trimmed + "'. Only positive integers are allowed");
            }
            if (value <= 0) {
                throw new InvalidRequestException("Invalid ID '" + trimmed + "'. Only positive integers are allowed");
            }
            ids.add(value);
        }
        return ids;
    }
}
