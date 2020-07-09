package worker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.Category;
import common.City;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

public class WorkWindowController  {

    @FXML
    private ListView<Category> categoryList = new ListView<>();

    @FXML
    private ListView<City> cityList = new ListView<>();

    private ObservableList<City> observableList ;
    private ObservableList<Category> observableList2 ;



    public void initialize() throws IOException {
        //TODO: форма ввода, выбор групп, ключевые слова
        //города
        String cityListJson = Jsoup.connect("http://188.242.232.214:8080/city").ignoreContentType(true).execute().body();
        System.out.println(cityListJson);
        ObjectMapper objectMapper = new ObjectMapper();
        List<City> listCity = objectMapper.readValue(cityListJson, new TypeReference<List<City>>(){});
        System.out.println(listCity.size());
        observableList = FXCollections.observableArrayList(listCity);
        cityList.setItems(observableList);
        /*cityList.setCellFactory(param -> new ListCell<City>() { //Код для смены выводимых значений
            @Override
            protected void updateItem(City p, boolean empty){
                super.updateItem(p, empty);
                if(empty || p == null || p.getName() == null){
                    setText("");
                }
                else{
                    setText(p.getName()); //вывожу просто имя города
                }
            }
        });*/
        //выбор файла при выборе
        cityList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<City>() {
            @Override
            public void changed(ObservableValue<? extends City> observable, City oldValue, City newValue) {
                // Получаем список категорий
                System.out.println("Selected item: " + newValue);
                String categoryListJson ="[{\"id\":1,\"name\":\"Музыкальные инструменты\"},{\"id\":2,\"name\":\"Для мтудии и концертов\"},{\"id\":3,\"name\":\"Духовые\"}]";
                ObjectMapper objectMapper2 = new ObjectMapper();
                try {
                    List<Category> listCategory = objectMapper2.readValue(categoryListJson, new TypeReference<List<Category>>(){});
                    observableList2 = FXCollections.observableArrayList(listCategory);
                    categoryList.setItems(observableList2);
                    /*categoryList.setCellFactory(param -> new ListCell<Category>() {
                        @Override
                        protected void updateItem(Category p, boolean empty){
                            super.updateItem(p, empty);
                            if(empty || p == null || p.getName() == null){
                                setText("");
                            }
                            else{
                                setText(p.getName()); //вывожу просто имя города
                            }
                        }
                    });*/
                } catch (Exception e) {
                e.printStackTrace();
            }
            }
        });

    }
    void initData() {
    }
}
