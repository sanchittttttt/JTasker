package com.JTasker.tasks;

import com.JTasker.model.Task;
import com.JTasker.strategy.RetryStrategy;

public class EmailTask extends Task
{

    public EmailTask(String id, String name, RetryStrategy retryStrategy) {
        super(id, name, retryStrategy);
    }

    @Override
    public void execute() {
        System.out.println("Executing email task: "+getName());
    }
}
