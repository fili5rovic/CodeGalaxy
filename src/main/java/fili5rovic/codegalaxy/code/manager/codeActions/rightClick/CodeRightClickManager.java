package fili5rovic.codegalaxy.code.manager.codeActions.rightClick;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.Manager;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class CodeRightClickManager extends Manager {
    public CodeRightClickManager(CodeGalaxy cg) {
        super(cg);
    }

    @Override
    public void init() {
        codeGalaxy.setOnContextMenuRequested(e -> {
            int position = codeGalaxy.hit(e.getX(), e.getY()).getInsertionIndex();
            codeGalaxy.displaceCaret(position);
            codeGalaxy.moveTo(position);

            if(!codeGalaxy.hasSelection())
                codeGalaxy.selectWord();


            // here you should add fine-tuning, because
            // at the end of the word it goes to the next one using this method
            System.out.println("Right click on: " + codeGalaxy.getSelectedText());
        });
    }
}
