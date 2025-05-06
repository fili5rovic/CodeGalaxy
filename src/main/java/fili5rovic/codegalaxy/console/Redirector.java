package fili5rovic.codegalaxy.console;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Redirector {

    private final Process process;
    private final ConsoleArea console;

    public Redirector(ConsoleArea console, Process process) {
        this.console = console;
        this.process = process;
    }

    public void sendInput(String input) {
        try {
            process.getOutputStream().write((input + "\n").getBytes());
            process.getOutputStream().flush();
        } catch (Exception e) {
            System.err.println("Error writing to input stream: " + e.getMessage());
        }
    }

    public void redirectStreams() {
        redirectOutput(process.getInputStream(), ConsoleArea.OUTPUT);
        redirectOutput(process.getErrorStream(), ConsoleArea.ERROR);
    }

    private void redirectOutput(InputStream inputStream, int type) {
        if (console == null) {
            System.err.println("Console is not set. Cannot redirect output.");
            return;
        }

        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String finalLine = line + "\n";
                    Platform.runLater(() -> {
                        System.out.println("Redirecting output: " + finalLine + " to type: " + type);
                        console.setTextType(type);
                        console.appendText(finalLine);
                    });
                }
            } catch (Exception e) {
                System.err.println("Error reading output stream: " + e.getMessage());
            }
        }).start();
    }
}
