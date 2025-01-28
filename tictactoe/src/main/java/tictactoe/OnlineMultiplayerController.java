package tictactoe;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class OnlineMultiplayerController {

    @FXML
    private Button button_MainMenu;

    @FXML
    void switchToMainMenu(ActionEvent event) throws IOException {
        App.setRoot("menu");
    }

}
