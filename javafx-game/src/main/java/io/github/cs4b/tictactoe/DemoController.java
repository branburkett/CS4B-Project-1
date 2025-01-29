package io.github.cs4b.tictactoe;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
//import javafx.scene.control.TextField;
import com.gluonhq.charm.glisten.control.TextField;


public class DemoController {

    @FXML
    private Button demoButton;
    @FXML
    private com.gluonhq.charm.glisten.control.TextField demoTextField;

    @FXML
    private Label demoLabel;
    @FXML
    private CheckBox demoCheckBox;
    @FXML
    private Slider demoSlider;

    public void initialize() {
        demoSlider.setValue(50); // Set default slider value
    }

    @FXML
    public void handleButtonAction() {
        System.out.println("TextField Value: " + demoTextField.getText());
        demoLabel.setText("Hello, " + demoTextField.getText());
    }

    @FXML
    public void handleCheckBoxAction() {
        if (demoCheckBox.isSelected()) {
            demoLabel.setText("Checkbox is selected");
        } else {
            demoLabel.setText("Checkbox is not selected");
        }
    }

    @FXML
    public void handleSliderAction() {
        demoLabel.setText("Slider Value: " + (int) demoSlider.getValue());
    }
}