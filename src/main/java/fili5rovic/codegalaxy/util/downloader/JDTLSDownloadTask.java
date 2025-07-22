package fili5rovic.codegalaxy.util.downloader;

import javafx.concurrent.Task;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.nio.file.*;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

public class JDTLSDownloadTask extends Task<Void> {

    private static final int CONNECTION_TIMEOUT = 30_000;
    private static final int READ_TIMEOUT = 60_000;
    private static final long MIN_EXTRACT_DIR_SIZE = 1024;
    private static final int MAX_PATH_LENGTH = 260; // Windows path limit

    private final JDTLSRelease release;
    private final Path targetDirectory;
    private final Path extractDirectory;

    // Progress tracking
    private long totalBytes = 0;
    private long processedBytes = 0;
    private int totalEntries = 0;
    private int processedEntries = 0;

    public JDTLSDownloadTask(JDTLSRelease release) {
        this.release = release;
        this.targetDirectory = Path.of(System.getProperty("user.dir"));
        this.extractDirectory = targetDirectory.resolve("lsp");

        // Set initial task properties
        updateTitle("JDTLS Download");
        updateMessage("Initializing...");
        updateProgress(0, 100);
    }

    @Override
    protected Void call() throws Exception {
        try {
            download();
            updateMessage("Download and extraction completed successfully!");
            updateProgress(100, 100);
            return null;
        } catch (Exception e) {
            updateMessage("Error: " + e.getMessage());
            throw e;
        }
    }

    private void download() throws IOException {
        updateMessage("Validating target directory...");
        validateTargetDirectory();

        if (isValidExistingExtraction()) {
            updateMessage("JDTLS already extracted at: " + extractDirectory);
            updateProgress(100, 100);
            return;
        }

        updateMessage("Cleaning up incomplete extraction...");
        cleanupIncompleteExtraction();

        updateMessage("Preparing download...");
        Path archivePath = downloadArchiveIfNeeded();
        try {
            updateMessage("Validating archive...");
            validateArchive(archivePath);

            updateMessage("Extracting archive...");
            extract(archivePath);
        } finally {
            updateMessage("Cleaning up archive...");
            cleanupArchive(archivePath);
        }
    }

    private void validateTargetDirectory() throws IOException {
        if (isCancelled()) throw new IOException("Task was cancelled");

        if (!Files.exists(targetDirectory)) {
            try {
                Files.createDirectories(targetDirectory);
            } catch (IOException e) {
                throw new IOException("Cannot create target directory: " + targetDirectory, e);
            }
        }

        if (!Files.isWritable(targetDirectory)) {
            throw new IOException("Target directory is not writable: " + targetDirectory);
        }

        long minRequiredSpace = 100 * 1024 * 1024; // 100 MB
        try {
            long freeSpace = Files.getFileStore(targetDirectory).getUsableSpace();
            if (freeSpace < minRequiredSpace) {
                throw new IOException("Insufficient disk space. Available: " + (freeSpace / 1024 / 1024) + " MB");
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not check disk space: " + e.getMessage());
        }
    }

    public boolean isValidExistingExtraction() {
        if (isCancelled()) return false;

        if (!Files.exists(extractDirectory) || !Files.isDirectory(extractDirectory)) {
            return false;
        }

        try (var pathStream = Files.walk(extractDirectory)) {
            long dirSize = pathStream
                    .filter(Files::isRegularFile)
                    .mapToLong(path -> {
                        try {
                            return Files.size(path);
                        } catch (IOException e) {
                            return 0;
                        }
                    })
                    .sum();

            return dirSize > MIN_EXTRACT_DIR_SIZE;
        } catch (IOException e) {
            System.out.println("Warning: Could not validate existing extraction: " + e.getMessage());
            return false;
        }
    }

    private void cleanupIncompleteExtraction() throws IOException {
        if (isCancelled()) throw new IOException("Task was cancelled");

        if (Files.exists(extractDirectory)) {
            try (var pathStream = Files.walk(extractDirectory)) {
                pathStream.sorted((a, b) -> b.compareTo(a))
                        .forEach(path -> {
                            if (isCancelled()) return;
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                System.out.println("Warning: Could not delete " + path + ": " + e.getMessage());
                            }
                        });
            } catch (IOException e) {
                throw new IOException("Failed to cleanup incomplete extraction", e);
            }
        }
    }

