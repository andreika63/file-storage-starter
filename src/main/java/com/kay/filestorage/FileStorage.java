package com.kay.filestorage;

public interface FileStorage {

    StorageFileDto createFile(StorageFileDto storageFileDto);
    void deleteFile(Long fileId);
    void cleanup();
}
