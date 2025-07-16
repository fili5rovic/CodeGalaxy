package fili5rovic.codegalaxy.util;

import fili5rovic.codegalaxy.Main;
import javafx.scene.text.Font;

import java.io.InputStream;

public class FontUtil {

    private static final String FONT_JETBRAINS_MONO = "/fili5rovic/codegalaxy/fonts/JetBrainsMono-Medium.ttf";

    public static void loadFonts() {
        loadJetBrainsMono();
    }

    private static void loadJetBrainsMono() {
        InputStream jetbrains = Main.class.getResourceAsStream(FONT_JETBRAINS_MONO);
        // size doesn't matter here, just loading the font
        Font.loadFont(jetbrains, 12);
    }
}
