package fili5rovic.codegalaxy.code;

import fili5rovic.codegalaxy.code.manager.FontManager;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class UserPreferences {
    private final Properties props = new Properties();
    private static UserPreferences instance;

    private static final String fontKey = "fontSize";

    public static UserPreferences getInstance() {
        if (instance == null)
            instance = new UserPreferences();
        return instance;
    }

    private UserPreferences() {
        init();
    }


    public void init() {
        try {
            props.load(new FileReader("preferences"));
        } catch (FileNotFoundException e) {
            try {
                new FileWriter("preferences");
            } catch (IOException ex) {
                System.out.println("Couldn't create preferences");
            }

        } catch (IOException e) {
            System.out.println("Couldn't load preferences");
        }
        if(props.getProperty(fontKey) == null) {
            props.setProperty(fontKey, String.valueOf(FontManager.getMinFontSize()));
        }
    }

    public void setFontPreference(int size) {
        props.setProperty(fontKey, String.valueOf(size));
    }
    public int getFontPreference() {
        return Integer.parseInt(props.getProperty(fontKey));
    }

}
