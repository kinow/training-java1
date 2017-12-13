package br.eti.kinoshita.training_java1.pubsub;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Task<T> implements Runnable {

    private final UUID identifier;
    private final LocalDateTime created;
    private LocalDateTime started;
    private LocalDateTime finished;
    private T result;

    private final Random generator = new Random();

    private static final Logger LOGGER = Logger.getLogger(Task.class.getName());

    public Task() {
        super();
        this.identifier = UUID.randomUUID();
        this.created = LocalDateTime.now(Clock.systemUTC());
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

    static class NumberTask extends Task<Number> {

    }

    /**
     * A dummy task that simply lasts betwen 500 and 1000 milliseconds. It logs the current nano time at every
     * execution.
     */
    public void run() {
        try {
            started = LocalDateTime.now(Clock.systemUTC());
            LOGGER.info(String.format("Thread started at %s", started.toString()));
            Thread.sleep(generator.nextInt(500) + 500);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Task failed: " + e.getMessage(), e);
        } finally {
            finished = LocalDateTime.now(Clock.systemUTC());
        }
    }

    public static void main(String[] args) {
        final int numberOfTasks = 50;
        final List<Task<?>> tasks = Collections.synchronizedList(new ArrayList<>(numberOfTasks));
        // 1 producer
        ExecutorService producer = Executors.newFixedThreadPool(1);
        producer.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0 ; i < numberOfTasks; i++) {
                    LOGGER.info("Adding ");
                    tasks.add(new NumberTask());
                }
            }
        });
        producer.shutdown();
        LOGGER.info("OK!");
    }
}
