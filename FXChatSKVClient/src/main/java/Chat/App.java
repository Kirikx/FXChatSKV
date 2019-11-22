package Chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("FXChatSKV");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/form.fxml"));
        Parent root = loader.load();
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root));

        ControllerChat controller = loader.getController();
        primaryStage.setOnHidden(e -> controller.shutdown());

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
