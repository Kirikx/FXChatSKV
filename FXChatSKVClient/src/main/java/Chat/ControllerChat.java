package Chat;


import Chat.Message.IMessageService;
import Chat.Message.ServerMessageService;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerChat implements Initializable {
    private IMessageService messageService;

    public @FXML TextArea MessageArea;
    public @FXML TextField Message;
    public @FXML Button Send;
    public @FXML VBox authPanel ;

    public @FXML TextField loginField;
    public @FXML PasswordField passField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            this.messageService = new ServerMessageService(this, true);
        } catch (Exception e) {
            showError(e);
        }
    }

    private void showError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Что-то пошло не так...");
        alert.setHeaderText(e.getMessage());

        VBox dialogPaneContent = new VBox();

        Label label = new Label("Stack Trace:");

//        String stackTrace = ExceptionUtils.getStackTrace(e);
        TextArea textArea = new TextArea();
//        textArea.setText(stackTrace);

        dialogPaneContent.getChildren().addAll(label, textArea);

        // Set content for Dialog Pane
        alert.getDialogPane().setContent(dialogPaneContent);
        alert.setResizable(true);
        alert.showAndWait();

        e.printStackTrace();
    }

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

//        Node source = (Node) e.getSource();
//        TextField tf = (TextField) source.getScene().lookup("#Message");

        if(Message.getText().length() > 0) {
//            TextArea ta = (TextArea) source.getScene().lookup("#MessageArea");
            MessageArea.appendText( "Я: " + Message.getText() + System.lineSeparator());
            messageService.sendMessage(Message.getText());
            Message.clear();
        }
        Message.requestFocus();
    }
    public void shutdown() {
        try {
            messageService.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void sendAuth(ActionEvent actionEvent) {
        String login = loginField.getText();
        String password = passField.getText();
        messageService.sendMessage(String.format("/auth %s %s", login, password));
    }
}
