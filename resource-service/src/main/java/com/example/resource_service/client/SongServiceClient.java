package com.example.resource_service.client;

import com.example.resource_service.dto.SongMetadataRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SongServiceClient {

    private final RestClient restClient;

    public SongServiceClient(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder loadBalancedRestClientBuilder) {
        this.restClient = loadBalancedRestClientBuilder.baseUrl("lb://song-service").build();
    }

    @Retryable(retryFor = Exception.class, maxAttempts = 5, backoff = @Backoff(delay = 2000))
    public void saveMetadata(SongMetadataRequest request) {
        restClient.post()
                .uri("/songs")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public void deleteMetadata(List<Integer> ids) {
        if (ids.isEmpty()) {
            return;
        }
        String csv = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
        try {
            restClient.delete()
                    .uri(uriBuilder -> uriBuilder.path("/songs").queryParam("id", csv).build())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            // Best-effort cascade: resources are already removed; do not fail deletion
            // if the Song Service is unavailable.
            log.warn("Failed to cascade delete song metadata for ids {}: {}", csv, ex.getMessage());
        }
    }
}
