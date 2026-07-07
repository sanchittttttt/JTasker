package com.JTasker.wal;

import com.JTasker.model.TaskStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class WriteAheadLog
{
    private Path file = Paths.get("wal.log");

    public void log(String taskId, String taskName, TaskStatus taskStatus)
    {
        try {
            Files.writeString(file, taskStatus+" "+taskId+" "+taskName+"\n", StandardOpenOption.CREATE ,StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> recover() {
        if (!Files.exists(file)) {
            return List.of(); // return empty list if no WAL exists
        }
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clear()
    {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
