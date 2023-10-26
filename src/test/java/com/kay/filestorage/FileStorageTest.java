package com.kay.filestorage;

import com.kay.filestorage.config.FileStorageAutoConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;

@SpringBootTest(classes = FileStorageAutoConfig.class)
@Transactional
public class FileStorageTest {

    @Autowired
    private FileStorage fileStorage;

    @Test
    public void test() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);
        StorageFileDto storageFileDto = StorageFileDto.of(() -> inputStream);

        StorageFileDto file = fileStorage.createFile(storageFileDto);
        Assertions.assertTrue(file.getId() != null && file.getPath() != null);
    }

}
