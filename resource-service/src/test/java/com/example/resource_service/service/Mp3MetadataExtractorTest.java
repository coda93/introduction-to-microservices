package com.example.resource_service.service;

import com.example.resource_service.exception.InvalidMp3Exception;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Mp3MetadataExtractorTest {

    private final Mp3MetadataExtractor extractor = new Mp3MetadataExtractor();

    @Test
    void rejectsNonMp3Bytes() {
        byte[] notAnMp3 = "this is plain text, definitely not audio".getBytes(StandardCharsets.UTF_8);
        assertThatThrownBy(() -> extractor.extract(notAnMp3))
                .isInstanceOf(InvalidMp3Exception.class);
    }

    @Test
    void rejectsEmptyBytes() {
        assertThatThrownBy(() -> extractor.extract(new byte[]{}))
                .isInstanceOf(InvalidMp3Exception.class);
    }
}
