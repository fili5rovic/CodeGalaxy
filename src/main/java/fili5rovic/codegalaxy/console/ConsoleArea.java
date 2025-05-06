package fili5rovic.codegalaxy.console;

import fili5rovic.codegalaxy.console.behaviour.BehaviourListener;
import fili5rovic.codegalaxy.console.highlighter.Highlighter;
import javafx.application.Platform;
import org.fxmisc.richtext.CodeArea;

public class ConsoleArea extends CodeArea {

    private final Redirector redirector;

    public ConsoleArea(Process process) {
        Highlighter.apply(this);
        BehaviourListener.apply(this);

        this.redirector = new Redirector(this, process);
        this.redirector.redirectStreams();

        waitForProcessExit(process);

    }

    public void onProcessExit(int code) {
        setEditable(false);
        appendText("\nProcess finished with code: " + code + "\n");
    }

    public void writeInput(String input) {
        System.out.println("Wrote: " + input);
        redirector.writeInput(input);
    }

    private void waitForProcessExit(Process process) {
        new Thread(() -> {
            try {
                int exitCode = process.waitFor();
                Platform.runLater(() -> {
                    onProcessExit(exitCode);
                });
            } catch (InterruptedException e) {
                System.err.println("Process wait interrupted: " + e.getMessage());
            }
        }).start();
    }
}
