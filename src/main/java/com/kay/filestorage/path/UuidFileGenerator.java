package com.kay.filestorage.path;

import java.io.File;
import java.util.UUID;
import java.util.function.Supplier;

public class UuidFileGenerator implements FileGenerator {

    private final Supplier<UUID> uuidSupplier;

    public static FileGenerator of(Supplier<UUID> uuidSupplier) {
        return new UuidFileGenerator(uuidSupplier);
    }

    public static FileGenerator of(UUID uuid) {
        return new UuidFileGenerator(() -> uuid);
    }

    private UuidFileGenerator(Supplier<UUID> uuidSupplier) {
        this.uuidSupplier = uuidSupplier;
    }

    @Override
    public File generate(File parent) {
        return new File(parent, uuidSupplier.get().toString());
    }
}
