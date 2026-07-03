package com.JTasker.engine;

import com.JTasker.model.Task;
import com.JTasker.model.TaskStatus;
import com.JTasker.strategy.RetryStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskEngine
{
    private volatile boolean running = false;
    private ExecutorService engine;
    private Map<String, Task> registry = new ConcurrentHashMap<>();

    public void start()
    {
        running = true;
        engine = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }
    public void submit(Task task) throws InterruptedException {
        if (!running)
        {
            throw new IllegalStateException("Engine is not running. Call start() first.");
        }
        registry.putIfAbsent(task.getId(),task);

        Runnable runnableTask = () -> {
            int attempts = 0;
            int maxRetries = task.getRetryStrategy().maxRetries();

            while (attempts <= maxRetries) {
                try {
                    task.setStatus(TaskStatus.RUNNING);
                    task.execute();
                    task.setStatus(TaskStatus.DONE);
                    break; // success — stop retrying
                } catch (Exception ex) {
                    attempts++;
                    if (attempts > maxRetries) {
                        task.setStatus(TaskStatus.FAILED);
                        System.out.println("Task failed after " + attempts + " attempts: " + task.getName());
                    } else {
                        task.setStatus(TaskStatus.RETRYING);
                        long delay = task.getRetryStrategy().delayInMillis(attempts);
                        System.out.println("Retrying task: " + task.getName() + " attempt " + attempts + " delay " + delay + "ms");
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }
        };

        engine.execute(runnableTask);
    }
    public void shutdown()
    {
        if (!running)
        {
            throw new IllegalStateException("Engine is not running. Call start() first.");
        }
        running = false;
        engine.shutdown();
    }
    public Task getTask(String taskId)
    {
        return registry.get(taskId);
    }
}
