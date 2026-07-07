# JTasker ⚙️
### Multi-threaded Task Scheduling Engine — Pure Java

> A production-quality task scheduling library built from scratch in pure Java — no frameworks, no magic.
> Comparable in concept to **Quartz Scheduler** and Spring's `@Scheduled`, but built ground-up to understand
> every layer: concurrent execution, pluggable retry policies, and crash recovery via Write-Ahead Log.

---

## Why JTasker?

Most developers *use* task schedulers. JTasker was built to understand how they *work* —
thread pools, concurrent job registries, retry strategies, and durable state persistence.
Every design decision maps to a real engineering concept.

---

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                        Client                           │
│                                                         │
│   Task task = new TaskBuilder()                         │
│       .name("send-email")                               │
│       .retryStrategy(new ExponentialBackoff(3, 1000))   │
│       .build();                                         │
│                                                         │
│   engine.submit(task);                                  │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│                    TaskEngine                           │
│                                                         │
│   ConcurrentHashMap  ──────►  Job Registry              │
│   ExecutorService    ──────►  Thread Pool               │
│   WriteAheadLog      ──────►  Crash Recovery            │
└────────────────────┬────────────────────────────────────┘
                     │
          ┌──────────┴──────────┐
          ▼                     ▼
┌─────────────────┐   ┌─────────────────┐
│   Worker Thread │   │   Worker Thread │
│                 │   │                 │
│  PENDING        │   │  PENDING        │
│     ↓           │   │     ↓           │
│  RUNNING        │   │  RUNNING        │
│     ↓           │   │     ↓           │
│  DONE/FAILED    │   │  RETRYING       │
└─────────────────┘   │     ↓           │
                      │  DONE/FAILED    │
                      └─────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│                   wal.log (disk)                        │
│                                                         │
│   RUNNING uuid-1 send-welcome-email                     │
│   DONE    uuid-1 send-welcome-email                     │
│   RUNNING uuid-2 send-invoice                           │
│   RETRYING uuid-2 send-invoice                          │
│   FAILED  uuid-2 send-invoice                           │
└─────────────────────────────────────────────────────────┘
```

---

## Features

### 🔁 Pluggable Retry Strategies
Three built-in strategies via the **Strategy design pattern** — swap without touching engine code:

| Strategy | Behavior | Use Case |
|---|---|---|
| `NoRetry` | Fails immediately | Fire-and-forget tasks |
| `FixedDelay` | Constant wait between retries | Simple retry with known recovery time |
| `ExponentialBackoff` | Delay doubles each attempt | Prevents thundering herd on overloaded services |

### ⚡ Concurrent Execution Engine
- Fixed thread pool sized to available CPU cores via `Runtime.getRuntime().availableProcessors()`
- Thread-safe job registry using `ConcurrentHashMap` with atomic `putIfAbsent()` — prevents duplicate task registration
- Non-blocking task submission — engine never blocks the calling thread

### 🔄 Full Job Lifecycle
```
PENDING → RUNNING → DONE
                 ↘ RETRYING → DONE
                            ↘ FAILED
```

### 💾 Write-Ahead Log (WAL)
Every state transition is persisted to `wal.log` before being applied — inspired by how **PostgreSQL** and **Kafka** guarantee durability. On startup, the engine replays the log to recover tasks that were in-flight during a crash.

### 🏗️ Builder Pattern
Fluent API for task configuration — no telescoping constructors, self-documenting code:
```java
Task task = new TaskBuilder()
    .name("generate-monthly-report")
    .retryStrategy(new ExponentialBackoff(5, 2000))
    .build();
```

---

## Project Structure

```
JTasker/
├── src/main/java/com/JTasker/
│   ├── model/
│   │   ├── Task.java               # Abstract base — id, name, status, retryStrategy
│   │   └── TaskStatus.java         # PENDING, RUNNING, RETRYING, DONE, FAILED
│   ├── strategy/
│   │   ├── RetryStrategy.java      # Interface — maxRetries(), delayInMillis(attempt)
│   │   └── strategies/
│   │       ├── NoRetry.java
│   │       ├── FixedDelay.java
│   │       └── ExponentialBackoff.java
│   ├── builder/
│   │   └── TaskBuilder.java        # Fluent builder with UUID auto-generation
│   ├── tasks/
│   │   └── EmailTask.java          # Concrete task implementation
│   ├── engine/
│   │   └── TaskEngine.java         # Core — thread pool, registry, WAL, retry loop
│   ├── wal/
│   │   └── WriteAheadLog.java      # Append-only log, recovery, clear
│   └── Main.java
└── pom.xml
```

---

## Quick Start

```java
// 1. Start the engine
TaskEngine engine = new TaskEngine();
engine.start(); // replays WAL if crash recovery needed

// 2. Build tasks
Task emailTask = new TaskBuilder()
    .name("send-welcome-email")
    .retryStrategy(new ExponentialBackoff(3, 1000))
    .build();

Task invoiceTask = new TaskBuilder()
    .name("send-invoice")
    .retryStrategy(new FixedDelay(5, 500))
    .build();

// 3. Submit — non-blocking, runs on worker threads
engine.submit(emailTask);
engine.submit(invoiceTask);

// 4. Shutdown gracefully
engine.shutdown();
```

---

## Design Decisions

**Why abstract class for `Task` and not interface?**
`Task` holds mutable state — `id`, `name`, `status`, `retryStrategy`. Interfaces cannot hold instance state meaningfully. Abstract class enforces the contract (`execute()`) while providing shared implementation.

**Why `ConcurrentHashMap` over `synchronized HashMap`?**
`synchronized HashMap` locks the entire map per operation. `ConcurrentHashMap` uses bucket-level locking — concurrent reads and writes to different keys don't block each other, critical for a high-throughput task registry.

**Why UUID for task IDs?**
Sequential `long` IDs require coordination across threads (and nodes in a distributed system). `UUID.randomUUID()` generates a globally unique 128-bit identifier with no coordination needed.

**Why Write-Ahead Log before in-memory update?**
If the engine crashes after updating memory but before persisting — state is lost. WAL ensures every transition is on disk first. On recovery, the log is the source of truth.

---

## Roadmap

- [x] Abstract task model with full lifecycle
- [x] Pluggable retry strategies (NoRetry, FixedDelay, ExponentialBackoff)
- [x] Fluent Builder API with UUID auto-generation
- [x] Multi-threaded execution engine (ExecutorService)
- [x] Concurrent job registry (ConcurrentHashMap)
- [x] Write-Ahead Log for crash recovery
- [ ] JUnit 5 test coverage
- [ ] Task timeout support
- [ ] Graceful shutdown with task drain
- [ ] REST API wrapper (Spring Boot)

---

## Tech Stack

- **Java 17**
- **Maven**
- **JUnit 5** *(in progress)*

---

## Key Concepts Demonstrated

`Abstract Classes` `Interfaces` `Strategy Pattern` `Builder Pattern` `ExecutorService` `ConcurrentHashMap` `Thread Pools` `Runnable` `volatile` `UUID` `File I/O` `NIO Files API` `Write-Ahead Log` `Crash Recovery` `Enum State Machine`

