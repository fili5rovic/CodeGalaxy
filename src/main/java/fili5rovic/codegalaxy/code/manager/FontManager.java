package fili5rovic.codegalaxy.code.manager;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.UserPreferences;
import javafx.scene.input.ScrollEvent;


public class FontManager extends Manager {
    private static final int MIN_FONT_SIZE;
    private static final int MAX_FONT_SIZE;
    private int currentFontSize;


    static {
        MIN_FONT_SIZE = 10;
        MAX_FONT_SIZE = 40;
    }

    public FontManager(CodeGalaxy cg) {
        super(cg);
    }


    @Override
    public void init() {
        currentFontSize = UserPreferences.getInstance().getFontPreference();

        setupListener();
    }

    private void setupListener() {
        codeGalaxy.addEventFilter(ScrollEvent.SCROLL, event -> {
            if(event.isControlDown()) {
                if(event.getDeltaY() > 0)
                    increaseSize();
                else
                    decreaseSize();
                updateUI();
            }
        });
    }

    private void increaseSize() {
        if(currentFontSize < MAX_FONT_SIZE)
            currentFontSize++;
    }

    private void decreaseSize() {
        if(currentFontSize > MIN_FONT_SIZE)
            currentFontSize--;
    }

    private void updateUI() {
        codeGalaxy.setStyle("-fx-font-size: " + currentFontSize);
        System.out.println("Current font size: " + currentFontSize);
    }

    public static int getMaxFontSize() {
        return MAX_FONT_SIZE;
    }

    public static int getMinFontSize() {
        return MIN_FONT_SIZE;
    }




}
