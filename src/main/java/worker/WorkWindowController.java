package worker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class WorkWindowController extends Application {



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        //TODO: форма ввода, выбор групп, ключевые слова
        Parent root = FXMLLoader.load(getClass().getResource("/WorkWindow.fxml"));

        //Document document = Jsoup.connect("http://188.242.232.214:8080/city").get();
        //проверка
        String cityListJson ="[{\"id\":1,\"name\":\"Санкт-Петербург\"},{\"id\":2,\"name\":\"Санкт-Петербург и ЛО\"},{\"id\":3,\"name\":\"Москва\"}]";


        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
