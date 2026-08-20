package com.example.song_service.dto;

import java.util.Map;

public record ValidationErrorResponse(
        String errorMessage,
        Map<String, String> details,
        String errorCode
) {
}

