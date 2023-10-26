package com.kay.filestorage;

import com.kay.filestorage.config.FileStorageProperties;
import com.kay.filestorage.path.FileGenerator;
import com.kay.filestorage.persistence.PersistenceManager;
import com.kay.filestorage.persistence.TxHelper;
import org.apache.commons.io.IOUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;

import java.io.*;
import java.util.concurrent.locks.ReentrantLock;


public class FileStorageImpl implements FileStorage {

    private final FileGenerator fileGenerator;
    private final CleanupService cleanupService;
    private final FileStorageProperties properties;
    private final ReentrantLock lock;
    private final PersistenceManager persistenceManager;


    public FileStorageImpl(FileGenerator fileGenerator
            , CleanupService cleanupService
            , FileStorageProperties properties
            , ReentrantLock lock
            , PersistenceManager persistenceManager
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
            }
        });
        return persistenceManager.persist(result);
    }

    @Override
    public void deleteFile(Long fileId) {
        persistenceManager.markDeleted(fileId);
    }

    @Override
    public void cleanup() {
        cleanupService.cleanup(properties.getRetentionInterval());
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
            return StorageFileDto.of(() -> {
                        try {
                            return new BufferedInputStream(new FileInputStream(file));
                        } catch (FileNotFoundException e) {
                            throw new FileStorageException(e.getMessage());
                        }
                    })
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
