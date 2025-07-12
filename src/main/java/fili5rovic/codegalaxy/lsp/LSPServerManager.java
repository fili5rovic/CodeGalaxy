package fili5rovic.codegalaxy.lsp;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

class LSPServerManager {
    private Process process;

    private static final Pattern LAUNCHER_JAR_PATTERN =
            Pattern.compile("org\\.eclipse\\.equinox\\.launcher_.*\\.jar");

    public void startServer(String workspacePath) throws IOException {
        String projectPath = System.getProperty("user.dir");
        String pluginsPath = projectPath + File.separator + "lsp" + File.separator + "plugins";

        String launcherJar = findLauncherJar(pluginsPath);
        if (launcherJar == null) {
            throw new IOException("[START-ERROR] Eclipse Equinox launcher JAR not found in: " + pluginsPath);
        }

        List<String> command = Arrays.asList(
                "java",
                "-jar",
                launcherJar,
                "-configuration",
                projectPath + "\\lsp\\config_win",
                "-data",
                workspacePath
        );

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(false);
        process = builder.start();

        new Thread(() -> {
            try (BufferedReader err = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = err.readLine()) != null) {
                    System.err.println("[LSP-ERR] " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    private String findLauncherJar(String pluginsPath) throws IOException {
        Path pluginsDir = Paths.get(pluginsPath);

        if (!Files.exists(pluginsDir) || !Files.isDirectory(pluginsDir)) {
            throw new IOException("Plugins directory does not exist: " + pluginsPath);
        }

        try (Stream<Path> stream = Files.list(pluginsDir)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(fileName -> LAUNCHER_JAR_PATTERN.matcher(fileName).matches())
                    .findFirst()
                    .map(fileName -> pluginsDir.resolve(fileName).toString())
                    .orElse(null);
        } catch (IOException e) {
            throw new IOException("Error reading plugins directory: " + pluginsPath, e);
        }
    }

    public InputStream getInputStream() {
        return process.getInputStream();
    }

    public OutputStream getOutputStream() {
        return process.getOutputStream();
    }

    public void stopServer() {
        if (process != null) {
            process.destroy();
        }
    }
}
