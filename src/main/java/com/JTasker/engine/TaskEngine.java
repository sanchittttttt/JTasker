package com.JTasker.engine;

import com.JTasker.model.Task;
import com.JTasker.model.TaskStatus;

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
    public void submit(Task task)
    {
        if (!running)
        {
            throw new IllegalStateException("Engine is not running. Call start() first.");
        }
        registry.putIfAbsent(task.getId(),task);

        Runnable runnaableTask = () -> {
            try{
                task.setStatus(TaskStatus.RUNNING);
                task.execute();
                task.setStatus(TaskStatus.DONE);
            }
            catch(Exception ex)
            {
                task.setStatus(TaskStatus.FAILED);
                ex.printStackTrace();
            }
        };

        engine.execute(runnaableTask);
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
