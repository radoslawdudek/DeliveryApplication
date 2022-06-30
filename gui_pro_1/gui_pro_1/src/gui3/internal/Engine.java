package gui3.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Engine {

    public static final long TPS = 20;
    private static final long MILLIS_PER_TICK = TimeUnit.SECONDS.toMillis(1) / TPS;

    private static final Engine INSTANCE = new Engine();

    public static Engine getInstance() {
        return INSTANCE;
    }

    private final List<Tickable> entities;
    private EngineThread thread;
    private long tick = 0;

    private Engine() {
        this.entities = new ArrayList<>();
    }

    public long getTick() {
        return tick;
    }

    public void register(Tickable entity) {
        entities.add(entity);
    }

    public boolean isRunning() {
        return thread != null && thread.isRunning();
    }

    public void start() {
        if (thread != null)
            throw new IllegalStateException("engine is already started");

        thread = new EngineThread();
        thread.start();
    }

    public void stop() {
        if (thread == null)
            throw new IllegalStateException("engine is already stopped");

        thread.terminate();
        thread = null;
    }

    private class EngineThread extends Thread {

        private final AtomicBoolean running = new AtomicBoolean(true);

        private long tick() {
            final long start = System.currentTimeMillis();
            entities.forEach(Tickable::tick);
            tick++;
            return System.currentTimeMillis() - start;
        }

        public boolean isRunning() {
            return running.get();
        }

        @Override
        public void run() {
            while (running.get()) {
                final long tickMillis = tick();
                final long deltaMillis = MILLIS_PER_TICK - tickMillis;

                if (deltaMillis > 0) {
                    try {
                        sleep(deltaMillis);
                    } catch (InterruptedException exception) {
                        throw new RuntimeException(exception);
                    }
                }
            }
        }

        public void terminate() {
            running.set(false);
        }
    }
}
