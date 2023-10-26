package com.kay.filestorage;

import java.io.InputStream;
import java.util.Date;
import java.util.Objects;
import java.util.function.Supplier;

public class StorageFileDto {

    private Long id;

    private String path;

    private String name;

    private Long size;

    private String mediaType;

    private Date created;

    private Date deleted;

    private Supplier<InputStream> inputStreamSupplier;

    public static StorageFileDto of(Supplier<InputStream> inputStreamSupplier) {
        Objects.requireNonNull(inputStreamSupplier);
        StorageFileDto storageFileDto = new StorageFileDto();
        storageFileDto.inputStreamSupplier = inputStreamSupplier;
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

    public Date getCreated() {
        return created;
    }

    public StorageFileDto created(Date created) {
        this.created = created;
        return this;
    }

    public Date getDeleted() {
        return deleted;
    }

    public StorageFileDto deleted(Date deleted) {
        this.deleted = deleted;
        return this;
    }

    public InputStream getInputStream() {
        return inputStreamSupplier.get();
    }


}
