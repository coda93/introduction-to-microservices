package com.example.resource_service.repository;

import com.example.resource_service.model.ResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<ResourceEntity, Integer> {
}
