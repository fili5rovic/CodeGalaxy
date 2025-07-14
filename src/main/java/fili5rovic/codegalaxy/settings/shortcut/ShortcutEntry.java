package fili5rovic.codegalaxy.settings.shortcut;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ShortcutEntry {
    private final String name;
    private final StringProperty value;

    public ShortcutEntry(String name, String value) {
        this.name = name;
        this.value = new SimpleStringProperty(value);
    }

    public String getName() {
        return name;
    }

    public StringProperty valueProperty() {
        return value;
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String newValue) {
        value.set(newValue);
    }
}
