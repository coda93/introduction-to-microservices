package com.example.resource_service.service;

public record ExtractedMetadata(
        String name,
        String artist,
        String album,
        String duration,
        String year
) {
}
