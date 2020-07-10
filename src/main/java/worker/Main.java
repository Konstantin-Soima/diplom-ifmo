package worker;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {

        //1. создаём иконку в трее
        TrayIcon trayIcon = null;
        if (SystemTray.isSupported()) { //Проверка наличия трея (в линуксе с Deepin, LXDM может и не быть)
            SystemTray tray = SystemTray.getSystemTray();

            //События для трея
            ActionListener listenerExit = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            };
            ActionListener listenerWatch = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //TODO: просмотр совпадений
                }
            };
            ActionListener listenerAdd = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Открыть форму добавление поиска
                    //WorkWindowController newWindow = new WorkWindowController();
                    //WorkWindowController.main(args);
                    FXMLLoader fxmlLoader = new FXMLLoader();

                    Platform.startup(() ->
                    {
                    try {

                        fxmlLoader.setLocation(getClass().getResource("/WorkWindow.fxml"));
                        //WorkWindowController workWindowController=new WorkWindowController();
                        //fxmlLoader.setController(workWindowController);
                        Parent root  = fxmlLoader.load();
                        Stage stage = new Stage();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();
                        System.out.println("закончился показ окна");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                }
            };
            PopupMenu popup = new PopupMenu();
            MenuItem addItem = new MenuItem("Добавить");
            addItem.addActionListener(listenerAdd);
            popup.add(addItem);
            MenuItem watchItem = new MenuItem("Просмотр совпадений");
            addItem.addActionListener(listenerWatch);
            popup.add(watchItem);
            MenuItem exitItem = new MenuItem("Выход");
            exitItem.addActionListener(listenerExit);
            popup.add(exitItem);
            String currentOs = System.getProperty("os.name");
            Image image;
            if (currentOs.startsWith("Windows")) {
                Image trayIconImage = Toolkit.getDefaultToolkit().getImage("sources/find.png");
                int trayIconWidth = new TrayIcon(trayIconImage).getSize().width;
                trayIcon = new TrayIcon(trayIconImage.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH), "Поиск сомнительных обьявлений", popup);
            }
            else {
                image = Toolkit.getDefaultToolkit().getImage("sources/tenor.gif"); //иконка в трей
                trayIcon = new TrayIcon(image, "Поиск сомнительных обьявлений", popup);
            }
            trayIcon.addActionListener(listenerAdd);
            trayIcon.addActionListener(listenerWatch);
            trayIcon.addActionListener(listenerExit);

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println(e);
            }
        } else {

        }
        //2. получаем из настроек мониторимый товар
        Properties properties = new Properties();
        try (InputStream input = WorkWindowController.class.getClassLoader().getResourceAsStream("find.properties")){
            properties.load(input);
        } catch (IOException e){
            e.printStackTrace();
        } //TODO: проверка на наличие настроек, если нет
        String title = properties.getProperty("title");
        String[] keyword = properties.getProperty("keywords").split(",");
        
        String[] groups = {};//{"https://www.avito.ru/sankt-peterburg/muzykalnye_instrumenty/dlya_studii_i_kontsertov-ASgBAgICAUTEAsgK"}
        DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate ld = LocalDate.parse(properties.getProperty("datestart"), DATEFORMATTER);
        //3. Создаём поисковик для ключевого слова в каждой группу
        for (String groupName: groups) {
            Finder testFind = new Finder(groupName,keyword);
            testFind.start();
        }
        //5. таймер раз в час

    }

}
