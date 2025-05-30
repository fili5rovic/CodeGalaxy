package fili5rovic.codegalaxy.console;

import javafx.application.Platform;

public class ProcessHelper {


    public static void waitForProcessExit(ConsoleArea console, Process process) {
        new Thread(() -> {
            try {
                int exitCode = process.waitFor();
                Platform.runLater(() -> {
                    onProcessExit(console, exitCode);
                });
            } catch (InterruptedException e) {
                System.err.println("Process wait interrupted: " + e.getMessage());
            }
        }).start();
    }

    private static void onProcessExit(ConsoleArea console, int code) {
        console.setEditable(false);
        console.appendText("\nProcess finished with code: " + code + "\n");

        console.moveTo(console.getLength());
        console.requestFollowCaret();
    }
}
