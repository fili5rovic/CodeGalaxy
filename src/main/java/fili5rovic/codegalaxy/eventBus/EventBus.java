package fili5rovic.codegalaxy.eventBus;

import fili5rovic.codegalaxy.eventBus.myEvents.MyEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class EventBus {

    private static EventBus instance;

    private final HashMap<Class<? extends MyEvent>, ArrayList<MyListener>> map = new HashMap<>();

    public EventBus instance() {
        if(instance == null)
            instance = new EventBus();

        return instance;
    }

    private EventBus() {}

    public void register(MyListener listener, Class<? extends MyEvent> c) {
        map.computeIfAbsent(c, _ -> new ArrayList<>()).add(listener);
    }

    public void publish(MyEvent e) {
        ArrayList<MyListener> myListeners = map.get(e.getClass());
        for (var l: myListeners) {
            l.handle(e);
        }
    }


}
