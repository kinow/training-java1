package br.eti.kinoshita.training_java1.pubsub;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

public abstract class Task<T> implements Runnable {

    private final UUID identifier;
    private final LocalDateTime created;
    private final LocalDateTime started;
    private final LocalDateTime finished;
    private final T result;

    private final Random generator = new Random();

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
     * A dummy task that simply lasts betwen 500 and 1000 milliseconds. Its only output is the
     * current nano time from the system, printed to the console output.
     */
    public void run() {
        try {
            System.out.println(System.nanoTime());
            Thread.sleep(generator.nextInt(500) + 500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
