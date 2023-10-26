package com.kay.filestorage.path;

import java.io.File;

public interface FileGenerator {

    File generate(File parent);

    default File generate() {
        return generate(new File("."));
    }

    default File generate(String path) {
        return generate(new File(path));
    }

    default FileGenerator and(FileGenerator generator) {
        return parent -> generator.generate(this.generate(parent));
    }
}
