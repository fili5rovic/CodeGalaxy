package fili5rovic.codegalaxy.window;


public class WindowHelper {
    public static void showWindow(int position) {
        if (position < 0 || position > Window.WINDOWS)
            return;
        Window.getWindowAt(position).getStage().show();
    }

    public static void showOnly(int position) {
        if (position < 0 || position > Window.WINDOWS)
            return;
        for (int i = 0; i < Window.WINDOWS; i++) {
            if (i != position)
                Window.getWindowAt(i).getStage().hide();
            else
                showWindow(position);
        }
    }

    public static void closeAllWindows() {
        for (int i = 0; i < Window.WINDOWS; i++) {
            hideWindow(i);
        }
    }

    public static void hideWindow(int position) {
        if (position < 0 || position > Window.WINDOWS)
            return;
        Window.getWindowAt(position).getStage().hide();
    }


}
