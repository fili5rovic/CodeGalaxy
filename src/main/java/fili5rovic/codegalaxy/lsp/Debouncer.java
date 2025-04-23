package fili5rovic.codegalaxy.lsp;

import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;

public class Debouncer {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> future;
    private final List<Runnable> completionCallbacks = new ArrayList<>();

    public void debounce(Runnable action, long delayMs) {
        if (future != null && !future.isDone()) {
            future.cancel(false);
        }

        Runnable wrappedAction = () -> {
            try {
                action.run();
            } finally {
                Platform.runLater(() -> {
                    for (Runnable callback : completionCallbacks) {
                        callback.run();
                    }
                });
            }
        };

        future = scheduler.schedule(wrappedAction, delayMs, TimeUnit.MILLISECONDS);
    }

    public void addCompletionCallback(Runnable callback) {
        completionCallbacks.add(callback);
    }

    public boolean removeCompletionCallback(Runnable callback) {
        return completionCallbacks.remove(callback);
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