package Chat;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class Controller {
    @FXML
    private void clickButton (MouseEvent e) {
        messageStack(e);
    }

    @FXML
    private void pressEnter(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            messageStack(e);
        }
    }

    private void messageStack(Event e) {
        Node source = (Node) e.getSource();
        TextField tf = (TextField) source.getScene().lookup("#Message");

        if(tf.getText().length() > 0) {
            TextArea ta = (TextArea) source.getScene().lookup("#MessageArea");
            ta.appendText(tf.getText() + "\n");
            tf.clear();
        }

        tf.requestFocus();
    }
}
