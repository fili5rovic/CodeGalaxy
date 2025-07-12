package fili5rovic.codegalaxy.controller;

public class Controllers {
    private static DashboardController dashboardController;
    private static SettingsController settingsController;

    public static DashboardController dashboardController() {
        if (dashboardController == null) {
            throw new IllegalStateException("DashboardController is not initialized.");
        }
        return dashboardController;
    }

    public static SettingsController settingsController() {
        if (settingsController == null) {
            throw new IllegalStateException("SettingsController is not initialized.");
        }
        return settingsController;
    }

    public static void setDashboardController(DashboardController _dashboardController) {
        dashboardController = _dashboardController;
    }

    public static void setSettingsController(SettingsController _settingsController) {
        settingsController = _settingsController;
    }
}
