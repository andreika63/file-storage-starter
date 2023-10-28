package com.kay.filestorage.persistence;

import com.kay.filestorage.FileStorageException;
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
        Instant now = Instant.now();
        StorageFile storageFile = new StorageFile();
        storageFile.setPath(dto.getPath());
        storageFile.setName(dto.getName());
        storageFile.setSize(dto.getSize());
        storageFile.setMediaType(dto.getMediaType());
        storageFile.setCreated(now);
        repository.save(storageFile);
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
        return repository.getDeleted(Instant.now().minus(retentionInterval))
                .stream()
                .map(this::toDto)
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
        repository.deleteById(fileId);
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
        return repository.findById(fileId)
                .orElseThrow(() -> new FileStorageException("File id=%s not found".formatted(fileId)));

    }
}
