package com.kay.filestorage.persistence;

import com.kay.filestorage.StorageFileDto;

import java.time.Duration;
import java.util.List;

public interface FileStoragePersistenceManager {

    StorageFileDto persist(StorageFileDto storageFileDto);

    StorageFileDto getFile(Long fileId);

    List<StorageFileDto> getDeleted(Duration retentionInterval);

    void markDeleted(Long fileId);

    void delete(Long fileId);
}
