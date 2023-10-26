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
    public List<StorageFileDto> getDeleted(Duration retentionInterval) {
        return repository.getDeleted(Instant.now().minus(retentionInterval))
                .stream()
                .map(storageFile -> StorageFileDto.of(storageFile.getPath())
                        .id(storageFile.getId())
                        .path(storageFile.getPath())
                        .name(storageFile.getName())
                        .size(storageFile.getSize())
                        .mediaType(storageFile.getMediaType())
                        .created(storageFile.getCreated())
                        .deleted(storageFile.getDeleted())
                )
                .toList();
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
