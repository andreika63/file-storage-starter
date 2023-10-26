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
        LocalDateTime now = LocalDateTime.now();
        UUID uuid = UUID.randomUUID();

        FileGenerator fileGenerator = LocalDateTimeFileGenerator.of(now)
                .year()
                .month()
                .day()
                .hour()
                .and(UuidFileGenerator.of(uuid));

        File file = fileGenerator.generate();
        Assertions.assertEquals("./%s/%s/%s/%s/%s".formatted(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), now.getHour(), uuid), file.getPath());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM/dd");
        fileGenerator = StringFileGenerator.of(() -> now.format(formatter));
        Assertions.assertEquals("./%s-%s/%s".formatted(now.getYear(), now.getMonthValue(), now.getDayOfMonth()), fileGenerator.generate().getPath());
    }
}
