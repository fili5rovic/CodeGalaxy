package fili5rovic.codegalaxy;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.util.Objects;

public class CodeEditorApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        CodeGalaxy codeArea = new CodeGalaxy();
        codeArea.replaceText(0, 0, "// Your code here...\n");

        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

        VirtualizedScrollPane<CodeArea> vsPane = new VirtualizedScrollPane<>(codeArea);

        Tab codeTab = new Tab("Editor");
        codeTab.setContent(vsPane);
        codeTab.setClosable(false);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().add(codeTab);

        StackPane root = new StackPane(tabPane);
        Scene scene = new Scene(root, 800, 600);

        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/fili5rovic/codegalaxy/main-dark.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/fili5rovic/codegalaxy/codegalaxy-dark.css")).toExternalForm());

        primaryStage.setTitle("Code Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
