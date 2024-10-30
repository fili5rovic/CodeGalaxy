package fili5rovic.codegalaxy.code.manager;

public abstract class Manager {
    protected boolean isEnabled = false;

    public abstract void init();

    protected void enable() {
        isEnabled = true;
    }

    protected void disable() {
        isEnabled = false;
    }


}
