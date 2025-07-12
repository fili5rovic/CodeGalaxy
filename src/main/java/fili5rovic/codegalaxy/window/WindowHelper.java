package fili5rovic.codegalaxy.window;


public class WindowHelper {
    public static void showWindow(int position) {
        if (position < 0 || position > Window.WINDOWS)
            return;
        Window.getWindowAt(position).getStage().show();
    }

    public static void hideWindow(int position) {
        if (position < 0 || position > Window.WINDOWS)
            return;
        Window.getWindowAt(position).getStage().hide();
    }


}
