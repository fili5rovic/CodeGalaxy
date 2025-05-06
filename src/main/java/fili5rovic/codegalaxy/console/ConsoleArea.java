package fili5rovic.codegalaxy.console;

import fili5rovic.codegalaxy.console.behaviour.BehaviourListener;
import fili5rovic.codegalaxy.console.highlighter.Highlighter;
import org.fxmisc.richtext.CodeArea;

public class ConsoleArea extends CodeArea {

    public static final int INPUT = 0;
    public static final int OUTPUT = 1;
    public static final int ERROR = 2;

    private final Redirector redirector;

    private int textType = 1;

    public ConsoleArea(Process process) {
        Highlighter.apply(this);
        BehaviourListener.apply(this);

        this.redirector = new Redirector(this, process);
        this.redirector.redirectStreams();

        ProcessHelper.waitForProcessExit(this, process);
    }

    public void setTextType(int textType) {
        this.textType = textType;
    }

    public Redirector getRedirector() {
        return redirector;
    }

    public String getStyleClassForTextType() {
        return switch (textType) {
            case INPUT -> "console_input";
            case OUTPUT -> "console_output";
            default -> "console_error";
        };
    }

}
