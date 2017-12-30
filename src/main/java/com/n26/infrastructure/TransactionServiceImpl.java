package com.n26.infrastructure;

import com.google.common.cache.CacheBuilder;
import com.n26.domain.Transaction;
import com.n26.domain.TransactionService;
import com.n26.infrastructure.exception.EntityNotFoundException;
import com.n26.infrastructure.exception.NoContentTimestampException;
import com.n26.infrastructure.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.stereotype.Service;

import java.util.DoubleSummaryStatistics;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.LongStream;
import java.util.stream.Stream;

@Service
public class TransactionServiceImpl implements TransactionService {

    public static final int LIMIT_TIMESTAMP = 60000;

    private static long CACHE_LIMIT = 10000;

    private final ConcurrentMapCache cache;

    public TransactionServiceImpl(ConcurrentMapCache cache) {
            this.cache = cache;
    }

    @Autowired
    public TransactionServiceImpl(){
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();
        long cacheSize = CACHE_LIMIT;
        if (cacheSize >= 0) {
            cacheBuilder.maximumSize(cacheSize);
        }
        ConcurrentMap<Object, Object> map = cacheBuilder.build().asMap();
        this.cache = new ConcurrentMapCache("transaction", map, false);
    }
    @Override
    public void add(Transaction transaction) throws NoContentTimestampException {
        if (!validTimestampAge(transaction.getTimestamp())
                || !validTimeStampCorrectZone(transaction.getTimestamp())) {
            throw new NoContentTimestampException(Transaction.class, "timestamp", String.valueOf(transaction.getTimestamp()));
        }
        this.cache.putIfAbsent(transaction.getTimestamp(), transaction.getAmount());
    }

    @Override
    public DoubleSummaryStatistics get() throws EntityNotFoundException {
        Long lastIndex = Util.timestampGenerator();
        Long firstIndex = lastIndex - LIMIT_TIMESTAMP;

        ConcurrentMap<Object, Object> map = this.cache.getNativeCache();

        Stream streamCache = LongStream.rangeClosed(firstIndex, lastIndex)
                .mapToObj(index -> map.get(index))
                .filter(Objects::nonNull);

        DoubleSummaryStatistics stats = streamCache.mapToDouble(amount -> (Double) amount).summaryStatistics();

        if (stats.getCount() == 0) {
            throw new EntityNotFoundException(Object.class,
                    "firstIndex", String.valueOf(firstIndex), "lastIndex", String.valueOf(lastIndex));
        }
        return stats;
    }

    private boolean validTimestampAge(long timestamp) {
        long methodTimestamp = Util.timestampGenerator();
        long allowedTimestampAge = methodTimestamp - (LIMIT_TIMESTAMP);
        //if timestamp of transaction is older than allowedTimestampAge in 60 seconds (60000) return false otherwhise true
        return (allowedTimestampAge - timestamp) <= 0 ? true : false;
    }

    private boolean validTimeStampCorrectZone(long timestamp) {
        long methodTimestamp = Util.timestampGenerator();
        return (methodTimestamp - timestamp) >= 0 ? true : false;
    }
}