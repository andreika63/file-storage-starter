package com.kay.filestorage.config;

import com.kay.filestorage.CleanupService;
import com.kay.filestorage.FileStorage;
import com.kay.filestorage.FileStorageImpl;
import com.kay.filestorage.path.*;
import com.kay.filestorage.persistence.FileStoragePersistenceManager;
import com.kay.filestorage.persistence.PersistenceManagerImpl;
import com.kay.filestorage.persistence.TxHelper;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.io.File;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

@Configuration
@PropertySource(value = "classpath:/file-storage.properties")
@EnableConfigurationProperties(FileStorageProperties.class)
@Import(EmbeddedJpaFallbackConfig.class)
public class FileStorageAutoConfig {

    public static final String PERSISTENCE_MANAGER = "fileStoragePersistenceManager";

    @ConditionalOnMissingBean
    @Bean
    FileGenerator fileGenerator(Sharding sharding, FileStorageProperties properties) {
        return parent -> {
            LocalDateTime now = LocalDateTime.now();
            UUID uuid = UUID.randomUUID();
            String shardingStr = String.valueOf(sharding.shardToInt(uuid));
            return LocalDateTimeFileGenerator.of(now)
                    .year()
                    .month()
                    .day()
                    .and(StringFileGenerator.of(shardingStr))
                    .and(UuidFileGenerator.of(uuid))
                    .generate(new File(properties.getPath()));
        };
    }

    @ConditionalOnMissingBean
    @Bean
    Sharding objectSharding(FileStorageProperties properties) {
        return HashCodeSharding.of(properties.shardingPower);
    }

    @Bean
    FileStorage fileStorage(FileGenerator fileGenerator
            , CleanupService cleanupService
            , FileStorageProperties properties
            , ReentrantLock lock
            , FileStoragePersistenceManager persistenceManager
    ) {
        return new FileStorageImpl(fileGenerator, cleanupService, properties, lock, persistenceManager);
    }

    @Bean
    CleanupService cleanupService(FileStorageProperties properties, FileStoragePersistenceManager persistenceManager, ReentrantLock lock) {
        return new CleanupService(properties, persistenceManager, lock);
    }

    @Bean
    public TxHelper fileStorageTxHelper(ApplicationContext ctx) {
        return TxHelper.getInstance(ctx, "fileStorageTxHelper");
    }

    @Bean
    public ReentrantLock lock() {
        return new ReentrantLock(true);
    }

    @Bean
    @ConditionalOnMissingBean(FileStoragePersistenceManager.class)
    public FileStoragePersistenceManager defaultFileStoragePersistenceManager(EntityManager entityManager) {
        return new PersistenceManagerImpl(entityManager);
    }

}
