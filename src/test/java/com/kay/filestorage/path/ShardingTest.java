package com.kay.filestorage.path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShardingTest {

    @Test
    public void shardingTest() {
        final int powerOf2 = 2;
        final int group = 1000;
        final int expected = group / (1 << powerOf2);
        final int maxDiff = group / 20;
        HashCodeSharding sharding = HashCodeSharding.of(powerOf2);
        ArrayList<Integer> val = new ArrayList<>();
        Stream.generate(UUID::randomUUID)
                .map(sharding::shardToInt)
                .limit(group)
                .forEach(val::add);
        Map<Integer, Long> resultMap = val.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        System.out.printf("powerOf2 = %s max diff = %s%n", powerOf2, maxDiff);
        resultMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach((e -> System.out.printf("%s -> expected: %s actual: %s diff: %s%n", e.getKey(), expected, e.getValue(), Math.abs(e.getValue() - expected))));
        Assertions.assertTrue(resultMap.values().stream().noneMatch(v -> Math.abs(v - expected) > maxDiff));
    }
}
