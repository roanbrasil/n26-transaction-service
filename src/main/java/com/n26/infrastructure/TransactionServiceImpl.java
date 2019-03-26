package com.n26.infrastructure;

import com.google.common.cache.CacheBuilder;
import com.n26.domain.Statistics;
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

/**
 * TransactionServiceImpl.java
 * Provide service to Transaction Requisition.
 *
 * @author roanbrasil
 * @version 1.0
 * @since 12-30-2017
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    public static final int LIMIT_TIMESTAMP = 60000;


    private final ConcurrentMapCache cache;

    public TransactionServiceImpl(ConcurrentMapCache cache) {
        this.cache = cache;
    }

    /**
     * {@inheritDoc}
     */
    @Autowired
    public TransactionServiceImpl() {
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();
        ConcurrentMap<Object, Object> map = cacheBuilder.build().asMap();
        this.cache = new ConcurrentMapCache("transaction", map, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(Transaction transaction) throws NoContentTimestampException {
        //if timestamp is older than 60 seconds OR if in the correct UTC timezone (diference btw transaction timestamp and
        // requisition timestamp cannot be negative)
        if (!validTimestampAge(transaction.getTimestamp())
                || !validTimeStampCorrectZone(transaction.getTimestamp())) {
            throw new NoContentTimestampException(Transaction.class, "timestamp", String.valueOf(transaction.getTimestamp()));
        }
        this.cache.putIfAbsent(transaction.getTimestamp(), transaction.getAmount());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Statistics get() throws EntityNotFoundException {
        Long lastIndex = Util.timestampGenerator();
        Long firstIndex = lastIndex - LIMIT_TIMESTAMP;

        ConcurrentMap<Object, Object> map = this.cache.getNativeCache();

        //get only transaction in a range of 60 seconds age available
        Stream streamCache = LongStream.rangeClosed(firstIndex, lastIndex)
                .mapToObj(map::get)
                .filter(Objects::nonNull);

        //setup statistics information
        DoubleSummaryStatistics doubleSummaryStatistics = streamCache.mapToDouble(amount -> (Double) amount).summaryStatistics();

        Statistics stats = new Statistics(doubleSummaryStatistics.getSum(),
                doubleSummaryStatistics.getAverage(), doubleSummaryStatistics.getMax(),
                doubleSummaryStatistics.getMin(), doubleSummaryStatistics.getCount()
        );
        //if there is just transaction older than 60 seconds, this exception will be launched
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
