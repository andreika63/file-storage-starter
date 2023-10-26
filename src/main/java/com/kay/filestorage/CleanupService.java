package com.kay.filestorage;

import com.kay.filestorage.config.FileStorageProperties;
import com.kay.filestorage.persistence.FileStoragePersistenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    public CleanupService(FileStorageProperties properties, FileStoragePersistenceManager persistenceManager, ReentrantLock lock) {
        this.properties = properties;
        this.persistenceManager = persistenceManager;
        this.lock = lock;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Flux.interval(properties.getCleanupInterval())
                .doOnNext(n -> cleanup(properties.getRetentionInterval()))
                .doOnError(th -> log.error(th.getMessage(), th))
                .onErrorResume(th -> Mono.empty())
                .subscribe();
    }

    public void cleanup(Duration retentionInterval) {
        log.info("starting cleanup...");
        int count = 0;
        for (String path : persistenceManager.getDeleted(retentionInterval)) {
            removeFileFromDisk(path);
            count++;
        }
        log.info("cleaned up {} files", count);
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
}
