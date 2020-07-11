package worker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.Category;
import common.City;
import javafx.application.Application;
import javafx.application.Platform;
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

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
        ObjectMapper objectMapper = new ObjectMapper();
        List<City> listCity = objectMapper.readValue(cityListJson, new TypeReference<List<City>>() {
        });
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
                    ObjectMapper objectMapper2 = new ObjectMapper();

                    List<Category> listCategory = objectMapper2.readValue(categoryListJson, new TypeReference<List<Category>>() {
                    });
                    observableList2 = FXCollections.observableArrayList(listCategory);
                    categoryList.setItems(observableList2);
                    categoryList.setCellFactory(param -> new ListCell<Category>() {
                        @Override
                        protected void updateItem(Category p, boolean empty) {
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
        try (InputStream input = new FileInputStream("find.properties")){
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
        //Убиваем javafx
        Stage thisWindow = (Stage) buttonOk.getScene().getWindow();
        thisWindow.close();
        //Platform.setImplicitExit(false);
       // Platform.exit();
        /*
        try {
            restartApplication();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.out.println("Не получилось взять путь");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Не получилось запустить");
        }*/

    }

    public void restartApplication() throws URISyntaxException, IOException {
        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        final File currentJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        System.out.println(javaBin);
        System.out.println(currentJar.getPath());
        /* is it a jar file? */
        if(!currentJar.getName().endsWith(".jar")) {
            System.out.println("это джар");
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("/Library/Java/JavaVirtualMachines/jdk-13.0.2.jdk/Contents/Home/bin/java \"-javaagent:/Applications/IntelliJ IDEA CE.app/Contents/lib/idea_rt.jar=55266:/Applications/IntelliJ IDEA CE.app/Contents/bin\" -Dfile.encoding=UTF-8 -classpath /Users/konstantin/IdeaProjects/diplom-ifmo/target/classes:/Users/konstantin/.m2/repository/org/postgresql/postgresql/42.2.12/postgresql-42.2.12.jar:/Users/konstantin/.m2/repository/org/openjfx/javafx-controls/15-ea+6/javafx-controls-15-ea+6.jar:/Users/konstantin/.m2/repository/org/openjfx/javafx-controls/15-ea+6/javafx-controls-15-ea+6-mac.jar:/Users/konstantin/.m2/repository/org/openjfx/javafx-graphics/15-ea+6/javafx-graphics-15-ea+6.jar:/Users/konstantin/.m2/repository/org/openjfx/javafx-graphics/15-ea+6/javafx-graphics-15-ea+6-mac.jar:/Users/konstantin/.m2/repository/org/openjfx/javafx-base/15-ea+6/javafx-base-15-ea+6.jar:/Users/konstantin/.m2/repository/org/openjfx/javafx-base/15-ea+6/javafx-base-15-ea+6-mac.jar:/Users/konstantin/.m2/repository/org/openjfx/javafx-fxml/15-ea+6/javafx-fxml-15-ea+6.jar:/Users/konstantin/.m2/repository/org/openjfx/javafx-fxml/15-ea+6/javafx-fxml-15-ea+6-mac.jar:/Users/konstantin/.m2/repository/org/seleniumhq/selenium/selenium-java/3.141.59/selenium-java-3.141.59.jar:/Users/konstantin/.m2/repository/org/seleniumhq/selenium/selenium-api/3.141.59/selenium-api-3.141.59.jar:/Users/konstantin/.m2/repository/org/seleniumhq/selenium/selenium-edge-driver/3.141.59/selenium-edge-driver-3.141.59.jar:/Users/konstantin/.m2/repository/org/seleniumhq/selenium/selenium-firefox-driver/3.141.59/selenium-firefox-driver-3.141.59.jar:/Users/konstantin/.m2/repository/org/seleniumhq/selenium/selenium-ie-driver/3.141.59/selenium-ie-driver-3.141.59.jar:/Users/konstantin/.m2/repository/org/seleniumhq/selenium/selenium-opera-driver/3.141.59/selenium-opera-driver-3.141.59.jar:/Users/konstantin/.m2/repository/org/seleniumhq/selenium/selenium-remote-driver/3.141.59/selenium-remote-driver-3.141.59.jar:/Users/konstantin/.m2/repository/org/seleniumhq/selenium/selenium-safari-driver/3.141.59/selenium-safari-driver-3.141.59.jar:/Users/konstantin/.m2/repository/org/seleniumhq/selenium/selenium-support/3.141.59/selenium-support-3.141.59.jar:/Users/konstantin/.m2/repository/net/bytebuddy/byte-buddy/1.8.15/byte-buddy-1.8.15.jar:/Users/konstantin/.m2/repository/org/apache/commons/commons-exec/1.3/commons-exec-1.3.jar:/Users/konstantin/.m2/repository/com/google/guava/guava/25.0-jre/guava-25.0-jre.jar:/Users/konstantin/.m2/repository/com/google/code/findbugs/jsr305/1.3.9/jsr305-1.3.9.jar:/Users/konstantin/.m2/repository/org/checkerframework/checker-compat-qual/2.0.0/checker-compat-qual-2.0.0.jar:/Users/konstantin/.m2/repository/com/google/errorprone/error_prone_annotations/2.1.3/error_prone_annotations-2.1.3.jar:/Users/konstantin/.m2/repository/com/google/j2objc/j2objc-annotations/1.1/j2objc-annotations-1.1.jar:/Users/konstantin/.m2/repository/org/codehaus/mojo/animal-sniffer-annotations/1.14/animal-sniffer-annotations-1.14.jar:/Users/konstantin/.m2/repository/com/squareup/okhttp3/okhttp/3.11.0/okhttp-3.11.0.jar:/Users/konstantin/.m2/repository/com/squareup/okio/okio/1.14.0/okio-1.14.0.jar:/Users/konstantin/.m2/repository/org/seleniumhq/selenium/selenium-chrome-driver/3.141.59/selenium-chrome-driver-3.141.59.jar:/Users/konstantin/.m2/repository/io/github/bonigarcia/webdrivermanager/3.8.1/webdrivermanager-3.8.1.jar:/Users/konstantin/.m2/repository/org/slf4j/slf4j-api/1.7.25/slf4j-api-1.7.25.jar:/Users/konstantin/.m2/repository/commons-io/commons-io/2.6/commons-io-2.6.jar:/Users/konstantin/.m2/repository/com/google/code/gson/gson/2.8.5/gson-2.8.5.jar:/Users/konstantin/.m2/repository/org/apache/commons/commons-lang3/3.8.1/commons-lang3-3.8.1.jar:/Users/konstantin/.m2/repository/org/apache/httpcomponents/httpclient/4.5.6/httpclient-4.5.6.jar:/Users/konstantin/.m2/repository/org/apache/httpcomponents/httpcore/4.4.10/httpcore-4.4.10.jar:/Users/konstantin/.m2/repository/commons-logging/commons-logging/1.2/commons-logging-1.2.jar:/Users/konstantin/.m2/repository/commons-codec/commons-codec/1.10/commons-codec-1.10.jar:/Users/konstantin/.m2/repository/org/rauschig/jarchivelib/1.0.0/jarchivelib-1.0.0.jar:/Users/konstantin/.m2/repository/org/apache/commons/commons-compress/1.18/commons-compress-1.18.jar:/Users/konstantin/.m2/repository/org/jsoup/jsoup/1.13.1/jsoup-1.13.1.jar:/Users/konstantin/.m2/repository/javax/persistence/javax.persistence-api/2.2/javax.persistence-api-2.2.jar:/Users/konstantin/.m2/repository/org/eclipse/persistence/eclipselink/2.7.7/eclipselink-2.7.7.jar:/Users/konstantin/.m2/repository/org/eclipse/persistence/jakarta.persistence/2.2.3/jakarta.persistence-2.2.3.jar:/Users/konstantin/.m2/repository/org/eclipse/persistence/commonj.sdo/2.1.1/commonj.sdo-2.1.1.jar:/Users/konstantin/.m2/repository/org/eclipse/persistence/org.eclipse.persistence.jpa.modelgen.processor/2.7.7/org.eclipse.persistence.jpa.modelgen.processor-2.7.7.jar:/Users/konstantin/.m2/repository/org/eclipse/persistence/org.eclipse.persistence.core/2.7.7/org.eclipse.persistence.core-2.7.7.jar:/Users/konstantin/.m2/repository/org/eclipse/persistence/org.eclipse.persistence.asm/2.7.7/org.eclipse.persistence.asm-2.7.7.jar:/Users/konstantin/.m2/repository/org/eclipse/persistence/org.eclipse.persistence.jpa/2.7.7/org.eclipse.persistence.jpa-2.7.7.jar:/Users/konstantin/.m2/repository/org/eclipse/persistence/org.eclipse.persistence.antlr/2.7.7/org.eclipse.persistence.antlr-2.7.7.jar:/Users/konstantin/.m2/repository/org/eclipse/persistence/org.eclipse.persistence.jpa.jpql/2.7.7/org.eclipse.persistence.jpa.jpql-2.7.7.jar:/Users/konstantin/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.11.1/jackson-databind-2.11.1.jar:/Users/konstantin/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.11.1/jackson-annotations-2.11.1.jar:/Users/konstantin/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.11.1/jackson-core-2.11.1.jar worker.Main");
            System.out.println(proc.pid());
            System.exit(0);
            return;
        }
        System.out.println("Выход");
        System.exit(0);
    }
}
