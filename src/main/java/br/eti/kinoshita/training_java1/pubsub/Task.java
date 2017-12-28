package br.eti.kinoshita.training_java1.pubsub;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/** The task. */
public abstract class Task implements Runnable {

    private final LocalDateTime created;
    private final int identifier;
    private LocalDateTime started;
    private LocalDateTime finished;
    private Object result;

    private final Random generator = new Random();

    private static final Logger LOGGER = Logger.getLogger(Task.class.getName());

    public Task(int identifier) {
        super();
        this.identifier = identifier;
        this.created = LocalDateTime.now(Clock.systemUTC());
    }

    public int getIdentifier() {
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

    public Object getResult() {
        return result;
    }

    public void setStarted(LocalDateTime started) {
        this.started = started;
    }

    public void setFinished(LocalDateTime finished) {
        this.finished = finished;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Random getGenerator() {
        return generator;
    }

    /**
     * A dummy task that simply lasts betwen 500 and 1000 milliseconds. It logs the current nano time at every
     * execution.
     */
    public void run() {
        try {
            started = LocalDateTime.now(Clock.systemUTC());
            // LOGGER.info(String.format("TASK -- STARTED"));
            Thread.sleep(generator.nextInt(500) + 500);
            finished = LocalDateTime.now(Clock.systemUTC());
            result = finished.toInstant(ZoneOffset.UTC).toEpochMilli()
                    - started.toInstant(ZoneOffset.UTC).toEpochMilli();
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Task failed: " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        // number of tasks to be processed
        final int numberOfTasks = 50;
        // buffer supporting 10 tasks only
        final BlockingQueue<Task> tasks = new ArrayBlockingQueue<>(10, /* fair */ false);
        // 1 producer
        ExecutorService producer = Executors.newFixedThreadPool(1);
        producer.submit(new Runnable() {
            @Override
            public void run() {
                int submitted = 0;
                while (true) {
                    LOGGER.info("PRODUCER -- Adding number task " + submitted);
                    try {
                        tasks.put(new Task(submitted) {
                        });
                        submitted++;
                        if (submitted == numberOfTasks) {
                            LOGGER.info("PRODUCER -- out");
                            break;
                        }
                    } catch (InterruptedException e) {
                        LOGGER.warning(e.getMessage());
                    }
                }
            }
        }, "producer");
        producer.shutdown();
        // 1 consumer
        ExecutorService consumer = Executors.newFixedThreadPool(1);
        consumer.submit(new Runnable() {
            private int processed = 0;

            @Override
            public void run() {
                Task task = null;
                while (true) {
                    try {
                        task = tasks.take();
                        task.run();
                        LOGGER.info("CONSUMER -- Number task " + task.getIdentifier() + " received. Took: "
                                + task.getResult());
                        processed++;
                        if (processed == numberOfTasks) {
                            LOGGER.info("CONSUMER -- out");
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "consumer");
        consumer.shutdown();
        LOGGER.info("OK!");
    }
}
