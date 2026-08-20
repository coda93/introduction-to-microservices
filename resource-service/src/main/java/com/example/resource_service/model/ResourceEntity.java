package com.example.resource_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "resources")
public class ResourceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private byte[] data;

    protected ResourceEntity() {
    }

    public ResourceEntity(byte[] data) {
        this.data = data;
    }

    public Integer getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }
}
