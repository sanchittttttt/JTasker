package com.JTasker.engine;

import com.JTasker.model.Task;
import com.JTasker.model.TaskStatus;
import com.JTasker.strategy.RetryStrategy;
import com.JTasker.wal.WriteAheadLog;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskEngine
{
    private volatile boolean running = false;
    private ExecutorService engine;
    private Map<String, Task> registry = new ConcurrentHashMap<>();
    private final WriteAheadLog wal = new WriteAheadLog();

    public void start()
    {
        recover();
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
                    wal.log(task.getId(), task.getName(), task.getStatus());
                    task.execute();
                    task.setStatus(TaskStatus.DONE);
                    wal.log(task.getId(), task.getName(), task.getStatus());
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

    public void recover() {
        List<String> entries = wal.recover();
        if (entries.isEmpty()) {
            System.out.println("No WAL found — fresh start");
            return;
        }
        System.out.println("Recovering from WAL — " + entries.size() + " entries found:");
        entries.forEach(System.out::println);
    }
}
