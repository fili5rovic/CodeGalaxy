package fili5rovic.codegalaxy.lsp;

import java.io.*;
import java.util.*;

public class LSPServerManager {
    private Process process;

    public void startServer(String workspacePath) throws IOException {
        List<String> command = Arrays.asList(
                "java",
                "-jar",
                "D:\\PROJECTS\\JavaCustomProjects\\CodeGalaxy\\lsp\\plugins\\org.eclipse.equinox.launcher_1.6.1000.v20250227-1734.jar",
                "-configuration",
                "D:\\PROJECTS\\JavaCustomProjects\\CodeGalaxy\\lsp\\config_win",
                "-data",
                workspacePath
        );

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);
        process = builder.start();

        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[LSP] " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stopServer() {
        if (process != null) {
            process.destroy();
        }
    }
}
