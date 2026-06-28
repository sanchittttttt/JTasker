package com.JTasker;

import com.JTasker.builder.TaskBuilder;
import com.JTasker.model.Task;
import com.JTasker.strategy.strategies.NoRetry;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Task task = new TaskBuilder()
                .id("001")
                .name("send-welcome-email")
                .retryStrategy(new NoRetry())
                .build();

        System.out.println(task);
    }
}
