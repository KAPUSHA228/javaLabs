package org.example.lab1new;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class StartWindowController {
    public Button startBtn;
    @FXML
    private TextField nameField;

    @FXML
    private void startBtnClicked(){
        nameField.setText("А  я не понял!!");
        System.out.println();
    }
}
