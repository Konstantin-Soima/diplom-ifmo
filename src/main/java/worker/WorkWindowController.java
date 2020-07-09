package worker;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class WorkWindowController  {


    @FXML
    private ListView<String> cityList = new ListView<>();

    private ObservableList<String> observableList = FXCollections.observableArrayList("Санкт-Петербург", "Москва");



    public void initialize(){
        //TODO: форма ввода, выбор групп, ключевые слова

        cityList.setItems(observableList);
        System.out.println(cityList.getItems().size());

        //Document document = Jsoup.connect("http://188.242.232.214:8080/city").get();
        //проверка
        String cityListJson ="[{\"id\":1,\"name\":\"Санкт-Петербург\"},{\"id\":2,\"name\":\"Санкт-Петербург и ЛО\"},{\"id\":3,\"name\":\"Москва\"}]";


    }
    void initData() {
    }
}
