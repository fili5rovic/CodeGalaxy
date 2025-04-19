package fili5rovic.codegalaxy.preferences;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class UserPreferences {
    private static final String PREFERENCES_FILE = "preferences";
    private final Properties props = new Properties();
    private static UserPreferences instance;

    public static UserPreferences getInstance() {
        if (instance == null) {
            instance = new UserPreferences();
        }
        return instance;
    }

    private UserPreferences() {
        try {
            props.load(new FileReader(PREFERENCES_FILE));
        } catch (FileNotFoundException e) {
            save();  // Creates file if not found
        } catch (IOException e) {
            throw new RuntimeException("Failed to load preferences", e);
        }
    }

    public void addTo(String key, String value) {
        List<String> currentValues = getMultiple(key);
        if (!currentValues.contains(value)) {
            currentValues.add(value);
            setMultiple(key, currentValues);
        }
    }

    public void removeFrom(String key, String value) {
        List<String> currentValues = getMultiple(key);
        if (currentValues.remove(value)) {
            setMultiple(key, currentValues);
        }
    }

    public void setMultiple(String key, List<String> values) {
        String joined = String.join("|", values);
        set(key, joined);
    }

    public List<String> getMultiple(String key) {
        String value = props.getProperty(key);
        if (value == null || value.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(value.split("\\|")));
    }

    public void set(String key, String value) {
        props.setProperty(key, value);
        save();
    }

    public String get(String key) {
        return props.getProperty(key);
    }

    public void remove(String key) {
        props.remove(key);
        save();
    }

    public void save() {
        try (FileWriter writer = new FileWriter(PREFERENCES_FILE)) {
            props.store(writer, null);
        } catch (IOException e) {
            System.out.println("Couldn't save preferences");
        }
    }
}
