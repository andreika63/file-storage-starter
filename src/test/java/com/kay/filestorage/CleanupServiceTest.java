package com.kay.filestorage;

import com.kay.filestorage.config.FileStorageAutoConfig;
import com.kay.filestorage.config.FileStorageProperties;
import com.kay.filestorage.path.FileGenerator;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest(classes = FileStorageAutoConfig.class)
public class CleanupServiceTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

//    @Autowired
//    private CleanupService cleanupService;

    @Autowired
    private FileGenerator fileGenerator;

    @Autowired
    private FileStorageProperties properties;

    @Test
    public void test() {
//        cleanupService.cleanup(null);
    }
    @Test
    public void deleteTest() {
//        cleanupService.deleteFile(-1L);
    }

    @Test
    public void removeFileTest() {
        File file = fileGenerator.generate();

        for (File f = file; f != null && !f.getPath().equals(properties.getPath()) ; f = f.getParentFile()) {
            log.info(f.getPath());
        }
    }
}
