package com.kay.filestorage;

import com.kay.filestorage.config.FileStorageAutoConfig;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.Duration;

@SpringBootTest(classes = FileStorageAutoConfig.class)
@Transactional
public class FileStorageTest {

    private final String content = "0123456789ABCDEF";
    private final String fileName = "test_file.txt";
    private final String fileMediaType = "media/test";

    @Autowired
    private FileStorage fileStorage;

    @Autowired
    private CleanupService cleanupService;

    @Test
    public void createAndGet() {
        StorageFileDto dto = createFile();
        Assertions.assertTrue(dto.getId() != null && dto.getPath() != null);
        Assertions.assertEquals(content, getContent(dto));
        Assertions.assertEquals(content.getBytes().length, dto.getSize());
        Assertions.assertEquals(fileName, dto.getName());
        Assertions.assertEquals(fileMediaType, dto.getMediaType());

        StorageFileDto fromStorage = fileStorage.getFile(dto.getId());

        Assertions.assertEquals(dto.getId(), fromStorage.getId());
        Assertions.assertEquals(getContent(dto), getContent(fromStorage));
        Assertions.assertEquals(dto.getPath(), fromStorage.getPath());
        Assertions.assertEquals(dto.getName(), fromStorage.getName());
        Assertions.assertEquals(dto.getSize(), fromStorage.getSize());
        Assertions.assertEquals(dto.getMediaType(), fromStorage.getMediaType());
        Assertions.assertEquals(dto.getCreated(), fromStorage.getCreated());
        Assertions.assertEquals(dto.getDeleted(), fromStorage.getDeleted());
    }

    @Test
    public void deleteAndRestore() {
        fileStorage.setRetentionInterval(Duration.ZERO);
        StorageFileDto dto = createFile();
        fileStorage.deleteFile(dto.getId());
        Assertions.assertFalse(new File(dto.getPath()).exists());
        Assertions.assertThrows(FileStorageException.class, () -> fileStorage.deleteFile(dto.getId()));

        fileStorage.setRetentionInterval(Duration.ofDays(15));
        StorageFileDto dto2 = createFile();
        fileStorage.deleteFile(dto2.getId());
        Assertions.assertTrue(new File(dto2.getPath()).exists());
        Assertions.assertThrows(FileStorageException.class, () -> fileStorage.getFile(dto2.getId()));

        StorageFileDto restored = fileStorage.restoreFile(dto2.getId());
        Assertions.assertNull(restored.getDeleted());
    }

    @Test
    public void params() {
        fileStorage.setRetentionInterval(null);
        Assertions.assertEquals(Duration.ZERO, fileStorage.getRetentionInterval());

        Duration fifteenMinutes = Duration.ofMinutes(15);
        fileStorage.setRetentionInterval(fifteenMinutes);
        Assertions.assertEquals(fifteenMinutes, fileStorage.getRetentionInterval());

        Assertions.assertTrue(cleanupService.isSchedulerActive());

        fileStorage.setCleanupInterval(null);
        Assertions.assertEquals(Duration.ZERO, fileStorage.getCleanupInterval());
        Assertions.assertFalse(cleanupService.isSchedulerActive());

        fileStorage.setCleanupInterval(Duration.ZERO);
        Assertions.assertEquals(Duration.ZERO, fileStorage.getCleanupInterval());
        Assertions.assertFalse(cleanupService.isSchedulerActive());

        fileStorage.setCleanupInterval(fifteenMinutes);
        Assertions.assertEquals(fifteenMinutes, fileStorage.getCleanupInterval());
        Assertions.assertTrue(cleanupService.isSchedulerActive());

    }

    @Test
    public void cleanup() {
        Assertions.assertThrows(NullPointerException.class, () -> fileStorage.cleanup(null));
        StorageFileDto dto = createFile();
        fileStorage.deleteFile(dto.getId());
        Assertions.assertEquals(1, fileStorage.cleanup(Duration.ZERO));
        Assertions.assertFalse(new File(dto.getPath()).exists());

    }

    private StorageFileDto createFile() {
        StorageFileDto dto = StorageFileDto.of(() -> new ByteArrayInputStream(content.getBytes()))
                .name(fileName)
                .mediaType(fileMediaType);
        return fileStorage.createFile(dto);
    }

    private String getContent(StorageFileDto dto) {
        try {
            return new String(IOUtils.toByteArray(dto.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
