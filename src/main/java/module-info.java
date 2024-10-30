module fili5rovic.codegalaxy {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;
    requires org.fxmisc.flowless;


    opens fili5rovic.codegalaxy to javafx.fxml;
    exports fili5rovic.codegalaxy;
}