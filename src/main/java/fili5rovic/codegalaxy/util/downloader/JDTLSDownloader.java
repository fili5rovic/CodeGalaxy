package fili5rovic.codegalaxy.util.downloader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.nio.file.*;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

public class JDTLSDownloader {

    private static final int CONNECTION_TIMEOUT = 30_000;
    private static final int READ_TIMEOUT = 60_000;
    private static final long MIN_EXTRACT_DIR_SIZE = 1024;
    private static final int MAX_PATH_LENGTH = 260; // Windows path limit

    private final JDTLSRelease release;
    private final Path targetDirectory;
    private final String fileName;
    private final Path extractDirectory;

    public JDTLSDownloader(JDTLSRelease release, Path targetDirectory, String fileName) {
        this.release = release;
        this.targetDirectory = targetDirectory;
        this.fileName = fileName;
        this.extractDirectory = targetDirectory.resolve("lsp");
    }

    public void download() throws IOException {
        validateTargetDirectory();

        if (isValidExistingExtraction()) {
            System.out.println("JDTLS already extracted at: " + extractDirectory);
            return;
        }

        // Clean up any incomplete extraction
        cleanupIncompleteExtraction();

        Path archivePath = downloadArchiveIfNeeded();
        try {
            validateArchive(archivePath);
            extract(archivePath);
        } finally {
            cleanupArchive(archivePath);
        }
    }

    private void validateTargetDirectory() throws IOException {
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

        // Check available disk space (at least 100MB for safety)
        try {
            long freeSpace = Files.getFileStore(targetDirectory).getUsableSpace();
            if (freeSpace < 100 * 1024 * 1024) {
                throw new IOException("Insufficient disk space. Available: " + (freeSpace / 1024 / 1024) + " MB");
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not check disk space: " + e.getMessage());
        }
    }

    private boolean isValidExistingExtraction() {
        if (!Files.exists(extractDirectory) || !Files.isDirectory(extractDirectory)) {
            return false;
        }

        try (var pathStream = Files.walk(extractDirectory)) {
            // Check if directory has reasonable content
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
        if (Files.exists(extractDirectory)) {
            System.out.println("Cleaning up incomplete extraction...");
            try (var pathStream = Files.walk(extractDirectory)) {
                pathStream.sorted((a, b) -> b.compareTo(a)) // Delete files before directories
                        .forEach(path -> {
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
        Path archivePath = targetDirectory.resolve(fileName);

        if (Files.exists(archivePath)) {
            System.out.println("Archive already exists: " + archivePath);
            return archivePath;
        }

        return downloadArchive(archivePath);
    }

    private Path downloadArchive(Path outputPath) throws IOException {
        System.out.println("Starting download to: " + outputPath);

        HttpURLConnection conn = null;
        try {
            conn = createConnection();
            DownloadProgress progress = new DownloadProgress(conn.getContentLength());

            try (InputStream in = new BufferedInputStream(conn.getInputStream());
                 FileOutputStream out = new FileOutputStream(outputPath.toFile())) {

                transferData(in, out, progress);
            }

            System.out.println("Download complete: " + outputPath.toAbsolutePath());
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

    private void transferData(InputStream in, OutputStream out, DownloadProgress progress) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;

        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
            progress.update(bytesRead);
        }
    }

    private void validateArchive(Path archivePath) throws IOException {
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
        System.out.println("Extracting to: " + extractDirectory);

        try {
            Files.createDirectories(extractDirectory);
        } catch (IOException e) {
            throw new IOException("Cannot create extraction directory: " + extractDirectory, e);
        }

        try (FileInputStream fis = new FileInputStream(archivePath.toFile());
             GZIPInputStream gis = new GZIPInputStream(fis);
             TarArchiveInputStream tis = new TarArchiveInputStream(gis)) {

            extractEntries(tis, extractDirectory);
        } catch (IOException e) {
            // Clean up partial extraction
            cleanupIncompleteExtraction();
            throw new IOException("Extraction failed: " + e.getMessage(), e);
        }

        System.out.println("Extraction complete to: " + extractDirectory.toAbsolutePath());
    }

    private void extractEntries(TarArchiveInputStream tis, Path outputDir) throws IOException {
        TarArchiveEntry entry;
        while ((entry = tis.getNextEntry()) != null) {
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
        }
    }

    private void extractFile(TarArchiveInputStream tis, Path outPath) throws IOException {
        try {
            Files.createDirectories(outPath.getParent());
        } catch (IOException e) {
            throw new IOException("Cannot create parent directory for: " + outPath, e);
        }

        try (OutputStream out = Files.newOutputStream(outPath)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = tis.read(buffer)) != -1) {
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
                System.out.println("Deleted archive: " + archivePath.getFileName());
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not delete archive " + archivePath + ": " + e.getMessage());
        }
    }

    // Helper class to handle download progress tracking
    private static class DownloadProgress {
        private final int contentLength;
        private final boolean hasContentLength;
        private long totalRead = 0;
        private int lastPercent = 0;

        public DownloadProgress(int contentLength) {
            this.contentLength = contentLength;
            this.hasContentLength = contentLength > 0;

            if (hasContentLength) {
                System.out.printf("Total size: %.2f MB%n", contentLength / (1024.0 * 1024));
            }
        }

        public void update(int bytesRead) {
            totalRead += bytesRead;

            if (hasContentLength) {
                int percent = (int) ((totalRead * 100) / contentLength);
                if (percent >= lastPercent + 5) {
                    System.out.println("Progress: " + percent + "%");
                    lastPercent = percent;
                }
            }
        }
    }
}