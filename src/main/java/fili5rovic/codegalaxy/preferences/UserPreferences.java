package fili5rovic.codegalaxy.preferences;

import java.io.*;
import java.util.Properties;

public class UserPreferences {
    private final Properties props = new Properties();
    private static UserPreferences instance;

    public static UserPreferences getInstance() {
        if (instance == null)
            instance = new UserPreferences();
        return instance;
    }

    private UserPreferences() {
        try {
            props.load(new FileReader("preferences"));
        } catch (FileNotFoundException e) {
            save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void set(String key, String value) {
        props.setProperty(key, value);
        save();
    }

    public String get(String key) {
        return props.getProperty(key);
    }

    public void save() {
        try (FileWriter writer = new FileWriter("preferences")) {
            props.store(writer, null);
        } catch (IOException e) {
            System.out.println("Couldn't save preferences");
        }
    }

}
