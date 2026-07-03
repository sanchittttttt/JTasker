package com.JTasker.builder;

import com.JTasker.model.Task;
import com.JTasker.strategy.RetryStrategy;
import com.JTasker.tasks.EmailTask;

import java.util.UUID;

public class TaskBuilder
{
    private String id = UUID.randomUUID().toString();;
    private String name;
    private RetryStrategy retryStrategy;

    public  TaskBuilder name(String name)
    {
        this.name = name;
        return this;
    }

    public TaskBuilder retryStrategy(RetryStrategy retryStrategy)
    {
        this.retryStrategy = retryStrategy;
        return this;
    }

    public Task build()
    {
        return new EmailTask(id, name, retryStrategy);
    }
}
