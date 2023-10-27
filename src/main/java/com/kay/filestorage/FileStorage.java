package com.kay.filestorage;

import java.time.Duration;

public interface FileStorage {

    StorageFileDto createFile(StorageFileDto storageFileDto);

    StorageFileDto getFile(Long fileId);

    void deleteFile(Long fileId);

    int cleanup(Duration retentionInterval);

    Duration getCleanupInterval();

    void setCleanupInterval(Duration cleanupInterval);

    Duration getRetentionInterval();

    void setRetentionInterval(Duration retentionInterval);
}
