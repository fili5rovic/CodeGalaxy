package fili5rovic.codegalaxy.console;

import fili5rovic.codegalaxy.console.behaviour.BehaviourListener;
import fili5rovic.codegalaxy.console.highlighter.Highlighter;
import org.fxmisc.richtext.CodeArea;

public class ConsoleArea extends CodeArea {

    public ConsoleArea(Process process) {
        Highlighter.apply(this);
        BehaviourListener.apply(this);

        Redirector redirector = new Redirector(this, process);
        redirector.redirectStreams();

    }
}
