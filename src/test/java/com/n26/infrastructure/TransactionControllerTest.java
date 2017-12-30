package com.n26.infrastructure;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.domain.Transaction;
import com.n26.domain.TransactionService;
import com.n26.infrastructure.util.Util;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.DoubleSummaryStatistics;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TransactionService service;

    DoubleSummaryStatistics stats;

    public static final int LIMIT_TIMESTAMP = 60000;

    @Before
    public void setUp() {
        Long lastIndex = Util.timestampGenerator();
        Long firstIndex = lastIndex - LIMIT_TIMESTAMP;

        Transaction t1 = new Transaction(50.00D, lastIndex - 2000);
        Transaction t2 = new Transaction(20.40D, lastIndex);
        ConcurrentMap<Object, Object> map = new ConcurrentHashMap<>();
        map.putIfAbsent(t1.getTimestamp(), t1.getAmount());
        map.putIfAbsent(t2.getTimestamp(), t2.getAmount());

        Stream streamCache = LongStream.rangeClosed(firstIndex, lastIndex)
                .mapToObj(index -> map.get(index))
                .filter(Objects::nonNull);

        this.stats = streamCache.mapToDouble(amount -> (Double) amount).summaryStatistics();
    }

    @Test
    public void testAddTransaction() throws Exception {
        Transaction t1 = new Transaction(0.99D, Util.timestampGenerator());
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(t1))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        verify(this.service, times(1)).add(t1);
    }

    @Test
    public void testCollectSuccessfulStastitics() throws Exception {
        when(this.service.get()).thenReturn(this.stats);
        mvc.perform(get("/statistics")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ObjectMapper mapper = new ObjectMapper();
                    String json = result.getResponse().getContentAsString();
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    DoubleSummaryStatistics localStats = mapper.readValue(json, DoubleSummaryStatistics.class);
                    assertThat(this.stats.getCount()).isEqualTo(localStats.getCount());
                    assertThat(this.stats.getAverage()).isEqualTo(localStats.getAverage());
                    assertThat(this.stats.getMax()).isEqualTo(localStats.getMax());
                    assertThat(this.stats.getMin()).isEqualTo(localStats.getMin());
                    assertThat(this.stats.getSum()).isEqualTo(localStats.getSum());
                });
    }
}
