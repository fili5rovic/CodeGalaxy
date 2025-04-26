package fili5rovic.codegalaxy.lsp;

import java.io.*;
import java.util.*;

class LSPServerManager {
    private Process process;

    public void startServer(String workspacePath) throws IOException {
        String projectPath = System.getProperty("user.dir");

        List<String> command = Arrays.asList(
                "java",
                "-jar",
                projectPath + "\\lsp\\plugins\\org.eclipse.equinox.launcher_1.6.1000.v20250227-1734.jar",
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
