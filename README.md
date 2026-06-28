# JTasker — Multi-threaded Task Scheduling Engine

A production-quality task scheduling library built in **pure Java** with no frameworks.
Comparable in concept to Quartz Scheduler and Spring's `@Scheduled`.

Built as a ground-up Java project to implement real backend engineering concepts —
multithreading, concurrent data structures, retry mechanisms, crash recovery, and clean OOP design.

---

## Features

- Pluggable retry strategies — Fixed Delay, Exponential Backoff, No Retry
- Builder pattern for clean, readable task configuration
- Full job lifecycle — `PENDING → RUNNING → DONE / FAILED / RETRYING`
- Multi-threaded execution engine *(in progress)*
- Write-Ahead Log for crash recovery *(in progress)*

---

## Tech Stack

- Java 17
- Maven
- JUnit 5

---

## Project Structure
JTasker/

├── src/

│   └── main/

│       └── java/

│           └── com/JTasker/

│               ├── model/

│               │   ├── Task.java

│               │   └── TaskStatus.java

│               ├── strategy/

│               │   ├── RetryStrategy.java

│               │   └── strategies/

│               │       ├── FixedDelay.java

│               │       ├── ExponentialBackoff.java

│               │       └── NoRetry.java

│               ├── builder/

│               │   └── TaskBuilder.java

│               ├── tasks/

│               │   └── EmailTask.java

│               └── Main.java

└── pom.xml

---

## How It Works

```java
// Create and configure a task using the Builder
Task task = new TaskBuilder()
    .id("001")
    .name("send-welcome-email")
    .retryStrategy(new ExponentialBackoff(3, 1000))
    .build();

// Submit to engine (coming soon)
engine.submit(task);
```

---

## Roadmap

- [x] Task model with full lifecycle
- [x] Pluggable retry strategies
- [x] Builder pattern for task configuration
- [ ] Multi-threaded execution engine
- [ ] Job registry with ConcurrentHashMap
- [ ] Write-Ahead Log for crash recovery
- [ ] JUnit 5 test coverage