    private Path downloadArchiveIfNeeded() throws IOException {
        if (isCancelled()) throw new IOException("Task was cancelled");

        Path archivePath = targetDirectory.resolve(release.getArchiveName());

        if (Files.exists(archivePath)) {
            updateMessage("Archive already exists: " + archivePath.getFileName());
            return archivePath;
        }

        return downloadArchive(archivePath);
    }

    private Path downloadArchive(Path outputPath) throws IOException {
        if (isCancelled()) throw new IOException("Task was cancelled");

        updateMessage("Starting download: " + outputPath.getFileName());

        HttpURLConnection conn = null;
        try {
            conn = createConnection();
            totalBytes = conn.getContentLengthLong();
            processedBytes = 0;

            if (totalBytes > 0) {
                updateMessage(String.format("Downloading: %.2f MB", totalBytes / (1024.0 * 1024)));
            } else {
                updateMessage("Downloading (size unknown)");
            }

            try (InputStream in = new BufferedInputStream(conn.getInputStream());
                 FileOutputStream out = new FileOutputStream(outputPath.toFile())) {

                transferData(in, out);
            }

            updateMessage("Download complete: " + outputPath.getFileName());
            return outputPath;

        } catch (SocketTimeoutException e) {
            throw new IOException("Download timed out. Please check your internet connection.", e);
        } catch (IOException e) {
            // Clean up partial download
            try {
                Files.deleteIfExists(outputPath);
            } catch (IOException cleanupEx) {
                System.out.println("Warning: Could not clean up partial download: " + cleanupEx.getMessage());
            }
            throw new IOException("Download failed: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private HttpURLConnection createConnection() throws IOException {
        try {
            HttpURLConnection conn = (HttpURLConnection) URI.create(release.getUrl()).toURL().openConnection();
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);

            // Handle redirects manually to avoid issues with different redirect types
            int redirectCount = 0;
            while (redirectCount < 5) { // Prevent infinite redirects
                if (isCancelled()) throw new IOException("Task was cancelled");

                int status = conn.getResponseCode();
                if (status == HttpURLConnection.HTTP_MOVED_TEMP ||
                        status == HttpURLConnection.HTTP_MOVED_PERM ||
                        status == HttpURLConnection.HTTP_SEE_OTHER) {

                    String redirectUrl = conn.getHeaderField("Location");
                    if (redirectUrl == null) {
                        throw new IOException("Received redirect response but no Location header");
                    }

                    conn = (HttpURLConnection) URI.create(redirectUrl).toURL().openConnection();
                    conn.setInstanceFollowRedirects(true);
                    conn.setConnectTimeout(CONNECTION_TIMEOUT);
                    conn.setReadTimeout(READ_TIMEOUT);
                    redirectCount++;
                } else if (status >= 400) {
                    throw new IOException("HTTP error " + status + ": " + conn.getResponseMessage());
                } else {
                    break;
                }
            }

            if (redirectCount >= 5) {
                throw new IOException("Too many redirects");
            }

            return conn;
        } catch (Exception e) {
            throw new IOException("Failed to create connection: " + e.getMessage(), e);
        }
    }

    private void transferData(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;

        while ((bytesRead = in.read(buffer)) != -1) {
            if (isCancelled()) {
                throw new IOException("Task was cancelled");
            }

            out.write(buffer, 0, bytesRead);
            processedBytes += bytesRead;

            // Update progress for download
            if (totalBytes > 0) {
                double progress = (double) processedBytes / totalBytes * 50; // Download is 50% of total progress
                updateProgress(progress, 100);
            }
        }
    }

    private void validateArchive(Path archivePath) throws IOException {
        if (isCancelled()) throw new IOException("Task was cancelled");

        if (!Files.exists(archivePath)) {
            throw new IOException("Archive not found: " + archivePath);
        }

        if (Files.size(archivePath) < 1024) { // Archive should be at least 1KB
            throw new IOException("Archive appears to be corrupted (too small): " + archivePath);
        }

        // Try to open the archive to validate it's not corrupted
        try (FileInputStream fis = new FileInputStream(archivePath.toFile());
             GZIPInputStream gis = new GZIPInputStream(fis);
             TarArchiveInputStream tis = new TarArchiveInputStream(gis)) {

            TarArchiveEntry entry = tis.getNextEntry();
            if (entry == null) {
                throw new IOException("Archive appears to be empty or corrupted");
            }
        } catch (IOException e) {
            throw new IOException("Archive validation failed: " + e.getMessage(), e);
        }
    }

    private void extract(Path archivePath) throws IOException {
        if (isCancelled()) throw new IOException("Task was cancelled");

        updateMessage("Extracting to: " + extractDirectory.getFileName());

        try {
            Files.createDirectories(extractDirectory);
        } catch (IOException e) {
            throw new IOException("Cannot create extraction directory: " + extractDirectory, e);
        }

        // First pass: count entries for progress tracking
        try (FileInputStream fis = new FileInputStream(archivePath.toFile());
             GZIPInputStream gis = new GZIPInputStream(fis);
             TarArchiveInputStream tis = new TarArchiveInputStream(gis)) {

            TarArchiveEntry entry;
            totalEntries = 0;
            while ((entry = tis.getNextEntry()) != null) {
                totalEntries++;
            }
        }

        // Second pass: actual extraction
        try (FileInputStream fis = new FileInputStream(archivePath.toFile());
             GZIPInputStream gis = new GZIPInputStream(fis);
             TarArchiveInputStream tis = new TarArchiveInputStream(gis)) {

            extractEntries(tis, extractDirectory);
        } catch (IOException e) {
            // Clean up partial extraction
            cleanupIncompleteExtraction();
            throw new IOException("Extraction failed: " + e.getMessage(), e);
        }

        updateMessage("Extraction complete: " + extractDirectory.getFileName());
    }

    private void extractEntries(TarArchiveInputStream tis, Path outputDir) throws IOException {
        TarArchiveEntry entry;
        processedEntries = 0;

        while ((entry = tis.getNextEntry()) != null) {
            if (isCancelled()) {
                throw new IOException("Task was cancelled");
            }

            // Security: Prevent path traversal attacks
            String name = entry.getName();
            if (name.contains("..") || name.startsWith("/") || name.contains("\\..")) {
                System.out.println("Skipping potentially unsafe entry: " + name);
                continue;
            }

            Path outPath = outputDir.resolve(name).normalize();

            // Double-check the resolved path is still within our target directory
            if (!outPath.startsWith(outputDir)) {
                System.out.println("Skipping entry outside target directory: " + name);
                continue;
            }

            // Check path length limits
            if (outPath.toString().length() > MAX_PATH_LENGTH) {
                System.out.println("Skipping entry with path too long: " + name);
                continue;
            }

            if (entry.isDirectory()) {
                try {
                    Files.createDirectories(outPath);
                } catch (IOException e) {
                    System.out.println("Warning: Could not create directory " + outPath + ": " + e.getMessage());
                }
            } else {
                extractFile(tis, outPath);
            }

            processedEntries++;
            // Extraction is the remaining 50% of progress
            double extractionProgress = 50 + ((double) processedEntries / totalEntries * 50);
            updateProgress(extractionProgress, 100);

            if (processedEntries % 10 == 0) { // Update message every 10 files
                updateMessage(String.format("Extracting... (%d/%d files)", processedEntries, totalEntries));
            }
        }
    }

    private void extractFile(TarArchiveInputStream tis, Path outPath) throws IOException {
        if (isCancelled()) throw new IOException("Task was cancelled");

        try {
            Files.createDirectories(outPath.getParent());
        } catch (IOException e) {
            throw new IOException("Cannot create parent directory for: " + outPath, e);
        }

        try (OutputStream out = Files.newOutputStream(outPath)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = tis.read(buffer)) != -1) {
                if (isCancelled()) {
                    throw new IOException("Task was cancelled");
                }
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            try {
                Files.deleteIfExists(outPath);
            } catch (IOException cleanupEx) {
                System.out.println("Warning: Could not clean up failed file extraction: " + cleanupEx.getMessage());
            }
            throw new IOException("Failed to extract file: " + outPath, e);
        }
    }

    private void cleanupArchive(Path archivePath) {
        try {
            if (Files.exists(archivePath)) {
                Files.delete(archivePath);
                updateMessage("Deleted archive: " + archivePath.getFileName());
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not delete archive " + archivePath + ": " + e.getMessage());
        }
    }
}
