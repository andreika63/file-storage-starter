package com.kay.filestorage;

import com.kay.filestorage.config.FileStorageProperties;
import com.kay.filestorage.path.FileGenerator;
import com.kay.filestorage.persistence.FileStoragePersistenceManager;
import com.kay.filestorage.persistence.TxHelper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;


public class FileStorageImpl implements FileStorage {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final FileGenerator fileGenerator;
    private final CleanupService cleanupService;
    private final FileStorageProperties properties;
    private final ReentrantLock lock;
    private final FileStoragePersistenceManager persistenceManager;


    public FileStorageImpl(FileGenerator fileGenerator
            , CleanupService cleanupService
            , FileStorageProperties properties
            , ReentrantLock lock
            , FileStoragePersistenceManager persistenceManager
    ) {
        this.fileGenerator = fileGenerator;
        this.cleanupService = cleanupService;
        this.properties = properties;
        this.lock = lock;
        this.persistenceManager = persistenceManager;
    }

    @Transactional
    @Override
    public StorageFileDto createFile(StorageFileDto storageFileDto) {
        StorageFileDto result = writeToDisk(storageFileDto);
        TxHelper.afterCompletion(status -> {
            if (TransactionSynchronization.STATUS_COMMITTED != status) {
                cleanupService.removeFileFromDisk(result.getPath());
            } else {
                log.debug("created fileId[%s] path[%s]".formatted(result.getId(), result.getPath()));
            }
        });
        return persistenceManager.persist(result);
    }

    @Override
    public StorageFileDto getFile(Long fileId) {
        return persistenceManager.getFile(fileId);
    }

    @Override
    public void deleteFile(Long fileId) {
        if (properties.getRetentionInterval().isZero()) {
            StorageFileDto file = persistenceManager.getFile(fileId);
            persistenceManager.delete(file.getId());
            cleanupService.removeFileFromDisk(file.getPath());
        } else {
            persistenceManager.markDeleted(fileId);
            log.debug("markDeleted fileId=%s".formatted(fileId));
        }
    }

    @Override
    public int cleanup(Duration retentionInterval) {
        Objects.requireNonNull(retentionInterval);
        return cleanupService.cleanup(retentionInterval);
    }

    @Override
    public Duration getCleanupInterval() {
        return properties.getCleanupInterval();
    }

    @Override
    public void setCleanupInterval(Duration cleanupInterval) {
        properties.setCleanupInterval(cleanupInterval != null ? cleanupInterval : Duration.ZERO);
        cleanupService.init();
    }

    @Override
    public Duration getRetentionInterval() {
        return properties.getRetentionInterval();
    }

    @Override
    public void setRetentionInterval(Duration retentionInterval) {
        properties.setRetentionInterval(retentionInterval != null ? retentionInterval : Duration.ZERO);
    }

    private StorageFileDto writeToDisk(StorageFileDto storageFileDto) {
        File file = fileGenerator.generate();
        lock.lock();
        file.getParentFile().mkdirs();
        try (
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                BufferedOutputStream out = new BufferedOutputStream(fileOutputStream)
        ) {
            lock.unlock();
            long size = IOUtils.copyLarge(storageFileDto.getInputStream(), out);
            return storageFileDto
                    .path(file.getPath())
                    .size(size);
        } catch (IOException e) {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
            throw new FileStorageException(e.getMessage());
        }
    }
}
