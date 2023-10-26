package com.kay.filestorage.path;

import java.io.File;
import java.util.Objects;
import java.util.function.Supplier;

public class StringFileGenerator implements FileGenerator {

    private final Supplier<String> stringSupplier;

    public static StringFileGenerator of(Supplier<String> stringSupplier) {
        return new StringFileGenerator(stringSupplier);
    }

    public static StringFileGenerator of(String string) {
        return new StringFileGenerator(() -> string);
    }

    private StringFileGenerator(Supplier<String> stringSupplier) {
        Objects.requireNonNull(stringSupplier);
        this.stringSupplier = stringSupplier;
    }

    @Override
    public File generate(File parent) {
        File file = parent;
        if (parent == null) {
            file = new File(".");
        }
        file = new File(file, stringSupplier.get());
        return file;
    }
}
