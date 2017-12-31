package com.n26.infrastructure.exception;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * EntityNotFoundException.java
 * No Content Timestamp Exception class responsible to launch when the transaction is older or invalid timestamp.
 *
 * @author roanbrasil
 * @version 1.0
 * @since 12-30-2017
 */
public class EntityNotFoundException extends Exception {

    /**
     * Constructor
     *
     * @param clazz
     * @param searchParamsMap
     */
    public EntityNotFoundException(Class clazz, String... searchParamsMap) {
        super(EntityNotFoundException.generateMessage(clazz.getSimpleName(), toMap(String.class, String.class, searchParamsMap)));
    }

    /**
     * Message generator
     *
     * @param entity
     * @param searchParams
     * @return
     */
    private static String generateMessage(String entity, Map<String, String> searchParams) {
        return StringUtils.capitalize(entity) +
                " was not found any statistics in the last 60 seconds " +
                searchParams;
    }

    /**
     * Map all entries that are wrong
     *
     * @param keyType
     * @param valueType
     * @param entries
     * @param <K>
     * @param <V>
     * @return <k, V> Map<k, V>
     */
    private static <K, V> Map<K, V> toMap(
            Class<K> keyType, Class<V> valueType, Object... entries) {
        if (entries.length % 2 == 1)
            throw new IllegalArgumentException("Invalid entries");
        return IntStream.range(0, entries.length / 2).map(i -> i * 2)
                .collect(HashMap::new,
                        (m, i) -> m.put(keyType.cast(entries[i]), valueType.cast(entries[i + 1])),
                        Map::putAll);
    }

}