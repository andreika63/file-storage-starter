package com.kay.filestorage;

import com.kay.filestorage.config.FileStorageProperties;
import com.kay.filestorage.persistence.FileStoragePersistenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.io.File;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.concurrent.locks.ReentrantLock;

public class CleanupService implements ApplicationRunner {

    private final FileStorageProperties properties;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final FileStoragePersistenceManager persistenceManager;
    private final ReentrantLock lock;
    private final Flux<Long> cleanupFlux;
    private Disposable disposable;

    public CleanupService(FileStorageProperties properties, FileStoragePersistenceManager persistenceManager, ReentrantLock lock) {
        this.properties = properties;
        this.persistenceManager = persistenceManager;
        this.lock = lock;
        cleanupFlux = Flux.interval(properties.getCleanupInterval())
                .doOnNext(n -> cleanup(properties.getRetentionInterval()))
                .onErrorContinue((th, o) -> log.error(th.getMessage(), th));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        init();
    }

    protected int cleanup(Duration retentionInterval) {
        log.info("starting cleanup...");
        int count = 0;
        for (StorageFileDto dto : persistenceManager.getDeleted(retentionInterval)) {
            persistenceManager.delete(dto.getId());
            removeFileFromDisk(dto.getPath());
            count++;
        }
        log.info("cleaned up {} files", count);
        return count;
    }

    protected void removeFileFromDisk(String path) {
        File fileToDelete = new File(path);
        if (!fileToDelete.exists()) {
            return;
        }
        lock.lock();
        for (File file = fileToDelete; file != null && !file.getPath().equals(properties.getPath()); file = file.getParentFile()) {
            try {
                Files.delete(file.toPath());
                log.trace("deleted %s".formatted(file.getPath()));
            } catch (DirectoryNotEmptyException e) {
                log.trace("Directory not empty: " + file.getPath());
                break;
            } catch (Throwable e) {
                log.error("error delete file: " + file.getPath(), e);
            }
        }
        lock.unlock();
    }

    protected synchronized void init() {
        if (isSchedulerActive()) {
            disposable.dispose();
            log.info("Cleanup scheduler stopped");
        }
        if (!properties.getRetentionInterval().isZero() && !properties.getCleanupInterval().isZero()) {
            disposable = cleanupFlux.subscribe();
            log.info("Cleanup scheduler started (CleanupInterval = %s)".formatted(properties.getCleanupInterval()));
        }
    }

    protected boolean isSchedulerActive() {
        return disposable != null && !disposable.isDisposed();
    }
}
