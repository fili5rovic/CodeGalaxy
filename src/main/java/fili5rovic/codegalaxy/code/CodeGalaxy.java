package fili5rovic.codegalaxy.code;

import fili5rovic.codegalaxy.code.manager.FontManager;
import fili5rovic.codegalaxy.code.manager.Manager;
import org.fxmisc.richtext.CodeArea;

import java.util.ArrayList;

public class CodeGalaxy extends CodeArea {
    private final ArrayList<Manager> managers = new ArrayList<>();

    public CodeGalaxy() {
        onCreate();
    }

    private void onCreate() {
        addManagers();
        initManagers();
    }

    private void addManagers() {
        managers.add(new FontManager(this));
    }

    private void initManagers() {
        for(Manager m : managers)
            m.init();
    }
}
