package worker;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class PushMessage extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    class WindowButtons extends HBox {

        public WindowButtons() {
            Button closeBtn = new Button("X");

            closeBtn.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent actionEvent) {
                    Platform.exit();
                }
            });

            this.getChildren().add(closeBtn);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.initStyle(StageStyle.UNDECORATED);

        BorderPane borderPane = new BorderPane();
        //borderPane.setStyle("-fx-background-color: green;");
        //Todo:почему тулбар крашиться на маке
        primaryStage.setScene(new Scene(borderPane, 300, 250));
        primaryStage.show();
    }
}
