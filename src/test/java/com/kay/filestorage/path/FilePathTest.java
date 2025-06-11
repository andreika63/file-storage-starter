package com.kay.filestorage.path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FilePathTest {

    @Test
    public void pathTest() {
        LocalDateTime dateTime = LocalDateTime.of(2020, 1, 1, 0, 0);
        UUID uuid = UUID.randomUUID();

        FileGenerator fileGenerator = LocalDateTimeFileGenerator.of(dateTime)
                .year()
                .month()
                .day()
                .hour()
                .and(UuidFileGenerator.of(uuid));

        File file = fileGenerator.generate();
        Assertions.assertEquals("./%s/%s".formatted("2020/01/01/00", uuid), file.getPath());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM/dd");
        fileGenerator = StringFileGenerator.of(() -> dateTime.format(formatter));
        Assertions.assertEquals("./%s-%s/%s".formatted("2020", "01", "01"), fileGenerator.generate().getPath());
    }
}
