package fili5rovic.codegalaxy.lsp;

import java.util.concurrent.*;

public class Debouncer {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> future;

    public void debounce(Runnable action, long delayMs) {
        if (future != null && !future.isDone()) {
            future.cancel(false);
        }
        future = scheduler.schedule(action, delayMs, TimeUnit.MILLISECONDS);
    }

    public boolean isDebouncing() {
        return future != null && !future.isDone();
    }

    public void cancel() {
        if (future != null) {
            future.cancel(false);
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
