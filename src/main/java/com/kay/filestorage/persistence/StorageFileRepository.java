package com.kay.filestorage.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface StorageFileRepository extends JpaRepository<StorageFile, Long> {

    @Query("select f from StorageFile f where f.deleted < :keepAfter")
    List<StorageFile> getDeleted(@Param("keepAfter") Instant keepAfter);
}
