package com.example.resource_service.service;

import com.example.resource_service.exception.InvalidMp3Exception;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Component
public class Mp3MetadataExtractor {

    private static final String MP3_MEDIA_TYPE = "audio/mpeg";

    /**
     * Parses the given bytes as an MP3 file and extracts its tags.
     * The only transformation applied is converting the duration from
     * seconds into {@code mm:ss} format; all other tags are returned as-is.
     *
     * @throws InvalidMp3Exception if the bytes are not a valid MP3 file
     */
    public ExtractedMetadata extract(byte[] data) {
        Metadata metadata = new Metadata();
        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(-1);

        try (InputStream stream = new ByteArrayInputStream(data)) {
            parser.parse(stream, handler, metadata, new ParseContext());
        } catch (Exception ex) {
            throw new InvalidMp3Exception("The request body is not a valid MP3 file");
        }

        String contentType = metadata.get("Content-Type");
        if (contentType == null || !contentType.startsWith(MP3_MEDIA_TYPE)) {
            throw new InvalidMp3Exception("The request body is not a valid MP3 file");
        }

        return new ExtractedMetadata(
                metadata.get("dc:title"),
                metadata.get("xmpDM:artist"),
                metadata.get("xmpDM:album"),
                toMmSs(metadata.get("xmpDM:duration")),
                metadata.get("xmpDM:releaseDate")
        );
    }

    private String toMmSs(String durationInSeconds) {
        if (durationInSeconds == null || durationInSeconds.isBlank()) {
            return null;
        }
        double totalSeconds;
        try {
            totalSeconds = Double.parseDouble(durationInSeconds);
        } catch (NumberFormatException ex) {
            return null;
        }
        long rounded = Math.round(totalSeconds);
        long minutes = rounded / 60;
        long seconds = rounded % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
