package com.example.resource_service.service;

import com.example.resource_service.client.SongServiceClient;
import com.example.resource_service.dto.SongMetadataRequest;
import com.example.resource_service.exception.InvalidRequestException;
import com.example.resource_service.exception.ResourceNotFoundException;
import com.example.resource_service.model.ResourceEntity;
import com.example.resource_service.repository.ResourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResourceService {

    private static final int MAX_CSV_LENGTH = 200;

    private final ResourceRepository resourceRepository;
    private final Mp3MetadataExtractor metadataExtractor;
    private final SongServiceClient songServiceClient;

    public ResourceService(ResourceRepository resourceRepository,
                           Mp3MetadataExtractor metadataExtractor,
                           SongServiceClient songServiceClient) {
        this.resourceRepository = resourceRepository;
        this.metadataExtractor = metadataExtractor;
        this.songServiceClient = songServiceClient;
    }

    @Transactional
    public Integer upload(byte[] data) {
        if (data == null || data.length == 0) {
            throw new InvalidRequestException("The request body is not a valid MP3 file");
        }

        ExtractedMetadata metadata = metadataExtractor.extract(data);

        ResourceEntity saved = resourceRepository.save(new ResourceEntity(data));

        songServiceClient.saveMetadata(new SongMetadataRequest(
                saved.getId(),
                metadata.name(),
                metadata.artist(),
                metadata.album(),
                metadata.duration(),
                metadata.year()
        ));

        return saved.getId();
    }

    @Transactional(readOnly = true)
    public byte[] getData(Integer id) {
        validatePositiveId(id);
        return resourceRepository.findById(id)
                .map(ResourceEntity::getData)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Transactional
    public List<Integer> delete(String csvIds) {
        List<Integer> ids = parseCsvIds(csvIds);

        List<Integer> deleted = new ArrayList<>();
        for (Integer id : ids) {
            if (resourceRepository.existsById(id)) {
                resourceRepository.deleteById(id);
                deleted.add(id);
            }
        }

        songServiceClient.deleteMetadata(deleted);
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
