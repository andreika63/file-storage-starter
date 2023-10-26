package com.kay.filestorage;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Supplier;

public class StorageFileDto {

    private Long id;

    private String path;

    private String name;

    private Long size;

    private String mediaType;

    private Instant created;

    private Instant deleted;

    private Supplier<InputStream> inputStreamSupplier;

    public static StorageFileDto of(Supplier<InputStream> inputStreamSupplier) {
        Objects.requireNonNull(inputStreamSupplier);
        StorageFileDto storageFileDto = new StorageFileDto();
        storageFileDto.inputStreamSupplier = inputStreamSupplier;
        return storageFileDto;
    }

    public static StorageFileDto of(String path) {
        Objects.requireNonNull(path);
        StorageFileDto storageFileDto = new StorageFileDto();
        storageFileDto.inputStreamSupplier = () -> {
            try {
                return new BufferedInputStream(new FileInputStream(path));
            } catch (FileNotFoundException e) {
                throw new FileStorageException(e.getMessage());
            }
        };
        return storageFileDto;
    }

    private StorageFileDto() {
    }

    public Long getId() {
        return id;
    }

    public StorageFileDto id(Long id) {
        this.id = id;
        return this;
    }

    public String getPath() {
        return path;
    }

    public StorageFileDto path(String path) {
        this.path = path;
        return this;
    }

    public String getName() {
        return name != null ? name : "file.bin";
    }

    public StorageFileDto name(String name) {
        this.name = name;
        return this;
    }

    public Long getSize() {
        return size;
    }

    public StorageFileDto size(Long size) {
        this.size = size;
        return this;
    }

    public String getMediaType() {
        return mediaType;
    }

    public StorageFileDto mediaType(String mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    public Instant getCreated() {
        return created;
    }

    public StorageFileDto created(Instant created) {
        this.created = created;
        return this;
    }

    public Instant getDeleted() {
        return deleted;
    }

    public StorageFileDto deleted(Instant deleted) {
        this.deleted = deleted;
        return this;
    }

    public InputStream getInputStream() {
        return inputStreamSupplier.get();
    }


}
