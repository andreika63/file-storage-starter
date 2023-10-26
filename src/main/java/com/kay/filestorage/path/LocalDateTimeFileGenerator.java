package com.kay.filestorage.path;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Supplier;

public class LocalDateTimeFileGenerator implements FileGenerator {

    private final Supplier<LocalDateTime> localDateTimeSupplier;

    public static LocalDateTimeFileGenerator of(Supplier<LocalDateTime> localDateTimeSupplier) {
        return new LocalDateTimeFileGenerator(localDateTimeSupplier);
    }

    public static LocalDateTimeFileGenerator of(LocalDateTime localDateTimeSupplier) {
        return new LocalDateTimeFileGenerator(() -> localDateTimeSupplier);
    }

    private LocalDateTimeFileGenerator(Supplier<LocalDateTime> localDateTimeSupplier) {
        Objects.requireNonNull(localDateTimeSupplier);
        this.localDateTimeSupplier = localDateTimeSupplier;
    }

    @Override
    public File generate(File parent) {
        File file = parent;
        if (parent == null) {
            file = new File(".");
        }
        return file;
    }

    public LocalDateTimeFileGenerator year() {
        return new LocalDateTimeFileGenerator(localDateTimeSupplier) {
            final FileGenerator parentGenerator = LocalDateTimeFileGenerator.this;
            @Override
            public File generate(File parent) {
                return new File(parentGenerator.generate(parent), String.valueOf(localDateTimeSupplier.get().getYear()));
            }
        };
    }

    public LocalDateTimeFileGenerator month() {
        return new LocalDateTimeFileGenerator(localDateTimeSupplier) {
            final FileGenerator parentGenerator = LocalDateTimeFileGenerator.this;
            @Override
            public File generate(File parent) {
                return new File(parentGenerator.generate(parent), String.format("%02d", localDateTimeSupplier.get().getMonthValue()));
            }
        };
    }

    public LocalDateTimeFileGenerator day() {
        return new LocalDateTimeFileGenerator(localDateTimeSupplier) {
            final FileGenerator parentGenerator = LocalDateTimeFileGenerator.this;
            @Override
            public File generate(File parent) {
                return new File(parentGenerator.generate(parent), String.format("%02d", localDateTimeSupplier.get().getDayOfMonth()));
            }
        };
    }

    public LocalDateTimeFileGenerator hour() {
        return new LocalDateTimeFileGenerator(localDateTimeSupplier) {
            final FileGenerator parentGenerator = LocalDateTimeFileGenerator.this;
            @Override
            public File generate(File parent) {
                return new File(parentGenerator.generate(parent), String.format("%02d", localDateTimeSupplier.get().getHour()));
            }
        };
    }

    public LocalDateTimeFileGenerator minute() {
        return new LocalDateTimeFileGenerator(localDateTimeSupplier) {
            final FileGenerator parentGenerator = LocalDateTimeFileGenerator.this;
            @Override
            public File generate(File parent) {
                return new File(parentGenerator.generate(parent), String.format("%02d", localDateTimeSupplier.get().getMinute()));
            }
        };
    }
}
