package com.n26.domain;

import lombok.*;

/**
 * Statistics.java
 * Pojo responsible to collect the statistics and return json statistics
 *
 * @author roanbrasil
 * @version 1.0
 * @since 12/30/2017
 */
@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class Statistics {
    private double sum;
    private double avg;
    private double max;
    private double min;
    private long count;
}
