package br.eti.kinoshita.training_java1.pubsub;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Task<T> implements Runnable {

    private final UUID identifier;
    private final LocalDateTime created;
    private final LocalDateTime started;
    private final LocalDateTime finished;
    private final T result;

    private final Random generator = new Random();

    private static final Logger LOGGER = Logger.getLogger(Task.class.getName());

    public Task(UUID identifier, LocalDateTime created, LocalDateTime started, LocalDateTime finished, T result) {
        super();
        this.identifier = identifier;
        this.created = created;
        this.started = started;
        this.finished = finished;
        this.result = result;
    }

    public UUID getIdentifier() {
        return identifier;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public LocalDateTime getStarted() {
        return started;
    }

    public LocalDateTime getFinished() {
        return finished;
    }

    public T getResult() {
        return result;
    }

    /**
     * A dummy task that simply lasts betwen 500 and 1000 milliseconds. It logs the
     * current nano time at every execution.
     */
    public void run() {
        try {
            LOGGER.info(String.format("%d", System.nanoTime()));
            Thread.sleep(generator.nextInt(500) + 500);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Task failed: " + e.getMessage(), e);
        }
    }
}
