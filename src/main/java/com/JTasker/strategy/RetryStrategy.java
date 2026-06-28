package com.JTasker.strategy;

public interface RetryStrategy
{
    int maxRetries();
    long delayInMillis(int numberOfRetries);
}
