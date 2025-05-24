package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.fileFinder.FileFinderPopup;

public class FileFinder {

    private static FileFinder instance;

    private FileFinderPopup popup;

    private FileFinder() {
        // private constructor to prevent instantiation
        popup = new FileFinderPopup();
    }

    public static FileFinder getInstance() {
        if (instance == null) {
            instance = new FileFinder();
        }
        return instance;
    }

    public FileFinderPopup popup() {
        return popup;
    }
}
