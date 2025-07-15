package fili5rovic.codegalaxy.settings;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class IDESettings {
    private final Properties props = new Properties();

    private static IDESettings instance;
    private static IDESettings recentInstance;

    /**
     * The path is from content root.
     */
    private String filePath;

    private static final String SETTINGS = ".settings/general.properties";
    private static final String TEMP_SETTINGS = ".settings/general.properties.tmp";
    private static final String RECENT_SETTINGS = ".settings/recent.properties";

    public static IDESettings getInstance() {
        if (instance == null) {
            instance = new IDESettings(SETTINGS);
        }
        return instance;
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

    public String[] getShortcutKeys() {
        return props.keySet().stream()
                .map(Object::toString)
                .filter(key -> key.startsWith("shortcut_"))
                .toArray(String[]::new);
    }


    public void save() {
        try (FileWriter writer = new FileWriter(filePath)) {
            props.store(writer, null);
        } catch (IOException e) {
            System.out.println("Couldn't save properties");
        }
    }

    /**
     * Copies the settings from the main properties file to a temporary file.
     * This is useful for applying changes later without modifying the original settings immediately.
     */
    public static void copySettingsToTemp(boolean skipIfExists) {
        Path settingsPath = Path.of(SETTINGS);
        Path tempSettingsPath = Path.of(TEMP_SETTINGS);

        if (skipIfExists && Files.exists(tempSettingsPath)) {
            System.out.println("Temporary settings file already exists, skipping copy.");
            return;
        }

        try {
            Files.copy(settingsPath, tempSettingsPath, StandardCopyOption.COPY_ATTRIBUTES);
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
            // so that props get created again
            instance = null;

            deleteTempSettings();
        } catch (IOException e) {
            System.out.println("Couldn't copy temp to properties: " + e.getMessage());
        }
    }

    public static void deleteTempSettings() {
        File tempFile = new File(TEMP_SETTINGS);
        if (tempFile.exists()) {
            if (!tempFile.delete()) {
                System.err.println("Failed to delete temporary settings file: " + TEMP_SETTINGS);
            }
        }
    }
}
