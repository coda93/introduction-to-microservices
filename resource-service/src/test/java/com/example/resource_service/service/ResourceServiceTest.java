package com.example.resource_service.service;

import com.example.resource_service.client.SongServiceClient;
import com.example.resource_service.dto.SongMetadataRequest;
import com.example.resource_service.exception.InvalidRequestException;
import com.example.resource_service.exception.ResourceNotFoundException;
import com.example.resource_service.model.ResourceEntity;
import com.example.resource_service.repository.ResourceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private Mp3MetadataExtractor metadataExtractor;

    @Mock
    private SongServiceClient songServiceClient;

    @InjectMocks
    private ResourceService resourceService;

    private ResourceEntity entityWithId(int id) {
        ResourceEntity entity = new ResourceEntity(new byte[]{1, 2, 3});
        try {
            var field = ResourceEntity.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }

    @Test
    void uploadStoresResourceAndSendsMetadata() {
        byte[] data = new byte[]{1, 2, 3};
        when(metadataExtractor.extract(data))
                .thenReturn(new ExtractedMetadata("Song", "Artist", "Album", "02:59", "1977"));
        when(resourceRepository.save(any(ResourceEntity.class))).thenReturn(entityWithId(42));

        Integer id = resourceService.upload(data);

        assertThat(id).isEqualTo(42);
        ArgumentCaptor<SongMetadataRequest> captor = ArgumentCaptor.forClass(SongMetadataRequest.class);
        verify(songServiceClient).saveMetadata(captor.capture());
        SongMetadataRequest sent = captor.getValue();
        assertThat(sent.id()).isEqualTo(42);
        assertThat(sent.name()).isEqualTo("Song");
        assertThat(sent.duration()).isEqualTo("02:59");
        assertThat(sent.year()).isEqualTo("1977");
    }

    @Test
    void uploadRejectsEmptyBody() {
        assertThatThrownBy(() -> resourceService.upload(new byte[]{}))
                .isInstanceOf(InvalidRequestException.class);
        verify(resourceRepository, never()).save(any());
        verify(songServiceClient, never()).saveMetadata(any());
    }

    @Test
    void getDataReturnsBytes() {
        when(resourceRepository.findById(1)).thenReturn(Optional.of(entityWithId(1)));
        assertThat(resourceService.getData(1)).containsExactly(1, 2, 3);
    }

    @Test
    void getDataThrowsWhenMissing() {
        when(resourceRepository.findById(5)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> resourceService.getData(5))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getDataRejectsNonPositiveId() {
        assertThatThrownBy(() -> resourceService.getData(0))
                .isInstanceOf(InvalidRequestException.class);
        assertThatThrownBy(() -> resourceService.getData(-3))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void deleteReturnsOnlyExistingIdsAndCascades() {
        when(resourceRepository.existsById(1)).thenReturn(true);
        when(resourceRepository.existsById(2)).thenReturn(false);

        List<Integer> deleted = resourceService.delete("1,2");

        assertThat(deleted).containsExactly(1);
        verify(resourceRepository).deleteById(1);
        verify(resourceRepository, never()).deleteById(2);
        verify(songServiceClient).deleteMetadata(List.of(1));
    }

    @Test
    void deleteRejectsTooLongCsv() {
        String csv = "1,".repeat(150);
        assertThatThrownBy(() -> resourceService.delete(csv))
                .isInstanceOf(InvalidRequestException.class);
        verify(songServiceClient, never()).deleteMetadata(anyList());
    }

    @Test
    void deleteRejectsInvalidFormat() {
        assertThatThrownBy(() -> resourceService.delete("1,abc"))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void deleteRejectsNonPositiveId() {
        assertThatThrownBy(() -> resourceService.delete("1,0"))
                .isInstanceOf(InvalidRequestException.class);
    }
}
