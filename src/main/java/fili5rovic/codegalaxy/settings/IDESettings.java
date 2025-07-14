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
    private static IDESettings recentInstance;

    /**
     * The path is from content root.
     */
    private String filePath;

    private static final String SETTINGS = ".settings/general.properties";
    private static final String TEMP_SETTINGS = ".settings/general.properties.tmp";
    private static final String RECENT_SETTINGS = ".settings/general.properties.recent";

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

    public static IDESettings getRecentInstance() {
        if (recentInstance == null) {
            recentInstance = new IDESettings(RECENT_SETTINGS);
        }
        return recentInstance;
    }

    private IDESettings(String filePath) {
        try {
            this.filePath = filePath;
            props.load(new FileReader(this.filePath));
        } catch (FileNotFoundException e) {
            save();  // creates a new file if not found
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties", e);
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
        try (FileWriter writer = new FileWriter(filePath)) {
            props.store(writer, null);
        } catch (IOException e) {
            System.out.println("Couldn't save properties");
        }
    }

    public static void copySettingsToTemp() {
        try {
            String settingsStr = Files.readString(new File(SETTINGS).toPath());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_SETTINGS))) {
                writer.write(settingsStr);
            }
        } catch (IOException e) {
            System.out.println("Couldn't copy properties to temp: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void applyTempSettings() {
        try {
            String settingsStr = Files.readString(new File(TEMP_SETTINGS).toPath());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(SETTINGS))) {
                writer.write(settingsStr);
            }
        } catch (IOException e) {
            System.out.println("Couldn't copy temp to properties: " + e.getMessage());
        }
    }
}
