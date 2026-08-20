package com.example.resource_service.dto;

public record SongMetadataRequest(
        Integer id,
        String name,
        String artist,
        String album,
        String duration,
        String year
) {
}
