package com.JTasker;

import com.JTasker.builder.TaskBuilder;
import com.JTasker.engine.TaskEngine;
import com.JTasker.model.Task;
import com.JTasker.strategy.strategies.FixedDelay;
import com.JTasker.strategy.strategies.NoRetry;
import com.JTasker.wal.WriteAheadLog;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        TaskEngine engine = new TaskEngine();
        engine.start();

        Task task1 = new TaskBuilder()
                .name("send-welcome-email")
                .retryStrategy(new NoRetry())
                .build();

        Task task2 = new TaskBuilder()
                .name("send-invoice")
                .retryStrategy(new FixedDelay(3, 500))
                .build();

        engine.submit(task1);
        engine.submit(task2);

        Thread.sleep(5000); // wait for tasks to finish

        System.out.println(engine.getTask(task1.getId()));
        System.out.println(engine.getTask(task2.getId()));

        engine.shutdown();
    }

}