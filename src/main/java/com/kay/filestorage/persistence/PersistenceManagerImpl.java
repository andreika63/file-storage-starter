package com.kay.filestorage.persistence;

import com.kay.filestorage.FileStorageException;
import com.kay.filestorage.StorageFileDto;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class PersistenceManagerImpl implements FileStoragePersistenceManager {

    private final EntityManager entityManager;

    public PersistenceManagerImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    @Override
    public StorageFileDto persist(StorageFileDto dto) {
        Instant now = Instant.now();
        StorageFile storageFile = new StorageFile();
        storageFile.setPath(dto.getPath());
        storageFile.setName(dto.getName());
        storageFile.setSize(dto.getSize());
        storageFile.setMediaType(dto.getMediaType());
        storageFile.setCreated(now);
        entityManager.persist(storageFile);
        return dto.id(storageFile.getId())
                .created(now);
    }

    @Transactional
    @Override
    public StorageFileDto getFile(Long fileId) {
       return toDto(getById(fileId));

    }

    @Transactional
    @Override
    public List<StorageFileDto> getDeleted(Duration retentionInterval) {
        return entityManager.createQuery(
                        "SELECT f FROM StorageFile f WHERE f.deleted < :keepAfter", StorageFile.class)
                .setParameter("keepAfter", Instant.now().minus(retentionInterval))
                .getResultStream().map(this::toDto)
                .toList();
    }

    @Transactional
    @Override
    public void markDeleted(Long fileId) {
        StorageFile storageFile = getById(fileId);
        storageFile.setDeleted(Instant.now());
    }

    @Transactional
    @Override
    public void delete(Long fileId) {
        entityManager.remove(entityManager.getReference(StorageFile.class, fileId));
    }

    @Transactional
    @Override
    public StorageFileDto restore(Long fileId) {
        StorageFile storageFile = getById(fileId);
        storageFile.setDeleted(null);
        return toDto(storageFile);
    }

    private StorageFileDto toDto(StorageFile storageFile) {
        return  StorageFileDto.of(storageFile.getPath())
                .id(storageFile.getId())
                .path(storageFile.getPath())
                .name(storageFile.getName())
                .size(storageFile.getSize())
                .mediaType(storageFile.getMediaType())
                .created(storageFile.getCreated())
                .deleted(storageFile.getDeleted());
    }

    private StorageFile getById(Long fileId) {
        return Optional.ofNullable(entityManager.find(StorageFile.class, fileId))
                .orElseThrow(() -> new FileStorageException("File id=%s not found".formatted(fileId)));
    }
}
