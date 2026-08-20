package com.example.song_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SongDto(

        @NotNull(message = "ID is required")
        Integer id,

        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        @NotBlank(message = "Artist is required")
        @Size(max = 100, message = "Artist must not exceed 100 characters")
        String artist,

        @NotBlank(message = "Album is required")
        @Size(max = 100, message = "Album must not exceed 100 characters")
        String album,

        @NotBlank(message = "Duration is required")
        @Pattern(
                regexp = "^(\\d{2}):([0-5]\\d)$",
                message = "Duration must be in mm:ss format with leading zeros"
        )
        String duration,

        @NotBlank(message = "Year is required")
        @Pattern(
                regexp = "^(19\\d{2}|20\\d{2})$",
                message = "Year must be between 1900 and 2099"
        )
        String year
) {
}
