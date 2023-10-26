package com.kay.filestorage.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface StorageFileRepository extends JpaRepository<StorageFile, Long> {

    @Query("select f.path from StorageFile f where f.deleted < :keepAfter")
    List<String> getDeleted(Instant keepAfter);
}
