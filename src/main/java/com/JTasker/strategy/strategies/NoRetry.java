package com.JTasker.strategy.strategies;

import com.JTasker.strategy.RetryStrategy;

public class NoRetry implements RetryStrategy
{
    @Override
    public int maxRetries() {
        return 0;
    }

    @Override
    public String toString() {
        return "NoRetry{}";
    }

    @Override
    public long delayInMillis(int numberOfRetries) {
        return 0;
    }
}
