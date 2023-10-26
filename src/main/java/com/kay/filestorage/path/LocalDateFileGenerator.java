package com.kay.filestorage.path;

import java.io.File;
import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Supplier;

public class LocalDateFileGenerator implements FileGenerator {

    private final Supplier<LocalDate> localDateSupplier;

    public static LocalDateFileGenerator of(Supplier<LocalDate> localDateSupplier) {
        return new LocalDateFileGenerator(localDateSupplier);
    }

    public static LocalDateFileGenerator of(LocalDate localDate) {
        return new LocalDateFileGenerator(() -> localDate);
    }

    private LocalDateFileGenerator(Supplier<LocalDate> localDateSupplier) {
        Objects.requireNonNull(localDateSupplier);
        this.localDateSupplier = localDateSupplier;
    }

    @Override
    public File generate(File parent) {
        File file = parent;
        if (parent == null) {
            file = new File(".");
        }
        LocalDate localDate = localDateSupplier.get();
        file = new File(file, String.valueOf(localDate.getYear()));
        file = new File(file, String.format("%02d", localDate.getMonthValue()));
        file = new File(file, String.format("%02d", localDate.getDayOfMonth()));
        return file;
    }
}
