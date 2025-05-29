package fili5rovic.codegalaxy.settings;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class IDESettings {
    private final Properties props = new Properties();

    private static IDESettings instance;
    private static IDESettings tempInstance;

    private String fileName;

    public static final String SETTINGS = "settings";
    public static final String TEMP_SETTINGS = "tempSettings";

    public static IDESettings getInstance() {
        if (instance == null) {

            instance = new IDESettings(SETTINGS);
        }
        return instance;
    }
    public static IDESettings getTempInstance() {
        if (tempInstance == null) {
            tempInstance = new IDESettings(TEMP_SETTINGS);
        }
        return tempInstance;
    }

    private IDESettings(String fileName) {
        try {
            this.fileName = fileName;
            props.load(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            save();  // creates a new file if not found
        } catch (IOException e) {
            throw new RuntimeException("Failed to load settings", e);
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

    public void save() {
        try (FileWriter writer = new FileWriter(fileName)) {
            props.store(writer, null);
        } catch (IOException e) {
            System.out.println("Couldn't save settings");
        }
    }

    public static void copySettingsToTemp() {
        try {
            String settingsStr = Files.readString(new File(SETTINGS).toPath());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_SETTINGS))) {
                writer.write(settingsStr);
            }
        } catch (IOException e) {
            System.out.println("Couldn't copy settings to temp: " + e.getMessage());
        }
    }

    public static void applyTempSettings() {
        try {
            String settingsStr = Files.readString(new File(TEMP_SETTINGS).toPath());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(SETTINGS))) {
                writer.write(settingsStr);
            }
        } catch (IOException e) {
            System.out.println("Couldn't copy temp to settings: " + e.getMessage());
        }
    }
}
