package fili5rovic.codegalaxy.code.manager;

import fili5rovic.codegalaxy.code.CodeGalaxy;

public abstract class Manager {
    protected CodeGalaxy codeGalaxy;

    public Manager(CodeGalaxy cg) {
        this.codeGalaxy = cg;
    }

    public abstract void init();
}
