package com.JTasker.model;

import com.JTasker.strategy.RetryStrategy;

import java.util.Objects;

public abstract class Task {
    private final String id;
    private String name;
    private TaskStatus status;
    private RetryStrategy retryStrategy;

    public Task(String id, String name, RetryStrategy retryStrategy) {
        this.id = id;
        this.name = name;
        this.retryStrategy = retryStrategy;
        this.status = TaskStatus.PENDING;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public RetryStrategy getRetryStrategy() {
        return retryStrategy;
    }

    public void setRetryStrategy(RetryStrategy retryStrategy) {
        this.retryStrategy = retryStrategy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", retryStrategy=" + retryStrategy +
                '}';
    }

    public abstract void execute();
}