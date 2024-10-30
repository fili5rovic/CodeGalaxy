package fili5rovic.codegalaxy.code;

import fili5rovic.codegalaxy.code.manager.Manager;
import org.fxmisc.richtext.CodeArea;

import java.util.ArrayList;

public class CodeGalaxy extends CodeArea {
    private final ArrayList<Manager> managers = new ArrayList<>();

    public CodeGalaxy() {
        onCreate();
    }

    private void onCreate() {
        
    }
}
