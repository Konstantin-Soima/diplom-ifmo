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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class WorkWindowController  {

    @FXML
    private ListView<Category> categoryList = new ListView<>();

    @FXML
    private ListView<City> cityList = new ListView<>();

    @FXML
    TextArea textAreaKeyword = new TextArea();

    @FXML
    TextField textFieldTitle = new TextField();

    @FXML
    DatePicker datePickerCalendar = new DatePicker();

    @FXML
    private Button buttonOk;

    @FXML
    private Label attentionMessage;

    private ObservableList<City> observableList ;
    private ObservableList<Category> observableList2 ;



    public void initialize() throws IOException {
        //города
        String cityListJson = Jsoup.connect("http://188.242.232.214:8080/city").ignoreContentType(true).execute().body();
        System.out.println(cityListJson);
        ObjectMapper objectMapper = new ObjectMapper();
        List<City> listCity = objectMapper.readValue(cityListJson, new TypeReference<List<City>>() {
        });
        System.out.println(listCity.size());
        observableList = FXCollections.observableArrayList(listCity);
        cityList.setItems(observableList);
        //выбор группы при выборе города
        cityList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<City>() {
            @Override
            public void changed(ObservableValue<? extends City> observable, City oldValue, City newValue) {
                // Получаем список категорий
                System.out.println("Selected item: " + newValue);
                try {
                    String categoryListJson = Jsoup.connect("http://188.242.232.214:8080/categoryByCity/" + newValue.getId()).ignoreContentType(true).execute().body();
                    System.out.println(cityListJson);
                    ObjectMapper objectMapper2 = new ObjectMapper();

                    List<Category> listCategory = objectMapper2.readValue(categoryListJson, new TypeReference<List<Category>>() {
                    });
                    observableList2 = FXCollections.observableArrayList(listCategory);
                    categoryList.setItems(observableList2);
                    categoryList.setCellFactory(param -> new ListCell<Category>() {
                        @Override
                        protected void updateItem(Category p, boolean empty) {
                            System.out.println(p);
                            super.updateItem(p, empty);
                            if (empty || p == null || p.getName() == null) {
                                setText("");
                            } else {
                                setText(p.getName()); //вывожу просто название группы
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
    void initData() {
    }

    //Сохранение параметров поиска в файл
    @FXML
    private void buttonOkClick(ActionEvent event) {
        event.consume();
        System.out.println("Кнопка нажалась");
        //TODO: Чек, что все поля заполнены
        if (textFieldTitle.getText()==null || textFieldTitle.getText().trim().isEmpty()){
            attentionMessage.setText("Вы не указали имя вещи");
            return;
        }
        if (textAreaKeyword.getText()==null || textAreaKeyword.getText().trim().isEmpty()){
            attentionMessage.setText("Вы не указали ключевые слова для поиска");
            return;
        }
        if (cityList.getSelectionModel().getSelectedItems().size()==0){
            attentionMessage.setText("Вы не выбрали город ");
            return;
        }
        if (categoryList.getSelectionModel().getSelectedItems().size()==0){
            attentionMessage.setText("Вы не выбрали категорию поиска ");
            return;
        }
        if (datePickerCalendar.getValue()==null){
            attentionMessage.setText("Вы не указали дату");
            return;
        }
        //Файл свойств в корне
        Properties properties = new Properties();
        try (InputStream input = WorkWindowController.class.getClassLoader().getResourceAsStream("find.properties")){
            properties.load(input);
        } catch (IOException e){
            e.printStackTrace();
        }
        //Пишим параметры
        Integer cityId = cityList.getSelectionModel().getSelectedItem().getId();
        properties.setProperty("city",cityId.toString());
        Integer categoryId = categoryList.getSelectionModel().getSelectedItem().getId();
        properties.setProperty("category",categoryId.toString());
        properties.setProperty("keywords",textAreaKeyword.getText());
        properties.setProperty("title",textFieldTitle.getText());
        properties.setProperty("datestart",datePickerCalendar.getValue().toString());
        try {
            properties.store(new FileWriter("find.properties"),null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
