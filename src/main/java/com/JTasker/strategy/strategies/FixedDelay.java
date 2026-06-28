package com.JTasker.strategy.strategies;

import com.JTasker.strategy.RetryStrategy;

public class FixedDelay implements RetryStrategy {
    private int maxRetries;
    private long delayInMillis;

    public FixedDelay(int maxRetries, long delayInMillis)
    {
        this.maxRetries = maxRetries;
        this.delayInMillis = delayInMillis;
    }

    @Override
    public String toString() {
        return "FixedDelay{" +
                "maxRetries=" + maxRetries +
                ", delayInMillis=" + delayInMillis +
                '}';
    }

    @Override
    public int maxRetries() {
        return maxRetries;
    }

    @Override
    public long delayInMillis(int numberOfRetries) {
        return delayInMillis;
    }
}
