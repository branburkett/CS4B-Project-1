package tictactoe;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MenuController {

    @FXML
    private Button button_Singleplayer;

    @FXML
    private Button button_LocalMultiplayer;

    @FXML
    private Button button_OnlineMultiplayer;

    @FXML
    void switchToSingleplayer(ActionEvent event) throws IOException {
        App.setRoot("singleplayer");
    }

    @FXML
    void switchToLocalMultiplayer(ActionEvent event) throws IOException {
        App.setRoot("local_multiplayer");
    }

    @FXML
    void switchToOnlineMultiplayer(ActionEvent event) throws IOException {
        App.setRoot("online_multiplayer");
    }

}
