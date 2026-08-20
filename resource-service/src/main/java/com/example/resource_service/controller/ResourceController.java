package com.example.resource_service.controller;

import com.example.resource_service.dto.DeleteResourceResponse;
import com.example.resource_service.dto.UploadResourceResponse;
import com.example.resource_service.service.ResourceService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping(consumes = "audio/mpeg")
    public ResponseEntity<UploadResourceResponse> upload(@RequestBody byte[] body) {
        Integer id = resourceService.upload(body);
        return ResponseEntity.ok(new UploadResourceResponse(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> get(@PathVariable Integer id) {
        byte[] data = resourceService.getData(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(data);
    }

    @DeleteMapping
    public ResponseEntity<DeleteResourceResponse> delete(@RequestParam("id") String id) {
        return ResponseEntity.ok(new DeleteResourceResponse(resourceService.delete(id)));
    }
}
