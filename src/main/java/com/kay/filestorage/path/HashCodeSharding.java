package com.kay.filestorage.path;

/**
 * Sharding Object by hashCode
 * powerOf2 - has to be greater than 0
 * powerOf2 = 1, result : 0,1
 * powerOf2 = 2, result: 0,1,2,3
 * powerOf2 = 3, result: 0,1,2,3,4,5,6,7
 */

public class HashCodeSharding implements Sharding {
    private final int lowBits;

    public static HashCodeSharding of(int powerOf2) {
        if (powerOf2 < 1) {
            throw new IllegalArgumentException("powerOf2 has to be greater than 0");
        }
        return new HashCodeSharding(powerOf2);
    }

    private HashCodeSharding(int powerOf2) {
        lowBits = (1 << powerOf2) - 1;
    }

    @Override
    public int shardToInt(Object object) {
        return object.hashCode() & lowBits;
    }
}
