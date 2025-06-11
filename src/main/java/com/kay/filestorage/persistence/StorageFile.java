package com.kay.filestorage.persistence;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "storage_file")
public class StorageFile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String path;

    private String name;

    private Long size;

    private String mediaType;

    private Instant created;

    private Instant deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getDeleted() {
        return deleted;
    }

    public void setDeleted(Instant deleted) {
        this.deleted = deleted;
    }
}
