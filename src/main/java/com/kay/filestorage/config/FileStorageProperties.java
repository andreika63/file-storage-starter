package com.kay.filestorage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.Duration;


@Validated
@ConfigurationProperties(prefix = "file-storage")
public class FileStorageProperties {
    String path;

    @Valid
    @Min(1)
    @Max(10)
    Integer shardingPower;

    Duration cleanupInterval;

    Duration retentionInterval;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getShardingPower() {
        return shardingPower;
    }

    public void setShardingPower(Integer shardingPower) {
        this.shardingPower = shardingPower;
    }

    public Duration getCleanupInterval() {
        return cleanupInterval;
    }

    public void setCleanupInterval(Duration cleanupInterval) {
        this.cleanupInterval = cleanupInterval;
    }

    public Duration getRetentionInterval() {
        return retentionInterval;
    }

    public void setRetentionInterval(Duration retentionInterval) {
        this.retentionInterval = retentionInterval;
    }
}
