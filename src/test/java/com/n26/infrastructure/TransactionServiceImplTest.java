package com.n26.infrastructure;

import com.google.common.cache.CacheBuilder;
import com.n26.domain.Statistics;
import com.n26.domain.Transaction;
import com.n26.domain.TransactionService;
import com.n26.infrastructure.exception.EntityNotFoundException;
import com.n26.infrastructure.exception.NoContentTimestampException;
import com.n26.infrastructure.util.Util;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionServiceImplTest {

    @Autowired
    private TransactionService service;

    private static Double MIN_DOUBLE_RANGE = 0.01D;

    private static Double MAX_DOUBLE_RANGE = 10000.00D;

    private static long CACHE_LIMIT = 100000;

    @Before
    public void setUp() {
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();
        long cacheSize = CACHE_LIMIT;
        if (cacheSize >= 0) {
            cacheBuilder.maximumSize(cacheSize);
        }
        ConcurrentMap<Object, Object> map = cacheBuilder.build().asMap();
        this.service = new TransactionServiceImpl(new ConcurrentMapCache("transactionTest", map, false));
    }

    @Test
    public void testCollectSuccessfulStastitics() throws NoContentTimestampException, InterruptedException, EntityNotFoundException {
        Transaction t1 = new Transaction(0.99D, Util.timestampGenerator());
        this.service.add(t1);

        Thread.sleep(1000);

        Transaction t2 = new Transaction(50.01D, Util.timestampGenerator());
        this.service.add(t2);

        Statistics stats = this.service.get();

        assertThat(stats.getCount()).isEqualTo(2);
        assertThat(stats.getAvg()).isEqualTo(25.50D);
        assertThat(stats.getMax()).isEqualTo(50.01D);
        assertThat(stats.getMin()).isEqualTo(0.99D);
        assertThat(stats.getSum()).isEqualTo(51.00D);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testCollectZeroTransactionsOnStastitics() throws NoContentTimestampException, InterruptedException, EntityNotFoundException {
        Transaction t1 = new Transaction(this.randomGenerator(),
                (Util.timestampGenerator()));
        this.service.add(t1);

        Transaction t2 = new Transaction(this.randomGenerator(),
                Util.timestampGenerator());
        this.service.add(t2);

        Thread.sleep(60001);
        Statistics stats = this.service.get();
    }

    @Test
    public void testAddTransactionOlderThan60Seconds() throws NoContentTimestampException, InterruptedException, EntityNotFoundException {
        try {
            Transaction t1 = new Transaction(this.randomGenerator(),
                    (Util.timestampGenerator() - 61000l));
            this.service.add(t1);

            Transaction t2 = new Transaction(this.randomGenerator(),
                    Util.timestampGenerator() - 61000l);
            this.service.add(t2);

            Transaction t3 = new Transaction(this.randomGenerator(), 1L);
            this.service.add(t3);

            Statistics stats = this.service.get();
            assertThat(false);
        } catch (NoContentTimestampException ex) {
            assertThat(true);
        }

    }

    @Test
    public void testCollectSuccessfulStastiticsWithLoopTransGenerator() throws NoContentTimestampException, InterruptedException, EntityNotFoundException {
        long timestamp = Util.timestampGenerator();
        long count = 0;
        double sum = 0;
        double min = 1000000.00d;
        double max = 0;
        while (true) {
            count++;
            Transaction t1 = new Transaction(this.randomGenerator(), Util.timestampGenerator());
            sum += t1.getAmount();
            min = this.getMin(min, t1.getAmount());
            max = this.getMax(max, t1.getAmount());
            this.service.add(t1);
            Thread.sleep(10);
            if (Util.timestampGenerator() - timestamp >= 10000l) {
                break;
            }
        }
        Statistics stats = this.service.get();
        assertThat(stats.getCount()).isEqualTo(count);
        assertThat(Util.roundAvoid(stats.getAvg(), 2)).isEqualTo(Util.roundAvoid(sum / count, 2));
        assertThat(stats.getMax()).isEqualTo(max);
        assertThat(stats.getMin()).isEqualTo(min);
        assertThat(Util.roundAvoid(stats.getSum(), 2)).isEqualTo(Util.roundAvoid(sum, 2));
    }

    private Double randomGenerator() {
        return Util.roundAvoid(ThreadLocalRandom.current().nextDouble(MIN_DOUBLE_RANGE, MAX_DOUBLE_RANGE), 2);
    }

    private double getMax(double max, double amount) {
        if (max < amount) {
            max = amount;
        }
        return max;
    }

    private double getMin(double min, double amount) {
        if (amount < min) {
            min = amount;
        }
        return min;
    }
}
