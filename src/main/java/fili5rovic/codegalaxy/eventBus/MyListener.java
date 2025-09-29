package fili5rovic.codegalaxy.eventBus;

import fili5rovic.codegalaxy.eventBus.myEvents.MyEvent;

public interface MyListener {

    void handle(MyEvent e);
}
