package fili5rovic.codegalaxy.util.downloader;

import javafx.concurrent.Task;

public class TestProgressTask extends Task<Void> {

    private final String[] downloadMessages = {
            "Initializing...",
            "Validating target directory...",
            "Checking existing installation...",
            "Preparing download...",
            "Connecting to server...",
            "Starting download...",
            "Downloading: 15.2 MB",
            "Download progress: 25%",
            "Download progress: 50%",
            "Download progress: 75%",
            "Download complete!",
            "Validating archive...",
            "Extracting archive...",
            "Extracting... (50/200 files)",
            "Extracting... (100/200 files)",
            "Extracting... (150/200 files)",
            "Extracting... (200/200 files)",
            "Cleaning up archive...",
            "Installation complete!"
    };

    public TestProgressTask() {
        updateTitle("Test Download Task");
        updateMessage("Starting test...");
        updateProgress(0, 100);
    }

    @Override
    protected Void call() throws Exception {
        try {
            int totalSteps = downloadMessages.length;
            double stepDuration = 5000.0 / totalSteps; // 5 seconds total

            for (int i = 0; i < totalSteps; i++) {
                // Check if task was cancelled
                if (isCancelled()) {
                    updateMessage("Task was cancelled");
                    throw new InterruptedException("Task cancelled by user");
                }

                // Update message and progress
                updateMessage(downloadMessages[i]);
                double progress = ((double) (i + 1) / totalSteps) * 100;
                updateProgress(progress, 100);

                // Sleep for the step duration
                Thread.sleep((long) stepDuration);

                // Add some variation to make it more realistic
                if (i == 5) { // Simulate slower download start
                    Thread.sleep(200);
                } else if (i >= 6 && i <= 10) { // Simulate download chunks
                    Thread.sleep(100);
                } else if (i >= 12 && i <= 16) { // Simulate extraction
                    Thread.sleep(150);
                }
            }

            updateMessage("Test completed successfully!");
            updateProgress(100, 100);
            return null;

        } catch (InterruptedException e) {
            if (isCancelled()) {
                updateMessage("Test cancelled");
                throw e;
            } else {
                throw new Exception("Test interrupted unexpectedly", e);
            }
        } catch (Exception e) {
            updateMessage("Test failed: " + e.getMessage());
            throw e;
        }
    }
}