package fili5rovic.codegalaxy.code.manager.format;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.Manager;
import fili5rovic.codegalaxy.lsp.LSP;
import javafx.application.Platform;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.nio.file.Files;

public class FormatManager extends Manager {

    public FormatManager(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    public void init() {
        codeGalaxy.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.isAltDown() && event.getCode() == javafx.scene.input.KeyCode.L) {
                formatDocument();
                event.consume();
            }
        });
    }


    private void formatDocument() {
        LSP.instance().formatDocument(codeGalaxy.getFilePath().toString())
                .thenAccept(edits -> {
                    Platform.runLater(() -> {
//                        for (TextEdit edit : edits) {
//                            if(edit.getNewText().isEmpty())
//                                continue;
//                            Range range = edit.getRange();
//                            int startLine = range.getStart().getLine();
//                            int startChar = range.getStart().getCharacter();
//                            int endLine = range.getEnd().getLine();
//                            int endChar = range.getEnd().getCharacter();
//
//                            String currentText = codeGalaxy.getText(startLine, startChar, endLine, endChar);  // Assuming you have a method to get the current text in the range
//
//                            System.out.println("Current text from " + startLine + ":" + startChar + " to " + endLine + ":" + endChar);
//                            System.out.println("Current text: " + currentText);
//                            System.out.println("New text: " + edit.getNewText());
//
//                            codeGalaxy.replaceText(startLine, startChar, endLine, endChar, edit.getNewText() + currentText);
//                        }

// Clear the text

                        try {
                            codeGalaxy.replaceText(0,codeGalaxy.getLength() - 1, Files.readString(codeGalaxy.getFilePath()));
                            codeGalaxy.selectWord();
                        } catch (IOException e) {
                            System.out.println("Error reading file: " + e.getMessage());
                        }
                    });
                });
    }

}
