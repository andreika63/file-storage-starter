package com.kay.filestorage.persistence;

import com.kay.filestorage.StorageFileDto;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class PersistenceManagerImpl implements FileStoragePersistenceManager {

    private final StorageFileRepository repository;

    public PersistenceManagerImpl(StorageFileRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @Override
    public StorageFileDto persist(StorageFileDto dto) {
        StorageFile storageFile = new StorageFile();
        storageFile.setPath(dto.getPath());
        storageFile.setName(dto.getName());
        storageFile.setSize(dto.getSize());
        storageFile.setMediaType(dto.getMediaType());
        storageFile.setCreated(Instant.now());
        repository.save(storageFile);
        return dto.id(storageFile.getId());
    }

    @Transactional
    @Override
    public String getPath(Long fileId) {
        return repository.getReferenceById(fileId)
                .getPath();
    }

    @Transactional
    @Override
    public List<String> getDeleted(Duration retentionInterval) {
        return repository.getDeleted(Instant.now().minus(retentionInterval));
    }

    @Transactional
    @Override
    public void markDeleted(Long fileId) {
        StorageFile storageFile = repository.getReferenceById(fileId);
        storageFile.setDeleted(Instant.now());
    }

    @Transactional
    @Override
    public void delete(Long fileId) {
        repository.deleteById(fileId);
    }
}
