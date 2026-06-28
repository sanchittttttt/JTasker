package com.JTasker.builder;

import com.JTasker.model.Task;
import com.JTasker.strategy.RetryStrategy;
import com.JTasker.tasks.EmailTask;

public class TaskBuilder
{
    private String id;
    private String name;
    private RetryStrategy retryStrategy;

    public TaskBuilder id(String id)
    {
        this.id = id;
        return this;
    }

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
