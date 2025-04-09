module fili5rovic.codegalaxy {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;
    requires org.fxmisc.flowless;
    requires com.github.javaparser.core;
    requires org.eclipse.lsp4j;
    requires org.eclipse.lsp4j.jsonrpc;


    opens fili5rovic.codegalaxy to javafx.fxml;
    exports fili5rovic.codegalaxy;
    exports fili5rovic.codegalaxy.controller;
    exports fili5rovic.codegalaxy.lsp;
    opens fili5rovic.codegalaxy.controller to javafx.fxml;
}