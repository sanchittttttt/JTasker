package com.JTasker.strategy.strategies;

import com.JTasker.strategy.RetryStrategy;

public class ExponentialBackoff implements RetryStrategy
{
    private int maxRetries;
    private long baseDelayInMillis;
    ExponentialBackoff(int maxRetries, long delayInMillis)
    {
        this.maxRetries = maxRetries;
        baseDelayInMillis = delayInMillis;
    }

    @Override
    public String toString() {
        return "ExponentialBackoff{" +
                "maxRetries=" + maxRetries +
                ", baseDelayInMillis=" + baseDelayInMillis +
                '}';
    }

    @Override
    public int maxRetries() {
        return maxRetries;
    }

    @Override
    public long delayInMillis(int numberOfRetries) {
        return (long)(baseDelayInMillis * Math.pow(2, numberOfRetries));
    }
}
